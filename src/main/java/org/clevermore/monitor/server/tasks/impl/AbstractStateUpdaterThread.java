/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.clevermore.monitor.server.tasks.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.clevermore.SmartExecutor;
import org.clevermore.monitor.server.constants.SmartPoolsMonitoring;
import org.clevermore.monitor.server.model.DatabaseServer;
import org.clevermore.monitor.server.model.IConnectedServersState;
import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.server.tasks.ConnectionSynch;
import org.clevermore.monitor.server.tasks.IStateUpdaterThread;
import org.clevermore.monitor.shared.runtime.MemoryUsage;
import org.clevermore.monitor.shared.runtime.MemoryUsageLight;
import org.clevermore.monitor.shared.servers.ConnectedServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public abstract class AbstractStateUpdaterThread<SS extends ServerStatus, R extends AbstractRefresher<SS>, DS extends DatabaseServer>
    implements IStateUpdaterThread {

    private static Logger logger = LoggerFactory.getLogger("StateUpdaterThread");

    private AtomicInteger executionNumber = new AtomicInteger(0);

    @Inject
    private SmartExecutor smartExecutor;

    @Inject
    private IConnectedServersState<SS, DS> connectedServersState;

    @Override
    public void run() {
        try {
            // During threads scheduling, do not allow updates to servers
            ConnectionSynch.connectionLock.lock();

            long start = System.currentTimeMillis();
            logger.info("Refreshing stats for all servers.");
            ArrayList<ConnectedServer> serversList = new ArrayList<ConnectedServer>(0);

            CompletionService<SS> compService = smartExecutor.getCompletionService(SmartPoolsMonitoring.REFERSHER);

            Collection<SS> values = connectedServersState.getAllServers();

            // scheduling update threads
            Map<Future<SS>, R> futuresMap = new HashMap<Future<SS>, R>();
            for (SS ss : values) {
                R refresher = getRefresher(ss, new Date(), executionNumber.getAndIncrement());
                Future<SS> future = compService.submit(refresher);
                futuresMap.put(future, refresher);
            }

            // Waiting for all threads to finish
            int i = 0;
            do {
                Future<SS> take = compService.poll(15, TimeUnit.SECONDS);
                i++;
                if (take != null) {
                    futuresMap.remove(take);
                    try {
                        SS ss = take.get(10, TimeUnit.SECONDS);
                        logger.info("Finished updating:{}, {} from {}", new Object[] {ss.getServerConfig().getName(), i, values.size()});
                        ConnectedServer cs = getConnectedServer(ss);
                        serversList.add(cs);
                    } catch (Exception e) {
                        take.cancel(true);
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    logger.warn("take was null!!!!");
                }

            } while (i < values.size());

            if (futuresMap.size() > 0) {
                // not good, for some server we couldn't collect stats
                logger.error("Can't collect stats from all servers, skipped servers are below.");
                for (Future<SS> f : futuresMap.keySet()) {
                    R r = futuresMap.get(f);
                    SS serverStataus = r.getServerStataus();
                    logger.error("Skipped:{}", serverStataus);
                    f.cancel(true);
                    ConnectedServer cs = getConnectedServer(serverStataus);
                    serversList.add(cs);
                }

            }

            // finished querying all connected servers, now merging the results.
            logger.info("Staring merge stats");
            connectedServersState.mergeStats(serversList);
            logger.info("Finished merge stats");

            refreshDBs();

            finishedRefresh();

            logger.info("Refreshing finished for all servers, took:{}.", (System.currentTimeMillis() - start));

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);

        } finally {
            ConnectionSynch.connectionLock.unlock();
        }
    }

    private void refreshDBs() {
        CompletionService<DS> compService = new ExecutorCompletionService<DS>(smartExecutor.getThreadPool(SmartPoolsMonitoring.REFERSHER));
        int ref = 0;
        for (DS ds : connectedServersState.getDatabases()) {
            if (ds.isConnected()) {
                compService.submit(getDbRefresher(ds));
                ref++;
            }
        }

        for (int i = 0; i < ref; i++) {
            try {
                Future<DS> take = compService.take();
                DS ds = take.get();
                logger.info("Finished refersh database:" + ds.getDatabaseConfig().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    public abstract Callable<DS> getDbRefresher(DS ds);

    public void finishedRefresh() {
        // Nothing for now
    }

    public abstract R getRefresher(SS ss, Date executionDate, int excutionNumber);

    /**
     * prepares ConnectedServer with relevant to client/UI information
     * 
     * @param ss
     * @return
     */
    public abstract ConnectedServer getConnectedServer(SS ss);

    /**
     * gets the light object from client representation
     * 
     * @param ss
     * @return
     */
    public MemoryUsageLight getMemoryLight(ServerStatus ss) {
        if (!ss.isConnected()) {
            return new MemoryUsageLight();
        }
        MemoryUsage mu = ss.getLastMemoryUsage();
        MemoryUsageLight mul = new MemoryUsageLight(mu.getInit(), mu.getUsed(), mu.getCommitted(), mu.getMax());
        return mul;
    }

    public IConnectedServersState<SS, DS> getConnectedServersState() {
        return connectedServersState;
    }

    public static class Test {

        @org.junit.Test
        public void test() {
            System.out.println("start");
            CompletionService<Boolean> compService = new ExecutorCompletionService<Boolean>(Executors.newCachedThreadPool());

            for (int i = 0; i < 10; i++) {
                compService.submit(new Callable<Boolean>() {

                    @Override
                    public Boolean call()
                        throws Exception {
                        Thread.sleep(10000);
                        return true;
                    }
                });
            }
            for (int i = 0; i < 10; i++) {
                try {
                    Future<Boolean> take = compService.take();

                    System.out.println(take.get(10, TimeUnit.MILLISECONDS));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
