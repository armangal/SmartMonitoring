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
package org.clevermore.monitor.server.services.rpc;

import java.util.HashMap;
import java.util.List;

import org.clevermore.monitor.client.SmartExecutorService;
import org.clevermore.monitor.server.model.DatabaseServer;
import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.server.model.config.AbstractServersConfig;
import org.clevermore.monitor.shared.smartpool.PoolsFeed;
import org.clevermore.monitor.shared.smartpool.SmartExecutorDataHolder;
import org.clevermore.monitor.shared.smartpool.SmartExecutorRefreshRequest;
import org.clevermore.monitor.shared.smartpool.SmartExecutorRefreshResponse;


/**
 * The server side implementation of the monitoring RPC service.
 */
public abstract class AbstractSmartExecutorServiceImpl<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    extends AbstractMonitoringService<SS, SC, DS>
    implements SmartExecutorService {

    private static final long serialVersionUID = 1L;

    public AbstractSmartExecutorServiceImpl() {}

    @Override
    public SmartExecutorRefreshResponse refresh(SmartExecutorRefreshRequest request) {
        HashMap<String, HashMap<String, PoolsFeed>> smartExecutorsMap = new HashMap<String, HashMap<String, PoolsFeed>>(0);
        List<SS> allServers = getConnectedServersState().getAllServers();

        for (SS server : allServers) {
            if (server.isConnected() && server.hasSmartExecutorData()) {
                SmartExecutorDataHolder sedh = server.getSmartExecutorDataHolder();

                for (String seName : sedh.getAvalibleExecutorNames()) {
                    if (!smartExecutorsMap.containsKey(seName)) {
                        smartExecutorsMap.put(seName, sedh.getPoolStats(seName));
                    }
                }
            }
        }

        SmartExecutorRefreshResponse res = new SmartExecutorRefreshResponse(smartExecutorsMap);
        return res;
    }

}
