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
package com.smexec.monitor.server.services.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import com.google.inject.Inject;
import com.smexec.monitor.client.ServerWidgetService;
import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.AbstractServersConfig;
import com.smexec.monitor.server.model.config.DatabaseConfig;
import com.smexec.monitor.server.utils.IJMXGeneralStats;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;
import com.smexec.monitor.server.utils.ListUtils;
import com.smexec.monitor.shared.errors.AuthenticationException;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;
import com.smexec.monitor.shared.servers.ConnectedDB;
import com.smexec.monitor.shared.servers.ConnectedServer;
import com.smexec.monitor.shared.servers.ServersRefreshRequest;
import com.smexec.monitor.shared.servers.ServersRefreshResponse;

/**
 * The server side implementation of the monitoring RPC service.
 */
public abstract class AbstractServerWidgetServiceImpl<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    extends AbstractMonitoringService<SS, SC, DS>
    implements ServerWidgetService {

    private static final long serialVersionUID = 1L;

    @Inject
    private JMXThreadDumpUtils jmxThreadDumpUtils;

    @Inject
    private IJMXGeneralStats<SS> jmxGeneralStats;

    public ConnectedServer getConnectedServer(Integer serverCode) throws AuthenticationException {
        checkAuthenticated(false);
        ConnectedServer cs = getConnectedServersState().getConnectedServer(serverCode);
        if (cs == null) {
            logger.warn("getConnectedServer, server:{} was not found or not connected.", serverCode);
            return null;

        } else {
            return cs;
        }
    }

    ArrayList<ConnectedDB> getDatabases() {
        ArrayList<ConnectedDB> ret = new ArrayList<ConnectedDB>(0);
        for (DS ds : getConnectedServersState().getDatabases()) {
            DatabaseConfig dc = ds.getDatabaseConfig();
            ret.add(new ConnectedDB(dc.getName(), dc.getIp(), dc.getPort(), dc.getType(), dc.getService(), ds.isConnected(), ds.getLastPingTime()));
        }
        return ret;
    }

    @Override
    public ServersRefreshResponse refresh(ServersRefreshRequest request) {
        return new ServersRefreshResponse(new Date().toString(), getConnectedServersState().getServers(), getDatabases());
    }

    public ThreadDump getThreadDump(Integer serverCode) throws AuthenticationException {
        checkAuthenticated(true);
        SS ss = getConnectedServersState().getServerStataus(serverCode);
        if (ss == null || !ss.isConnected()) {
            logger.warn("Server:" + serverCode + " not found or not connected.");
            return null;

        } else {
            return jmxThreadDumpUtils.getThreadDump(ss);
        }
    }

    public String getGCHistory(Integer serverCode) throws AuthenticationException {
        checkAuthenticated(false);
        SS serverStataus = getConnectedServersState().getServerStataus(serverCode);
        if (serverStataus != null) {
            return serverStataus.getGCHistory();
        } else {
            return "Server not found:" + serverCode;
        }
    }

    public LinkedList<MemoryUsage> getMemoryStats(Integer serverCode, Integer chunks) throws AuthenticationException {
        checkAuthenticated(false);
        SS serverStataus = getConnectedServersState().getServerStataus(serverCode);
        if (serverStataus != null) {
            LinkedList<MemoryUsage> memoryUsage = serverStataus.getMemoryUsage(chunks);
            return ListUtils.blur(memoryUsage, 200, new LinkedList<MemoryUsage>());

        }
        logger.warn("can't find server with code:{} for memory stats", serverCode);
        return null;
    }

    public LinkedList<CpuUtilizationChunk> getCpuUsageHistory(Integer serverCode, Integer chunks) throws AuthenticationException {
        checkAuthenticated(false);
        SS serverStataus = getConnectedServersState().getServerStataus(serverCode);
        if (serverStataus != null) {
            LinkedList<CpuUtilizationChunk> percentList = serverStataus.getCpuUtilization().getPercentList(chunks);
            return ListUtils.blur(percentList, 200, new LinkedList<CpuUtilizationChunk>());
        }
        logger.warn("can't find server with code:{} for cpu stats", serverCode);
        return null;
    }

    public RuntimeInfo getRuntimeInfo(Integer serverCode) throws AuthenticationException {
        checkAuthenticated(false);
        SS serverStataus = getConnectedServersState().getServerStataus(serverCode);
        if (serverStataus != null && serverStataus.isConnected()) {
            try {
                return jmxGeneralStats.getRuntimeInfo(serverStataus);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        logger.warn("can't find server with code:{} for cpu stats", serverCode);
        return null;
    }

}
