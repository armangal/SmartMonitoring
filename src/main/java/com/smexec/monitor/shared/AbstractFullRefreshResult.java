/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

public abstract class AbstractFullRefreshResult<CS extends ConnectedServer>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private LinkedList<Alert> alerts;

    private String serverTime;

    /**
     * a map of aggregated thread pools statistic, ready to be presented by client
     */
    private HashMap<String, PoolsFeed> poolFeedMap;

    /**
     * list of connected servers with internal stats
     */
    private ArrayList<CS> servers = new ArrayList<CS>(0);

    public AbstractFullRefreshResult() {
        this.alerts = new LinkedList<Alert>();
        this.poolFeedMap = new HashMap<String, PoolsFeed>();
        this.servers = new ArrayList<CS>(0);
    }

    public AbstractFullRefreshResult(LinkedList<Alert> alerts, ArrayList<CS> servers, HashMap<String, PoolsFeed> poolFeedMap) {
        super();
        this.alerts = alerts;
        this.serverTime = new Date().toString();
        this.servers = servers;
        this.poolFeedMap = poolFeedMap;
    }

    public LinkedList<Alert> getAlerts() {
        return alerts;
    }

    public String getServerTime() {
        return serverTime;
    }

    public HashMap<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    public ArrayList<CS> getServers() {
        return servers;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ARR [pfm=");
        builder.append(poolFeedMap!= null ? poolFeedMap.size() : "0")
               .append(", alerts=")
               .append(alerts)
               .append(", server:")
               .append(servers)
               .append(", serverTime:")
               .append(serverTime)
               .append("]");
        return builder.toString();
    }

}
