package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class AbstractRefreshResult<C extends ConnectedServer>
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

    public AbstractRefreshResult() {}

    public AbstractRefreshResult(ArrayList<C> servers, HashMap<String, PoolsFeed> poolFeedMap) {
        super();
        this.poolFeedMap = poolFeedMap;
        this.servers = servers;
    }

    public HashMap<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    public ArrayList<C> getServers() {
        return servers;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ARR [pfm=");
        builder.append(poolFeedMap.size());
        builder.append(", ser=");
        builder.append(servers);
        builder.append("]");
        return builder.toString();
    }

}
