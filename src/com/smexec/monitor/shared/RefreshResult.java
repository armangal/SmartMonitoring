package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RefreshResult
    implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * a map of aggregated thread pools statistic, ready to be presented by client
     */
    private HashMap<String, PoolsFeed> poolFeedMap;

    /**
     * list of connected servers with internal stats
     */
    private ArrayList<ConnectedServer> servers = new ArrayList<ConnectedServer>(0);

    private String title;

    public RefreshResult() {}

    public RefreshResult(HashMap<String, PoolsFeed> poolFeedMap, ArrayList<ConnectedServer> servers, String title) {
        super();
        this.poolFeedMap = poolFeedMap;
        this.servers = servers;
        this.title = title;
    }

    public HashMap<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    public ArrayList<ConnectedServer> getServers() {
        return servers;
    }

    public String getTitle() {
        return title;
    }
}
