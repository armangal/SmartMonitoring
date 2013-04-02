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
package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

public class ConnectedServersState
    extends AbstractConnectedServersState<ServerStataus, RefreshResult, ConnectedServer>
    implements IConnectedServersState<ServerStataus, ConnectedServer, RefreshResult> {

    @Override
    public void mergeExtraData(ServerStataus ss) {
        // nothing
    }

    @Override
    public RefreshResult createNewRefreshResult(ArrayList<ConnectedServer> servers, HashMap<String, PoolsFeed> poolFeedMap) {
        return new RefreshResult(servers, poolFeedMap);
    }
}
