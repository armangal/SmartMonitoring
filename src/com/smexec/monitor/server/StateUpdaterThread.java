package com.smexec.monitor.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.ConnectedServers;
import com.smexec.monitor.shared.PoolsFeed;

public class StateUpdaterThread
    implements Runnable {

    @Override
    public void run() {
        System.out.println("Refreshing stats");
        ConnectedServers servers = new ConnectedServers();

        ArrayList<ConnectedServer> serversList = new ArrayList<ConnectedServer>(0);
        for (ServerStataus ss : ConnectedServersState.map.values()) {
            ServerConfig sc = ss.getServerConfig();
            if (ss.isConnected()) {
                try {
                    getMemoryStats(ss);
                    getSmartThreadPoolStats(ss);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ConnectedServer cs = new ConnectedServer(sc.getName(), sc.getServerCode(), sc.getIp(), sc.getJmxPort(), ss.isConnected(), ss.getLastMemoryUsage());
            serversList.add(cs);
        }
        servers.setServers(serversList);

        ConnectedServersState.mergeStats(servers);
    }

    private void getSmartThreadPoolStats(ServerStataus ss)
        throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {

        Map<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>(0);

        JMXConnector jmxConnector = ss.getConnector();
        MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
        System.out.println(mbsc.getMBeanCount());

        System.out.println(Arrays.toString(mbsc.getDomains()));

        Set<ObjectInstance> names = new HashSet<ObjectInstance>(mbsc.queryMBeans(null, null));

        for (ObjectInstance on : names) {
            ObjectName objectName = on.getObjectName();
            if ("SmartExecutor.Pools".equals(objectName.getKeyProperty("type"))) {
                PoolsFeed pf = new PoolsFeed();
                String poolName = objectName.getKeyProperty("name");
                pf.setPoolName(poolName);

                String chunks = (String) mbsc.getAttribute(objectName, "TimeChunks");
                chunks = chunks.replace("[", "");
                String[] split = chunks.split("]");

                ChartFeed timesChartFeed = new ChartFeed(split.length, 3);
                for (int i = 1; i < split.length; i++) {
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
                for (int i = 1; i < split.length; i++) {
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
                System.out.println(pf);
            }
        }

        ss.setPoolFeedMap(poolFeedMap);
    }

    private void getMemoryStats(ServerStataus serverStataus)
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException, MalformedObjectNameException {
        MBeanServerConnection mbsc = serverStataus.getConnector().getMBeanServerConnection();

        Object o = mbsc.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
        CompositeData cd = (CompositeData) o;
        serverStataus.updateMemoryUsage(Long.valueOf(cd.get("init").toString()),
                                        Long.valueOf(cd.get("used").toString()),
                                        Long.valueOf(cd.get("committed").toString()),
                                        Long.valueOf(cd.get("max").toString()));
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
