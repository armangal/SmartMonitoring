package com.smexec.monitor.server.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.utils.JMXChannelServerStats;
import com.smexec.monitor.server.utils.JMXGameServerStats;
import com.smexec.monitor.server.utils.JMXGeneralStats;
import com.smexec.monitor.server.utils.JMXLobbyServerStats;
import com.smexec.monitor.server.utils.JMXSmartExecutorStats;
import com.smexec.monitor.shared.ConnectedServer;

public class StateUpdaterThread
    implements Runnable {

    private static ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private AtomicInteger num = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "REFRESH_" + num.getAndIncrement());
        }

    });

    /**
     * inner class, used to update server stats. We use separate threads in order to make the updates as fast
     * as possible
     * 
     * @author armang
     */
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
                    JMXGeneralStats.getMemoryStats(ss);
                    JMXSmartExecutorStats.getSmartThreadPoolStats(ss);
                    JMXChannelServerStats.getChannelStatistics(ss);
                    JMXLobbyServerStats.getLobbyStatistics(ss);
                    JMXGameServerStats.getGameServerStatistics(ss);

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
            // During threads scheduling, do not allow updates to servers
            ConnectionSynch.connectionLock.lock();

            System.out.println("Refreshing stats for all servers");
            ArrayList<ConnectedServer> serversList = new ArrayList<ConnectedServer>(0);

            CompletionService<ServerStataus> compService = new ExecutorCompletionService<ServerStataus>(threadPool);

            Collection<ServerStataus> values = ConnectedServersState.getMap().values();
            // scheduling update threads
            for (ServerStataus ss : values) {
                compService.submit(new Refresher(ss));
            }

            // Waiting for all threads to finish
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

            // finished querying all connected servers, now merging the results.
            ConnectedServersState.mergeStats(serversList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionSynch.connectionLock.unlock();
        }
    }

}
