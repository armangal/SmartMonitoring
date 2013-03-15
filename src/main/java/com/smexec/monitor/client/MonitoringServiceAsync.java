package com.smexec.monitor.client;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.MemoryUsage;
import com.smexec.monitor.shared.RefreshResult;

public interface MonitoringServiceAsync<CS extends ConnectedServer, R extends RefreshResult<CS>, FR extends FullRefreshResult<R, CS>> {

    void refresh(int lastAlertId, AsyncCallback<FR> callback);

    void getThreadDump(Integer serverCode, AsyncCallback<String> callback);

    void getGCHistory(Integer serverCode, AsyncCallback<String> callback);

    void authenticate(String userName, String password, AsyncCallback<Boolean> callback);

    void getMemoryStats(Integer serverCode, AsyncCallback<LinkedList<MemoryUsage>> callback);

    void getCpuUsageHistory(Integer serverCode, AsyncCallback<LinkedList<Double>> callback);
}
