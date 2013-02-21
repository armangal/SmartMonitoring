package com.smexec.monitor.server.tasks;

import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;

import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.utils.JMXGetGCStat;
import com.smexec.monitor.shared.ChannelChunkStats;
import com.smexec.monitor.shared.ChannelSeverStats;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.GCHistory;
import com.smexec.monitor.shared.PoolsFeed;

public class StateUpdaterThread
    implements Runnable {

    @Override
    public void run() {
        try {
            ConnectionSynch.connectionLock.lock();

            System.out.println("Refreshing stats");
            ArrayList<ConnectedServer> serversList = new ArrayList<ConnectedServer>(0);
            for (ServerStataus ss : ConnectedServersState.getMap().values()) {
                ServerConfig sc = ss.getServerConfig();
                if (ss.isConnected()) {
                    try {
                        getMemoryStats(ss);
                        getSmartThreadPoolStats(ss);
                        getChannelStatistics(ss);

                        ss.setFirstTimeAccess(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ConnectedServer cs = new ConnectedServer(sc.getName(),
                                                         sc.getServerCode(),
                                                         sc.getIp(),
                                                         sc.getJmxPort(),
                                                         ss.isConnected(),
                                                         ss.getLastMemoryUsage(),
                                                         ss.getLastGCHistory(),
                                                         ss.getUpTime(),
                                                         ss.haveChannelSeverStats() ? ss.getChannelSeverStats() : null);
                serversList.add(cs);
            }

            // finished querying all connected servers.
            ConnectedServersState.mergeStats(serversList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionSynch.connectionLock.unlock();
        }
    }

    /**
     * in case the server is channel server, we will collect statistics from it
     * 
     * @param ss
     * @throws IOException
     * @throws MalformedObjectNameException
     * @throws AttributeNotFoundException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     */
    private void getChannelStatistics(ServerStataus ss) {

        try {
            JMXConnector jmxConnector = ss.getConnector();
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            ObjectName sessionStats = new ObjectName("com.playtech.poker.jmx:type=Stats,name=ChannelServerStats");
            if (mbsc.isRegistered(sessionStats)) {
                // channel server
                ChannelSeverStats stats = ss.getChannelSeverStats();
                CompositeData[] data;
                if (ss.isFirstTimeAccess()) {
                    // load all stats
                    data = (CompositeData[]) mbsc.getAttribute(sessionStats, "ServerStats");

                } else {
                    // load delta
                    long startTime = stats.getLastChunk().getStartTime();
                    data = (CompositeData[]) mbsc.invoke(sessionStats, "getLastServerStats", new Object[] {startTime}, new String[] {"long"});
                }
                System.out.println("got :" + data.length + " chunks from channel");
                for (CompositeData cd : data) {
                    ChannelChunkStats cscs = new ChannelChunkStats(getAtributeFromComposite(cd, "connectedBinarySessions"),
                                                                   getAtributeFromComposite(cd, "connectedLegacySessions"),
                                                                   getAtributeFromComposite(cd, "disconnectedBinarySessions"),
                                                                   getAtributeFromComposite(cd, "disconnectedLegacySessions"),
                                                                   getAtributeFromComposite(cd, "startTime"),
                                                                   getAtributeFromComposite(cd, "endTime"),
                                                                   getAtributeFromComposite(cd, "totalBinarySessions"),
                                                                   getAtributeFromComposite(cd, "totalLegacySessions"),
                                                                   -1);

                    stats.addChunk(cscs);
                    System.out.println(cscs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer getAtributeFromComposite(CompositeData cd, String name) {
        try {
            return Integer.valueOf(cd.get(name).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void getSmartThreadPoolStats(ServerStataus ss)
        throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, MalformedObjectNameException {

        Map<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>(0);

        JMXConnector jmxConnector = ss.getConnector();
        MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
        ObjectName smartExecutor = new ObjectName("org.smexec:type=SmartExecutor.Pools,name=*");

        Set<ObjectInstance> smartExecutorSet = mbsc.queryMBeans(smartExecutor, null);

        for (ObjectInstance on : smartExecutorSet) {
            ObjectName objectName = on.getObjectName();
            PoolsFeed pf = new PoolsFeed();
            pf.setPoolName(objectName.getKeyProperty("name"));

            String chunks = (String) mbsc.getAttribute(objectName, "TimeChunks");
            // System.out.println(chunks);
            chunks = chunks.replace("[", "");
            String[] split = chunks.split("]");

            ChartFeed timesChartFeed = new ChartFeed(split.length, 3);
            for (int i = 0; i < split.length; i++) {
                String[] values = split[i].split(",");
                timesChartFeed.getValues()[0][i] = Long.valueOf(values[0]);
                timesChartFeed.getValues()[1][i] = Long.valueOf(values[1]);
                timesChartFeed.getValues()[2][i] = Long.valueOf(values[2]);
            }
            pf.setTimeChartFeeds(timesChartFeed);
            // /////////////////////////////////////////
            chunks = (String) mbsc.getAttribute(objectName, "TasksChunks");
            chunks = chunks.replace("[", "");
            split = chunks.split("]");

            ChartFeed tasksChartFeed = new ChartFeed(split.length, 5);
            for (int i = 0; i < split.length; i++) {
                String[] values = split[i].split(",");
                tasksChartFeed.getValues()[0][i] = Long.valueOf(values[0]);
                tasksChartFeed.getValues()[1][i] = Long.valueOf(values[1]);
                tasksChartFeed.getValues()[2][i] = Long.valueOf(values[2]);
                tasksChartFeed.getValues()[3][i] = Long.valueOf(values[3]);
                tasksChartFeed.getValues()[4][i] = Long.valueOf(values[4]);
            }
            pf.setTasksChartFeeds(tasksChartFeed);

            // //////////////////////////////////////////////////////////
            pf.setActiveThreads(getLong(mbsc, objectName, "ActiveCount"));
            pf.setAvgGenTime(getLong(mbsc, objectName, "AvgTime"));
            pf.setCompleted(getLong(mbsc, objectName, "Completed"));
            pf.setExecuted(getLong(mbsc, objectName, "Executed"));
            pf.setFailed(getLong(mbsc, objectName, "Failed"));
            pf.setLargestPoolSize(getLong(mbsc, objectName, "LargestPoolSize"));
            pf.setMaxGenTime(getLong(mbsc, objectName, "MaxTime"));
            pf.setMinGenTime(getLong(mbsc, objectName, "MinTime"));
            pf.setPoolSize(getLong(mbsc, objectName, "PoolSize"));
            pf.setRejected(getLong(mbsc, objectName, "Rejected"));
            pf.setSubmitted(getLong(mbsc, objectName, "Submitted"));
            pf.setTotoalGenTime(getLong(mbsc, objectName, "TotalTime"));

            poolFeedMap.put(pf.getPoolName(), pf);
            // System.out.println(pf);
        }

        ss.setPoolFeedMap(poolFeedMap);
    }

    private void getMemoryStats(ServerStataus serverStataus)
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException, MalformedObjectNameException {
        MBeanServerConnection mbsc = serverStataus.getConnector().getMBeanServerConnection();
        JMXGetGCStat p = new JMXGetGCStat(mbsc);

        MemoryMXBean mxBean = newPlatformMXBeanProxy(mbsc, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);

        MemoryUsage heapMemoryUsage = mxBean.getHeapMemoryUsage();

        String memoryState = p.getMemoryState();

        serverStataus.setUptime(p.getUpTime());

        serverStataus.updateMemoryUsage(heapMemoryUsage.getInit(),
                                        heapMemoryUsage.getUsed(),
                                        heapMemoryUsage.getCommitted(),
                                        heapMemoryUsage.getMax(),
                                        memoryState);

        List<GCHistory> gcHistoryList = p.getGcHistory();
        for (GCHistory gch : gcHistoryList) {
            serverStataus.updateGCHistory(gch);
        }
    }

    private long getLong(MBeanServerConnection mbsc, ObjectName on, String name) {
        try {
            Object property = mbsc.getAttribute(on, name);
            return Long.valueOf(property.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;

        }
    }
}
