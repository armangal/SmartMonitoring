package com.smexec.monitor.server;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.server.services.alert.AlertService;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.server.utils.JMXGeneralStats;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.Version;
import com.smexec.monitor.shared.config.ClientConfigurations;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public abstract class AbstractMonitoringService<SS extends ServerStataus, CS extends ConnectedServer, RR extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<RR, CS>>
    extends RemoteServiceServlet {

    private static Logger logger = LoggerFactory.getLogger("MonitoringService");

    private static final String AUTHENTICATED = "authenticated";

    @Inject
    private IConnectedServersState<SS, CS, RR> connectedServersState;

    @Inject
    private JMXThreadDumpUtils jmxThreadDumpUtils;

    @Inject
    private AlertService alertService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private JMXGeneralStats jmxGeneralStats;

    public AbstractMonitoringService() {
        GuiceUtils.getInjector().injectMembers(this);
    }

    public FR refresh(int lastAlertId) {
        checkAuthenticated();
        RR refreshResult = connectedServersState.getRefreshResult();
        LinkedList<Alert> alertsAfter = alertService.getAlertsAfter(lastAlertId);

        return createFullRefreshResult(refreshResult, alertsAfter);
    }

    public abstract FR createFullRefreshResult(RR refreshResult, LinkedList<Alert> alerts);

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
        ServerStataus serverStataus = (ServerStataus) connectedServersState.getServerStataus(serverCode);
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
        ServersConfig sc = configurationService.getServersConfig();
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

    public LinkedList<MemoryUsage> getMemoryStats(Integer serverCode) {
        checkAuthenticated();
        ServerStataus serverStataus = (ServerStataus) connectedServersState.getServerStataus(serverCode);
        if (serverStataus != null) {
            return serverStataus.getMemoryUsage();
        }
        logger.warn("can't find server with code:{} for memory stats", serverCode);
        return null;
    }

    public LinkedList<CpuUtilizationChunk> getCpuUsageHistory(Integer serverCode) {
        checkAuthenticated();
        ServerStataus serverStataus = (ServerStataus) connectedServersState.getServerStataus(serverCode);
        if (serverStataus != null) {
            return serverStataus.getCpuUtilization().getPercentList();
        }
        logger.warn("can't find server with code:{} for cpu stats", serverCode);
        return null;
    }

    public RuntimeInfo getRuntimeInfo(Integer serverCode) {
        checkAuthenticated();
        ServerStataus serverStataus = (ServerStataus) connectedServersState.getServerStataus(serverCode);
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

    public IConnectedServersState<SS, CS, RR> getConnectedServersState() {
        return connectedServersState;
    }

    public ClientConfigurations getClientConfigurations() {

        return new ClientConfigurations(Version.getEnvName(), Version.getVersion());
    }
}
