package com.smexec.monitor.server;

import java.util.LinkedList;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.smexec.monitor.client.MonitoringService;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.ServersConfig;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;
import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.MemoryUsage;
import com.smexec.monitor.shared.RefreshResult;
import com.smexec.monitor.shared.Version;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MonitoringServiceImpl
    extends RemoteServiceServlet
    implements MonitoringService {

    private static Logger logger = LoggerFactory.getLogger(MonitoringServiceImpl.class);

    private static final String AUTHENTICATED = "authenticated";

    @Inject
    private IConnectedServersState connectedServersState;

    @Inject
    private JMXThreadDumpUtils jmxThreadDumpUtils;

    public MonitoringServiceImpl() {
        GuiceUtils.getInjector().injectMembers(this);
    }

    @Override
    public FullRefreshResult refresh(int lastAlertId) {
        checkAuthenticated();
        RefreshResult<ConnectedServer> refreshResult = connectedServersState.getRefreshResult();
        LinkedList<Alert> alertsAfter = connectedServersState.getAlertsAfter(lastAlertId);
        FullRefreshResult frr = new FullRefreshResult(refreshResult, alertsAfter, Version.getVersion());
        return frr;
    }

    @Override
    public String getThreadDump(Integer serverCode) {
        checkAuthenticated();
        return jmxThreadDumpUtils.getThreadDump(serverCode);
    }

    @Override
    public String getGCHistory(Integer serverCode) {
        checkAuthenticated();
        ServerStataus serverStataus = (ServerStataus) connectedServersState.getMap().get(serverCode);
        if (serverStataus != null) {
            return serverStataus.getGCHistory();
        } else {
            return "Server not found:" + serverCode;
        }
    }

    @Override
    public Boolean authenticate(String userName, String password) {
        logger.info("Authnticating user:{}, pass:{}", userName, password);

        HttpSession session = getThreadLocalRequest().getSession();
        userName = userName.trim();
        password = password.trim();
        ServersConfig sc = connectedServersState.getServersConfig();
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

    private void checkAuthenticated() {
        HttpSession session = getThreadLocalRequest().getSession();
        Object auth = session.getAttribute(AUTHENTICATED);
        if (auth == null || !(auth instanceof Boolean) || (((Boolean) auth).booleanValue() == false)) {
            logger.info("Session is not authenticated");

            throw new SecurityException("Session not authenticated, please refresh the browser.");
        }
    }

    @Override
    public LinkedList<MemoryUsage> getMemoryStats(Integer serverCode) {
        checkAuthenticated();
        ServerStataus serverStataus = (ServerStataus) connectedServersState.getMap().get(serverCode);
        if (serverStataus != null) {
            return serverStataus.getMemoryUsage();
        }
        logger.warn("can't find server with code:{} for memory stats", serverCode);
        return null;
    }

    @Override
    public LinkedList<Double> getCpuUsageHistory(Integer serverCode) {
        checkAuthenticated();
        ServerStataus serverStataus = (ServerStataus) connectedServersState.getMap().get(serverCode);
        if (serverStataus != null) {
            return serverStataus.getCpuUtilization().getPercentList();
        }
        logger.warn("can't find server with code:{} for cpu stats", serverCode);
        return null;
    }
}
