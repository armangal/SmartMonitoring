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
import com.smexec.monitor.server.model.config.DatabaseConfig;
import com.smexec.monitor.server.services.alert.IAlertService;
import com.smexec.monitor.server.services.config.IConfigurationService;
import com.smexec.monitor.server.utils.IJMXGeneralStats;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;
import com.smexec.monitor.server.utils.ListUtils;
import com.smexec.monitor.shared.ConnectedDB;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.ServerTimeResult;
import com.smexec.monitor.shared.ServerWidgetRefresh;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public abstract class AbstractMonitoringService<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    extends RemoteServiceServlet {

    private static Logger logger = LoggerFactory.getLogger("MonitoringService");

    private static final String AUTHENTICATED = "authenticated";

    @Inject
    private IConnectedServersState<SS, DS> connectedServersState;

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

    public ConnectedServer getConnectedServer(Integer serverCode) {
        checkAuthenticated(false);
        ConnectedServer cs = connectedServersState.getConnectedServer(serverCode);
        if (cs == null) {
            logger.warn("getConnectedServer, server:{} was not found or not connected.", serverCode);
            return null;

        } else {
            return cs;
        }
    }

    public ServerTimeResult refresh() {
        checkAuthenticated(false);

        return new ServerTimeResult();
    }

    ArrayList<ConnectedDB> getDatabases() {
        ArrayList<ConnectedDB> ret = new ArrayList<ConnectedDB>(0);
        for (DS ds : connectedServersState.getDatabases()) {
            DatabaseConfig dc = ds.getDatabaseConfig();
            ret.add(new ConnectedDB(dc.getName(), dc.getIp(), dc.getPort(), dc.getType(), dc.getService(), ds.isConnected(), ds.getLastPingTime()));
        }
        return ret;
    }

    public LinkedList<Alert> getAlerts(int lastAlertId) {
        checkAuthenticated(false);
        logger.info("GetAlerts Request: alertId:{}", lastAlertId);
        LinkedList<Alert> alertsAfter = getAlertService().getAlertsAfter(lastAlertId, 1000);
        logger.info("Returning:{} alerts.", alertsAfter.size());
        return alertsAfter;
    }

    public ServerWidgetRefresh getServerWidgetRefresh() {
        return new ServerWidgetRefresh(connectedServersState.getServers(), getDatabases());
    }

    public ThreadDump getThreadDump(Integer serverCode) {
        checkAuthenticated(true);
        SS ss = connectedServersState.getServerStataus(serverCode);
        if (ss == null || !ss.isConnected()) {
            logger.warn("Server:" + serverCode + " not found or not connected.");
            return null;

        } else {
            return jmxThreadDumpUtils.getThreadDump(ss);
        }
    }

    public String getGCHistory(Integer serverCode) {
        checkAuthenticated(false);
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
        if ("admin".equalsIgnoreCase(userName) && sc.getPassword().equals(password)) {
            session.setAttribute(AUTHENTICATED, "1");
            logger.info("Authnticated admin user:{}, pass:{}", userName, password);
            return true;
        } else if ("guest".equalsIgnoreCase(userName) && sc.getGuestPassword().equals(password)) {
            session.setAttribute(AUTHENTICATED, "2");
            logger.info("Authnticated guest user:{}, pass:{}", userName, password);
            return true;
        } else {
            logger.info("NOT Authnticated user:{}, pass:{}", userName, password);
            session.removeAttribute(AUTHENTICATED);
            return false;
        }
    }

    public void logout() {
        HttpSession session = getThreadLocalRequest().getSession();
        logger.info("logging out");
        session.removeAttribute(AUTHENTICATED);
    }

    /**
     * @param admin - check if the user is admin
     */
    void checkAuthenticated(boolean admin) {
        HttpSession session = getThreadLocalRequest().getSession();
        String auth = session.getAttribute(AUTHENTICATED).toString();
        if (auth == null || (!auth.equals("1") && !auth.equals("2"))) {
            logger.info("Session is not authenticated");
            throw new SecurityException("Session not authenticated, please refresh the browser.");
        }

        if (admin && !auth.equals("1")) {
            logger.info("Session is authenticated but not admin");
            throw new SecurityException("Session is authenticated but not admin");
        }

        if (!admin && (!auth.equals("1") && !auth.equals("2"))) {
            logger.info("Session is authenticated but not admin");
            throw new SecurityException("Session is authenticated but not admin");
        }

    }

    public LinkedList<MemoryUsage> getMemoryStats(Integer serverCode, Integer chunks) {
        checkAuthenticated(false);
        SS serverStataus = connectedServersState.getServerStataus(serverCode);
        if (serverStataus != null) {
            LinkedList<MemoryUsage> memoryUsage = serverStataus.getMemoryUsage(chunks);
            return ListUtils.blur(memoryUsage, 200, new LinkedList<MemoryUsage>());

        }
        logger.warn("can't find server with code:{} for memory stats", serverCode);
        return null;
    }

    public LinkedList<CpuUtilizationChunk> getCpuUsageHistory(Integer serverCode, Integer chunks) {
        checkAuthenticated(false);
        SS serverStataus = connectedServersState.getServerStataus(serverCode);
        if (serverStataus != null) {
            LinkedList<CpuUtilizationChunk> percentList = serverStataus.getCpuUtilization().getPercentList(chunks);
            return ListUtils.blur(percentList, 200, new LinkedList<CpuUtilizationChunk>());
        }
        logger.warn("can't find server with code:{} for cpu stats", serverCode);
        return null;
    }

    public RuntimeInfo getRuntimeInfo(Integer serverCode) {
        checkAuthenticated(false);
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

    public IConnectedServersState<SS, DS> getConnectedServersState() {
        return connectedServersState;
    }

    public IAlertService<SS> getAlertService() {
        return alertService;
    }

    public String getSettingsXML() {
        try {
            checkAuthenticated(true);
            logger.info("About to load configuration xml");
            return configurationService.getServersConfigXML();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public Boolean saveSettingsXML(String xml) {
        try {
            checkAuthenticated(true);
            logger.info("About to save xml:{}", xml);
            configurationService.saveServersConfigXML(xml);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public Boolean stopAlerts(boolean enable) {
        checkAuthenticated(true);
        return configurationService.stopAlerts(enable);
    }

    public IConfigurationService<SC> getConfigurationService() {
        return configurationService;
    }

}
