package com.smexec.monitor.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.smexec.monitor.client.MonitoringService;
import com.smexec.monitor.server.model.ConnectedServersState;
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

}
