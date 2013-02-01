package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.List;

public class RefreshResult
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PoolsFeed> poolsFeeds;

    private List<String> servers;

    public RefreshResult() {}

    public RefreshResult(List<PoolsFeed> poolsFeeds, List<String> servers) {
        super();
        this.poolsFeeds = poolsFeeds;
        this.servers = servers;
    }

    public List<PoolsFeed> getPoolsFeeds() {
        return poolsFeeds;
    }

    public List<String> getServers() {
        return servers;
    }

}
