package com.smexec.monitor.server.model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.smexec.monitor.shared.ConnectedServers;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.RefreshResult;

public final class ConnectedServersState {

    private static ConcurrentHashMap<Integer, ServerStataus> map = new ConcurrentHashMap<Integer, ServerStataus>();

    /**
     * Result ready to be used by clients
     */
    private static RefreshResult result = new RefreshResult();

    /**
     * current most up-to-date configurations
     */
    private static ServersConfig serversConfig;

    
    public static ConcurrentHashMap<Integer, ServerStataus> getMap() {
        return map;
    }
   
    public static synchronized RefreshResult getRefreshResult() {
        return result;
    }

    public static synchronized void mergeStats(ConnectedServers servers) {
        HashMap<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>();
        for (ServerStataus ss : map.values()) {
            if (ss.isConnected()) {
                for (PoolsFeed pf : ss.getPoolFeedMap().values()) {
                    // go over all pools in each server
                    if (poolFeedMap.containsKey(pf.getPoolName())) {
                        poolFeedMap.get(pf.getPoolName()).merge(pf);
                    } else {
                        poolFeedMap.put(pf.getPoolName(), pf);
                    }
                }
            }
        }

        result = new RefreshResult(poolFeedMap, servers, serversConfig.getName());
    }

    public static void setServersConfig(ServersConfig serversConfig) {
        ConnectedServersState.serversConfig = serversConfig;
    }
}
