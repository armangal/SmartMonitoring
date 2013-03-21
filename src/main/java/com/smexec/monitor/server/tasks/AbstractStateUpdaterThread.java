package com.smexec.monitor.server.tasks;

import java.util.ArrayList;
import java.util.Collection;
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
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;

public abstract class AbstractStateUpdaterThread<S extends ServerStataus, R extends Refresher<S>, C extends ConnectedServer, RR extends AbstractRefreshResult<C>>
    implements IStateUpdaterThread {

    private static Logger logger = LoggerFactory.getLogger(StateUpdaterThread.class);

    static ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private AtomicInteger num = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "REFRESH_" + num.getAndIncrement());
        }

    });

    @Inject
    private IConnectedServersState<S, C, RR> connectedServersState;

    @Override
    public void run() {
        try {
            // During threads scheduling, do not allow updates to servers
            ConnectionSynch.connectionLock.lock();

            logger.info("Refreshing stats for all servers");
            ArrayList<C> serversList = new ArrayList<C>(0);

            CompletionService<S> compService = new ExecutorCompletionService<S>(threadPool);

            Collection<S> values = connectedServersState.getMap().values();
            // scheduling update threads
            for (S ss : values) {
                compService.submit(getRefresher(ss));
            }

            // Waiting for all threads to finish
            for (int i = 0; i < values.size(); i++) {
                Future<S> take = compService.take();
                S ss = take.get();
                logger.info("Finished updating:{}, {} from {}", new Object[] {ss.getServerConfig().getName(), i, values.size()});
                C cs = getConnectedServer(ss);
                serversList.add(cs);
            }

            // finished querying all connected servers, now merging the results.
            connectedServersState.mergeStats(serversList);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        } finally {
            ConnectionSynch.connectionLock.unlock();
        }
    }

    public abstract R getRefresher(S ss);

    public abstract C getConnectedServer(S ss);
}
