package com.smexec.monitor.shared.smartpool;

import java.util.Date;
import java.util.HashMap;

import com.smexec.monitor.client.AbstractRefreshResponse;

public class SmartExecutorRefreshResponse
    extends AbstractRefreshResponse {

    private static final long serialVersionUID = 1L;

    /**
     * a map of aggregated thread pools statistic, ready to be presented by client
     */
    private HashMap<String, PoolsFeed> poolFeedMap;

    public SmartExecutorRefreshResponse() {
        super(new Date().toString());
    }

    public HashMap<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }
}
