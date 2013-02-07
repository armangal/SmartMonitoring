package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.HashMap;

public class RefreshResult
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, PoolsFeed> poolFeedMap;

    private ConnectedServers connectedServers;

    public RefreshResult() {}

    public RefreshResult(HashMap<String, PoolsFeed> poolFeedMap, ConnectedServers connectedServers) {
        super();
        this.poolFeedMap = poolFeedMap;
        this.connectedServers = connectedServers;
    }

    public HashMap<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    public ConnectedServers getConnectedServers() {
        return connectedServers;
    }
    
    
    public void setPoolFeedMap(HashMap<String, PoolsFeed> poolFeedMap) {
        this.poolFeedMap = poolFeedMap;
    }
}
