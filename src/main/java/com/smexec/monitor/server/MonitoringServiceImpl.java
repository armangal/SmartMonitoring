package com.smexec.monitor.server;

import java.util.LinkedList;

import com.smexec.monitor.client.MonitoringServiceStd;
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
    extends AbstractMonitoringService<ServerStataus, ConnectedServer, RefreshResult, FullRefreshResult<RefreshResult, ConnectedServer>>
    implements MonitoringServiceStd {

    public MonitoringServiceImpl() {}

    @Override
    public FullRefreshResult<RefreshResult, ConnectedServer> createFullRefreshResult(RefreshResult refreshResult, LinkedList<Alert> alerts) {
        return new FullRefreshResult<RefreshResult, ConnectedServer>(refreshResult, alerts);
    }
}
