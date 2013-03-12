package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RefreshResult <C extends ConnectedServer>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * a map of aggregated thread pools statistic, ready to be presented by client
     */
    private HashMap<String, PoolsFeed> poolFeedMap;

    /**
     * list of connected servers with internal stats
     */
    private ArrayList<C> servers = new ArrayList<C>(0);

    private String title;

    public RefreshResult() {}

    public RefreshResult(String title, ArrayList<C> servers, HashMap<String, PoolsFeed> poolFeedMap) {
        super();
        this.poolFeedMap = poolFeedMap;
        this.servers = servers;
        this.title = title;
    }

    public HashMap<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    public ArrayList<C> getServers() {
        return servers;
    }

    public String getTitle() {
        return title;
    }

}
