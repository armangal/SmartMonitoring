package com.smexec.monitor.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.RefreshResult;

@RemoteServiceRelativePath("../mainService")
public interface MonitoringServiceStd
    extends MonitoringService<ConnectedServer, RefreshResult, FullRefreshResult<RefreshResult, ConnectedServer>> {}
