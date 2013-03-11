package com.smexec.monitor.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.smexec.monitor.shared.FullRefreshResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("../mainService")
public interface MonitoringService
    extends RemoteService {

    FullRefreshResult refresh(int lastAlertId);
    
    String getThreadDump(Integer serverCode);
    
    String getGCHistory(Integer serverCode);
    
    Boolean authenticate(String userName, String password);
}
