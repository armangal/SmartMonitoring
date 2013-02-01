package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.List;

public class PoolsFeed
    implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String poolName;
    List<ChartFeed> chartFeeds;

    public PoolsFeed() {}

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public List<ChartFeed> getChartFeeds() {
        return chartFeeds;
    }

    public void setChartFeeds(List<ChartFeed> chartFeeds) {
        this.chartFeeds = chartFeeds;
    }

}
