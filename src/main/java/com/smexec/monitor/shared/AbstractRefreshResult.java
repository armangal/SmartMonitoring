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
import java.util.HashMap;

import com.smexec.monitor.shared.smartpool.PoolsFeed;

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
