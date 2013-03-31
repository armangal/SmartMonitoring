package com.smexec.monitor.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.RefreshResult;

public class Smartmonitoring
    extends AbstractEntryPoint<ConnectedServer, RefreshResult, FullRefreshResult<RefreshResult, ConnectedServer>> {

    private static final MonitoringServiceAsync<ConnectedServer, RefreshResult, FullRefreshResult<RefreshResult, ConnectedServer>> service = GWT.create(MonitoringServiceStd.class);

    public Smartmonitoring() {
        super(service);
        Log.debug("Smartmonitoring created");
    }
}
