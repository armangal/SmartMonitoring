package com.smexec.monitor.server;

import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.smexec.monitor.client.MonitoringService;
import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.ServersConfig;
import com.smexec.monitor.server.utils.JMXGetThreadDump;
import com.smexec.monitor.shared.RefreshResult;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MonitoringServiceImpl
    extends RemoteServiceServlet
    implements MonitoringService {

    private static final String AUTHENTICATED = "authenticated";

    @Override
    public RefreshResult refresh() {
        checkAuthenticated();
        return ConnectedServersState.getRefreshResult();
    }

    @Override
    public String getThreadDump(Integer serverCode) {
        checkAuthenticated();
        return JMXGetThreadDump.getThreadDump(serverCode);
    }

    @Override
    public String getGCHistory(Integer serverCode) {
        checkAuthenticated();
        ServerStataus serverStataus = ConnectedServersState.getMap().get(serverCode);
        if (serverStataus != null) {
            return serverStataus.getGCHistory();
        } else {
            return "Server not found:" + serverCode;
        }
    }

    @Override
    public Boolean authenticate(String userName, String password) {
        HttpSession session = getThreadLocalRequest().getSession();
        userName = userName.trim();
        password = password.trim();
        ServersConfig sc = ConnectedServersState.getServersConfig();
        if (sc.getUsername().equalsIgnoreCase(userName) && sc.getPassword().equals(password)) {
            session.setAttribute(AUTHENTICATED, Boolean.TRUE);
            return true;
        } else {
            session.removeAttribute(AUTHENTICATED);
            return false;
        }
    }

    private void checkAuthenticated() {
        HttpSession session = getThreadLocalRequest().getSession();
        Object auth = session.getAttribute(AUTHENTICATED);
        if (auth == null || !(auth instanceof Boolean) || (((Boolean) auth).booleanValue() == false)) {
            throw new SecurityException("Session not authenticated, please refresh the browser.");
        }
    }
}
