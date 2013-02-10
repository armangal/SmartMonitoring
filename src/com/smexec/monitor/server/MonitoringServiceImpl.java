package com.smexec.monitor.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.smexec.monitor.client.MonitoringService;
import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.utils.JMXGetThreadDump;
import com.smexec.monitor.shared.RefreshResult;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MonitoringServiceImpl
    extends RemoteServiceServlet
    implements MonitoringService {

    @Override
    public RefreshResult refresh() {

        return ConnectedServersState.getRefreshResult();
    }

    @Override
    public String getThreadDump(Integer serverCode) {

        return JMXGetThreadDump.getThreadDump(serverCode);
    }

    @Override
    public String getGCHistory(Integer serverCode) {
        ServerStataus serverStataus = ConnectedServersState.getMap().get(serverCode);
        if (serverStataus != null) {
            return serverStataus.getGCHistory();
        } else {
            return "Server not found:" + serverCode;
        }
    }

}
