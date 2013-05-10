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
package com.smexec.monitor.server.tasks.impl;

import java.util.Date;

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServerConfig;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.runtime.MemoryUsageLight;

public class StateUpdaterThread
    extends AbstractStateUpdaterThread<ServerStataus, Refresher<ServerStataus>, ConnectedServer> {

    @Override
    public Refresher<ServerStataus> getRefresher(ServerStataus ss, Date executionDate, int excutionNumber) {
        return new Refresher<ServerStataus>(ss, executionDate, excutionNumber);
    }

    @Override
    public ConnectedServer getConnectedServer(ServerStataus ss) {
        ServerConfig sc = ss.getServerConfig();

        MemoryUsageLight mul = getMemoryLight(ss);
        
        return new ConnectedServer(sc.getName(),
                                   sc.getServerCode(),
                                   sc.getIp(),
                                   sc.getJmxPort(),
                                   ss.isConnected(),
                                   mul,
                                   ss.getHighHistory(),
                                   ss.getUpTime(),
                                   ss.getCpuUtilization().getLastPercent().getUsage(),
                                   ss.getCpuUtilization().getLastPercent().getSystemLoadAverage());
    }

}
