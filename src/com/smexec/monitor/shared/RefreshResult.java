package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.HashMap;

public class RefreshResult
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, PoolsFeed> poolFeedMap;

    private ConnectedServers connectedServers;

    private String title;

    public RefreshResult() {}

    public RefreshResult(HashMap<String, PoolsFeed> poolFeedMap, ConnectedServers connectedServers, String title) {
        super();
        this.poolFeedMap = poolFeedMap;
        this.connectedServers = connectedServers;
        this.title = title;
    }

    public HashMap<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    public ConnectedServers getConnectedServers() {
        return connectedServers;
    }

    public String getTitle() {
        return title;
    }
}
