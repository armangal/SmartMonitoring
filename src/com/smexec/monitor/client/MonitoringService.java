package com.smexec.monitor.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.smexec.monitor.shared.RefreshResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("mainService")
public interface MonitoringService
    extends RemoteService {

    RefreshResult refresh();
    
    String getThreadDump(Integer serverCode);
}
