package com.smexec.monitor.server.tasks;

import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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

import com.google.gwt.dom.client.LinkElement;
import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.utils.JMXGetGCStat;
import com.smexec.monitor.shared.ChannelChunkStats;
import com.smexec.monitor.shared.ChannelSeverStats;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.GCHistory;
import com.smexec.monitor.shared.GameServerChunk;
import com.smexec.monitor.shared.GameServerStats;
import com.smexec.monitor.shared.LobbyChunkStats;
import com.smexec.monitor.shared.LobbySeverStats;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.Tournament;
import com.sun.management.OperatingSystemMXBean;

public class StateUpdaterThread
    implements Runnable {

    private static ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private AtomicInteger num = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "REFRESH_" + num.getAndIncrement());
        }

    });

    private class Refresher
        implements Callable<ServerStataus> {

        private ServerStataus ss;

        public Refresher(ServerStataus ss) {
            this.ss = ss;
        }

        @Override
        public ServerStataus call()
            throws Exception {
            try {
                if (ss.isConnected()) {
                    System.out.println("Refreshing:" + ss.getServerConfig().getName());
                    getMemoryStats(ss);
                    getSmartThreadPoolStats(ss);
                    getChannelStatistics(ss);
                    getLobbyStatistics(ss);
                    getGameServerStatistics(ss);

                    ss.setFirstTimeAccess(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ss;
        }

    };

    @Override
    public void run() {
        try {
            ConnectionSynch.connectionLock.lock();

            System.out.println("Refreshing stats");
            ArrayList<ConnectedServer> serversList = new ArrayList<ConnectedServer>(0);

            CompletionService<ServerStataus> compService = new ExecutorCompletionService<ServerStataus>(threadPool);

            Collection<ServerStataus> values = ConnectedServersState.getMap().values();
            for (ServerStataus ss : values) {
                compService.submit(new Refresher(ss));
            }

            for (int i = 0; i < values.size(); i++) {
                Future<ServerStataus> take = compService.take();
                ServerStataus ss = take.get();
                System.out.println("Finished updating:" + ss.getServerConfig().getName());
                ServerConfig sc = ss.getServerConfig();
                ConnectedServer cs = new ConnectedServer(sc.getName(),
                                                         sc.getServerCode(),
                                                         sc.getIp(),
                                                         sc.getJmxPort(),
                                                         ss.isConnected(),
                                                         ss.getLastMemoryUsage(),
                                                         ss.getLastGCHistory(),
                                                         ss.getUpTime(),
                                                         ss.hasChannelSeverStats() ? ss.getChannelSeverStats() : null,
                                                         ss.getCpuUtilization());
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
     * getting stats from game server about
     * 
     * @param ss
     */
    private void getGameServerStatistics(ServerStataus ss) {
        try {
            JMXConnector jmxConnector = ss.getConnector();
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            ObjectName sessionStats = new ObjectName("com.playtech.poker.jmx:type=Stats,name=PokerGameServerStats");
            if (mbsc.isRegistered(sessionStats)) {
                CompositeData[] data;
                GameServerStats gameSeverStats = ss.getGameSeverStats();
                if (ss.isFirstTimeAccess()) {
                    // load all stats from game server
                    System.out.println("Requesting full stats from Game server:" + ss.getServerConfig().getServerCode());
                    data = (CompositeData[]) mbsc.getAttribute(sessionStats, "ServerStats");

                } else {
                    // load delta
                    long startTime = gameSeverStats.getLastChunk().getStartTime();
                    System.out.println("Requesting delta Game chunks from:" + startTime);
                    data = (CompositeData[]) mbsc.invoke(sessionStats, "getLastServerStats", new Object[] {startTime}, new String[] {"long"});
                }

                System.out.println("got :" + data.length + " chunks from Game:" + ss.getServerConfig().getServerCode());

                for (int i = data.length - 1; i >= 0; i--) {
                    CompositeData cd = data[i];

                    GameServerChunk gch = new GameServerChunk(getIntAtributeFromComposite(cd, "startTime"), getIntAtributeFromComposite(cd, "endTime"));

                    // load canceled tournaments
                    CompositeData[] canceled = (CompositeData[]) cd.get("cancelledTournaments");
                    if (canceled.length > 0) {
                        LinkedList<Tournament> list = new LinkedList<Tournament>();
                        for (CompositeData tr : canceled) {
                            Tournament tournament = new Tournament(getLongAtributeFromComposite(tr, "code"),
                                                                   tr.get("name").toString(),
                                                                   tr.get("cancellationDate").toString(),
                                                                   getIntAtributeFromComposite(tr, "reason"));
                            list.add(tournament);
                        }
                        gch.setCanceled(list);
                    }

                    // load interrupted tournaments
                    CompositeData[] interrupted = (CompositeData[]) cd.get("interruptedTournaments");
                    if (interrupted.length > 0) {
                        LinkedList<Tournament> list = new LinkedList<Tournament>();
                        for (CompositeData tr : interrupted) {
                            Tournament tournament = new Tournament(getLongAtributeFromComposite(tr, "code"),
                                                                   tr.get("name").toString(),
                                                                   tr.get("interruptionDate").toString(),
                                                                   getIntAtributeFromComposite(tr, "reason"),
                                                                   getIntAtributeFromComposite(tr, "registeredPlayers"));
                            list.add(tournament);
                        }
                        gch.setInterrupted(list);
                    }

                    gameSeverStats.addChunk(gch);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * gets stats about lobby server
     * 
     * @param ss2
     */
    private void getLobbyStatistics(ServerStataus ss) {
        try {
            JMXConnector jmxConnector = ss.getConnector();
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            ObjectName sessionStats = new ObjectName("com.playtech.poker.jmx:type=Stats,name=LobbyServerStats");
            if (mbsc.isRegistered(sessionStats)) {
                // Lobby server
                LobbySeverStats stats = ss.getLobbySeverStats();
                CompositeData[] data;
                if (ss.isFirstTimeAccess()) {
                    // load all stats from lobby
                    System.out.println("Requesting full stats from Lobby");
                    data = (CompositeData[]) mbsc.getAttribute(sessionStats, "ServerStats");

                } else {
                    // load delta
                    long startTime = stats.getLastChunk().getStartTime();
                    System.out.println("Requesting delta Lobby chunks from:" + startTime);
                    data = (CompositeData[]) mbsc.invoke(sessionStats, "getLastServerStats", new Object[] {startTime}, new String[] {"long"});
                }
                System.out.println("got :" + data.length + " chunks from lobby");

                for (int i = data.length - 1; i >= 0; i--) {
                    CompositeData cd = data[i];

                    LobbyChunkStats lcs = new LobbyChunkStats(getIntAtributeFromComposite(cd, "startTime"),
                                                              getIntAtributeFromComposite(cd, "endTime"),
                                                              getIntAtributeFromComposite(cd, "funTables"),
                                                              getIntAtributeFromComposite(cd, "funActiveTables"),
                                                              getIntAtributeFromComposite(cd, "funCashPlayers"),
                                                              getIntAtributeFromComposite(cd, "funTournamentPlayers"),
                                                              getIntAtributeFromComposite(cd, "funActiveTournaments"),
                                                              getIntAtributeFromComposite(cd, "realSpeedRooms"),
                                                              getIntAtributeFromComposite(cd, "realActiveSpeedRooms"),
                                                              getIntAtributeFromComposite(cd, "realActiveSpeedRoomPlayers"),
                                                              getIntAtributeFromComposite(cd, "realTables"),
                                                              getIntAtributeFromComposite(cd, "realActiveTables"),
                                                              getIntAtributeFromComposite(cd, "realCashPlayers"),
                                                              getIntAtributeFromComposite(cd, "realActiveTournaments"),
                                                              getIntAtributeFromComposite(cd, "realTournamentPlayers"),
                                                              getIntAtributeFromComposite(cd, "realTournamentsInRegisterStatus"),
                                                              getIntAtributeFromComposite(cd, "funTournamentsInRegisterStatus"),
                                                              getIntAtributeFromComposite(cd, "realRegisteredPlayers"),
                                                              getIntAtributeFromComposite(cd, "funRegisteredPlayers"));

                    stats.addChunk(lcs);
                    System.out.println(lcs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    System.out.println("Requesting full stats from Channel:" + ss.getServerConfig().getServerCode());

                    data = (CompositeData[]) mbsc.getAttribute(sessionStats, "ServerStats");

                } else {
                    // load delta
                    long startTime = stats.getLastChunk().getStartTime();
                    System.out.println("Requesting delta Channel:" + ss.getServerConfig().getServerCode() + ", chunks from:" + startTime);
                    data = (CompositeData[]) mbsc.invoke(sessionStats, "getLastServerStats", new Object[] {startTime}, new String[] {"long"});
                }
                System.out.println("got :" + data.length + " chunks from channel:" + ss.getServerConfig().getServerCode());
                for (int i = data.length - 1; i >= 0; i--) {
                    CompositeData cd = data[i];
                    ChannelChunkStats cscs = new ChannelChunkStats(getIntAtributeFromComposite(cd, "connectedBinarySessions"),
                                                                   getIntAtributeFromComposite(cd, "connectedLegacySessions"),
                                                                   getIntAtributeFromComposite(cd, "disconnectedBinarySessions"),
                                                                   getIntAtributeFromComposite(cd, "disconnectedLegacySessions"),
                                                                   getIntAtributeFromComposite(cd, "startTime"),
                                                                   getIntAtributeFromComposite(cd, "endTime"),
                                                                   getIntAtributeFromComposite(cd, "totalBinarySessions"),
                                                                   getIntAtributeFromComposite(cd, "totalLegacySessions"));

                    stats.addChunk(cscs);
                    System.out.println(cscs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer getIntAtributeFromComposite(CompositeData cd, String name) {
        try {
            return Integer.valueOf(cd.get(name).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private Long getLongAtributeFromComposite(CompositeData cd, String name) {
        try {
            return Long.valueOf(cd.get(name).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
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

            CompositeData[] times = (CompositeData[]) mbsc.getAttribute(objectName, "ExecutionTimeStats");

            ChartFeed timesChartFeed = new ChartFeed(times.length, 3);
            for (int i = 0; i < times.length; i++) {
                CompositeData timeStat = times[i];
                timesChartFeed.getValues()[0][i] = getLongAtributeFromComposite(timeStat, "max");
                timesChartFeed.getValues()[1][i] = getLongAtributeFromComposite(timeStat, "avg");
                timesChartFeed.getValues()[2][i] = getLongAtributeFromComposite(timeStat, "min");
            }
            pf.setTimeChartFeeds(timesChartFeed);
            // /////////////////////////////////////////

            CompositeData[] tasks = (CompositeData[]) mbsc.getAttribute(objectName, "TaskExecutionStats");
            ChartFeed tasksChartFeed = new ChartFeed(tasks.length, 5);
            for (int i = 0; i < tasks.length; i++) {
                CompositeData taskStat = tasks[i];
                tasksChartFeed.getValues()[0][i] = getLongAtributeFromComposite(taskStat, "submitted");
                tasksChartFeed.getValues()[1][i] = getLongAtributeFromComposite(taskStat, "executed");
                tasksChartFeed.getValues()[2][i] = getLongAtributeFromComposite(taskStat, "completed");
                tasksChartFeed.getValues()[3][i] = getLongAtributeFromComposite(taskStat, "failed");
                tasksChartFeed.getValues()[4][i] = getLongAtributeFromComposite(taskStat, "rejected");
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

    /**
     * gets the memory and cpu stats
     * 
     * @param serverStataus
     * @throws MBeanException
     * @throws AttributeNotFoundException
     * @throws InstanceNotFoundException
     * @throws ReflectionException
     * @throws IOException
     * @throws MalformedObjectNameException
     */
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

        OperatingSystemMXBean operatingSystemMXBean = newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        serverStataus.updateCPUutilization(operatingSystemMXBean.getProcessCpuTime(), System.nanoTime());
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
