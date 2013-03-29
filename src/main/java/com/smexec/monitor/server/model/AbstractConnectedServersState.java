package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

public abstract class AbstractConnectedServersState<SS extends ServerStataus, RR extends AbstractRefreshResult<CS>, CS extends ConnectedServer> {

    private static Logger logger = LoggerFactory.getLogger(AbstractConnectedServersState.class);

    private ConcurrentHashMap<Integer, SS> connectedServersMap = new ConcurrentHashMap<Integer, SS>();

    /**
     * Result ready to be used by clients
     */
    private RR result;

    public abstract RR createNewRefreshResult(ArrayList<CS> servers, HashMap<String, PoolsFeed> poolFeedMap);

    public abstract void mergeExtraData(SS ss);

    public SS getServerStataus(final Integer serverCode) {
        return connectedServersMap.get(serverCode);
    }

    public Collection<SS> getAllServers() {
        return connectedServersMap.values();
    }

    public SS removeServer(final Integer serevrCode) {
        return connectedServersMap.remove(serevrCode);
    }

    public SS addServer(SS serverStataus) {
        return connectedServersMap.put(serverStataus.getServerConfig().getServerCode(), serverStataus);
    }

    public synchronized RR getRefreshResult() {
        return result;
    }

    /**
     * might be overridden to initiate additional result objects
     * 
     * @param servers
     */
    public void mergeStats(ArrayList<CS> servers) {
        HashMap<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>();

        logger.info("Merging stats");
        for (SS ss : connectedServersMap.values()) {
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
