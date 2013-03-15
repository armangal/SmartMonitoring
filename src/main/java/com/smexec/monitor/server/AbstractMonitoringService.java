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
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public abstract class AbstractMonitoringService<SS extends ServerStataus, CS extends ConnectedServer, RR extends RefreshResult<CS>, FR extends FullRefreshResult<RR, CS>>
    extends RemoteServiceServlet
    implements MonitoringService<CS, RR, FR> {

    private static Logger logger = LoggerFactory.getLogger("MonitoringService");

    private static final String AUTHENTICATED = "authenticated";

    @Inject
    private IConnectedServersState<SS, CS, RR> connectedServersState;

    @Inject
    private JMXThreadDumpUtils jmxThreadDumpUtils;

    public AbstractMonitoringService() {
        GuiceUtils.getInjector().injectMembers(this);
    }

    @Override
    public FR refresh(int lastAlertId) {
        checkAuthenticated();
        RR refreshResult = connectedServersState.getRefreshResult();
        LinkedList<Alert> alertsAfter = connectedServersState.getAlertsAfter(lastAlertId);

        return createFullRefreshResult(refreshResult, alertsAfter, Version.getVersion());
    }

    public abstract FR createFullRefreshResult(RR refreshResult, LinkedList<Alert> alerts, String version);

    @Override
    public String getThreadDump(Integer serverCode) {
        checkAuthenticated();
        SS ss = connectedServersState.getMap().get(serverCode);
        if (ss == null || !ss.isConnected()) {
            return new String("Server:" + serverCode + " not found or not connected.");

        } else {
            return jmxThreadDumpUtils.getThreadDump(ss);
        }
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

    public IConnectedServersState<SS, CS, RR> getConnectedServersState() {
        return connectedServersState;
    }
}
