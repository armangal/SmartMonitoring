package com.smexec.monitor.client;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.MemoryUsage;
import com.smexec.monitor.shared.RefreshResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("../mainService")
public interface MonitoringService<CS extends ConnectedServer, R extends RefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends RemoteService {

    FR refresh(int lastAlertId);

    String getThreadDump(Integer serverCode);

    String getGCHistory(Integer serverCode);

    Boolean authenticate(String userName, String password);

    LinkedList<MemoryUsage> getMemoryStats(Integer serverCode);

    LinkedList<Double> getCpuUsageHistory(Integer serverCode);
}
