package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.PoolsFeed;

public abstract class AbstractConnectedServersState<S extends ServerStataus, R extends AbstractRefreshResult<C>, C extends ConnectedServer> {

    private static Logger logger = LoggerFactory.getLogger(AbstractConnectedServersState.class);

    private ConcurrentHashMap<Integer, S> connectedServersMap = new ConcurrentHashMap<Integer, S>();

    /**
     * Result ready to be used by clients
     */
    private R result;

    public abstract R createNewRefreshResult(ArrayList<C> servers, HashMap<String, PoolsFeed> poolFeedMap);

    public abstract void mergeExtraData(S ss);

    public ConcurrentHashMap<Integer, S> getMap() {
        return connectedServersMap;
    }

    public synchronized R getRefreshResult() {
        return result;
    }

    /**
     * might be overridden to initiate additional result objects
     * 
     * @param servers
     */
    public void mergeStats(ArrayList<C> servers) {
        HashMap<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>();

        logger.info("Merging stats");
        for (S ss : connectedServersMap.values()) {
            if (ss.isConnected()) {
                for (PoolsFeed pf : ss.getPoolFeedMap().values()) {
                    // go over all pools in each server
                    if (poolFeedMap.containsKey(pf.getPoolName())) {
                        poolFeedMap.get(pf.getPoolName()).merge(pf);
                    } else {
                        poolFeedMap.put(pf.getPoolName(), pf);
                    }
                }

                mergeExtraData(ss);
            }
        }

        result = createNewRefreshResult(servers, poolFeedMap);
    }

}
