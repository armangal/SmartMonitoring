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
package com.smexec.monitor.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.smexec.monitor.client.MonitoringServiceStd;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public class MonitoringServiceImpl
    extends AbstractMonitoringService<ServerStatus, ConnectedServer, FullRefreshResult>
    implements MonitoringServiceStd {

    public MonitoringServiceImpl() {}

    @Override
    public FullRefreshResult createFullRefreshResult(LinkedList<Alert> alerts, ArrayList<ConnectedServer> servers, HashMap<String, PoolsFeed> poolFeedMap) {

        return new FullRefreshResult(alerts, servers, poolFeedMap);
    }



}
