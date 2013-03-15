package com.smexec.monitor.server;

import java.util.LinkedList;

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.RefreshResult;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public class MonitoringServiceImpl
    extends
    AbstractMonitoringService<ServerStataus, ConnectedServer, RefreshResult<ConnectedServer>, FullRefreshResult<RefreshResult<ConnectedServer>, ConnectedServer>> {

    public MonitoringServiceImpl() {}

    @Override
    public FullRefreshResult<RefreshResult<ConnectedServer>, ConnectedServer> createFullRefreshResult(RefreshResult<ConnectedServer> refreshResult,
                                                                                                      LinkedList<Alert> alerts,
                                                                                                      String version) {
        return new FullRefreshResult<RefreshResult<ConnectedServer>, ConnectedServer>(refreshResult, alerts, version);
    }
}
