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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.model.AbstractConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

public abstract class AbstractStateUpdaterThread<T extends ServerStataus, R extends Refresher<T>, C extends ConnectedServer, RR extends RefreshResult<C>>
    implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(StateUpdaterThread.class);

    static ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private AtomicInteger num = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "REFRESH_" + num.getAndIncrement());
        }

    });

    @Inject
    private AbstractConnectedServersState<T, RR, C> abstractConnectedServersState;

    @Override
    public void run() {
        try {
            // During threads scheduling, do not allow updates to servers
            ConnectionSynch.connectionLock.lock();

            System.out.println("Refreshing stats for all servers");
            ArrayList<C> serversList = new ArrayList<C>(0);

            CompletionService<T> compService = new ExecutorCompletionService<T>(threadPool);

            Collection<T> values = abstractConnectedServersState.getMap().values();
            // scheduling update threads
            for (T ss : values) {
                compService.submit((Callable<T>) getRefresher(ss));
            }

            // Waiting for all threads to finish
            for (int i = 0; i < values.size(); i++) {
                Future<T> take = compService.take();
                T ss = take.get();
                System.out.println("Finished updating:" + ss.getServerConfig().getName());
                C cs = getConnectedServer(ss);
                serversList.add(cs);
            }

            // finished querying all connected servers, now merging the results.
            abstractConnectedServersState.mergeStats(serversList);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        } finally {
            ConnectionSynch.connectionLock.unlock();
        }
    }

    public abstract R getRefresher(T ss);

    public abstract C getConnectedServer(T ss);
}
