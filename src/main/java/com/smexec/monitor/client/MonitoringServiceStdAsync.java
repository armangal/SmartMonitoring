package com.smexec.monitor.client;

import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.RefreshResult;

public interface MonitoringServiceStdAsync
    extends MonitoringServiceAsync<ConnectedServer, RefreshResult, FullRefreshResult<RefreshResult, ConnectedServer>> {}
