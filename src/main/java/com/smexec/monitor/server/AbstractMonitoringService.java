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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.AbstractServersConfig;
import com.smexec.monitor.server.services.alert.IAlertService;
import com.smexec.monitor.server.services.config.IConfigurationService;
import com.smexec.monitor.server.utils.IJMXGeneralStats;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;
import com.smexec.monitor.server.utils.ListUtils;
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public abstract class AbstractMonitoringService<SS extends ServerStatus, CS extends ConnectedServer, FR extends AbstractFullRefreshResult<CS>, SC extends AbstractServersConfig, DS extends DatabaseServer>
    extends RemoteServiceServlet {

    private static Logger logger = LoggerFactory.getLogger("MonitoringService");

    private static final String AUTHENTICATED = "authenticated";

    @Inject
    private IConnectedServersState<SS, CS, DS> connectedServersState;

    @Inject
    private JMXThreadDumpUtils jmxThreadDumpUtils;

    @Inject
    private IAlertService<SS> alertService;

    @Inject
    private IConfigurationService<SC> configurationService;

    @Inject
    private IJMXGeneralStats<SS> jmxGeneralStats;

    public AbstractMonitoringService() {
        GuiceUtils.getInjector().injectMembers(this);
    }

    public FR refresh() {
        checkAuthenticated();

        return createFullRefreshResult(connectedServersState.getServers(), connectedServersState.getPoolFeedMap());
    }

    public LinkedList<Alert> getAlerts(int lastAlertId) {
        checkAuthenticated();
        logger.info("GetAlerts Request: alertId:{}", lastAlertId);
        LinkedList<Alert> alertsAfter = getAlertService().getAlertsAfter(lastAlertId, 1000);
        logger.info("Returning:{} alerts.", alertsAfter.size());
        return alertsAfter;
    }

    public abstract FR createFullRefreshResult(ArrayList<CS> servers, HashMap<String, PoolsFeed> poolFeedMap);

    public ThreadDump getThreadDump(Integer serverCode) {
        checkAuthenticated();
        SS ss = connectedServersState.getServerStataus(serverCode);
        if (ss == null || !ss.isConnected()) {
            logger.warn("Server:" + serverCode + " not found or not connected.");
            return null;

        } else {
            return jmxThreadDumpUtils.getThreadDump(ss);
        }
    }

    public String getGCHistory(Integer serverCode) {
        checkAuthenticated();
        SS serverStataus = connectedServersState.getServerStataus(serverCode);
        if (serverStataus != null) {
            return serverStataus.getGCHistory();
        } else {
            return "Server not found:" + serverCode;
        }
    }

    public Boolean authenticate(String userName, String password) {
        logger.info("Authnticating user:{}, pass:{}", userName, password);

        HttpSession session = getThreadLocalRequest().getSession();
        userName = userName.trim();
        password = password.trim();
        SC sc = configurationService.getServersConfig();
        if (sc.getUsername().equalsIgnoreCase(userName) && sc.getPassword().equals(password)) {
            session.setAttribute(AUTHENTICATED, Boolean.TRUE);
            logger.info("Authnticated user:{}, pass:{}", userName, password);
            return true;
        } else {
            logger.info("NOT Authnticated user:{}, pass:{}", userName, password);
            session.removeAttribute(AUTHENTICATED);
            return false;
        }
    }

    void checkAuthenticated() {
        HttpSession session = getThreadLocalRequest().getSession();
        Object auth = session.getAttribute(AUTHENTICATED);
        if (auth == null || !(auth instanceof Boolean) || (((Boolean) auth).booleanValue() == false)) {
            logger.info("Session is not authenticated");

            throw new SecurityException("Session not authenticated, please refresh the browser.");
        }
    }

    public LinkedList<MemoryUsage> getMemoryStats(Integer serverCode, Integer chunks) {
        checkAuthenticated();
        SS serverStataus = connectedServersState.getServerStataus(serverCode);
        if (serverStataus != null) {
            LinkedList<MemoryUsage> memoryUsage = serverStataus.getMemoryUsage(chunks);
            return ListUtils.blur(memoryUsage, 200, new LinkedList<MemoryUsage>());

        }
        logger.warn("can't find server with code:{} for memory stats", serverCode);
        return null;
    }

    public LinkedList<CpuUtilizationChunk> getCpuUsageHistory(Integer serverCode, Integer chunks) {
        checkAuthenticated();
        SS serverStataus = connectedServersState.getServerStataus(serverCode);
        if (serverStataus != null) {
            LinkedList<CpuUtilizationChunk> percentList = serverStataus.getCpuUtilization().getPercentList(chunks);
            return ListUtils.blur(percentList, 200, new LinkedList<CpuUtilizationChunk>());
        }
        logger.warn("can't find server with code:{} for cpu stats", serverCode);
        return null;
    }

    public RuntimeInfo getRuntimeInfo(Integer serverCode) {
        checkAuthenticated();
        SS serverStataus = connectedServersState.getServerStataus(serverCode);
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

    public IConnectedServersState<SS, CS, DS> getConnectedServersState() {
        return connectedServersState;
    }

    public IAlertService<SS> getAlertService() {
        return alertService;
    }

    public String getSettingsXML() {
        try {
            logger.info("About to load configuration xml");
            return configurationService.getServersConfigXML();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public Boolean saveSettingsXML(String xml) {
        try {
            logger.info("About to save xml:{}", xml);
            configurationService.saveServersConfigXML(xml);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public Boolean stopAlerts(boolean enable) {
        return configurationService.stopAlerts(enable);
    }

    public IConfigurationService<SC> getConfigurationService() {
        return configurationService;
    }

}
