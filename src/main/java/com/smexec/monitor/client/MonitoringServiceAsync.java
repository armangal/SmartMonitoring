package com.smexec.monitor.client;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.MemoryUsage;

public interface MonitoringServiceAsync {

    void refresh(int lastAlertId, AsyncCallback<FullRefreshResult> callback);

    void getThreadDump(Integer serverCode, AsyncCallback<String> callback);

    void getGCHistory(Integer serverCode, AsyncCallback<String> callback);

    void authenticate(String userName, String password, AsyncCallback<Boolean> callback);

    void getMemoryStats(Integer serverCode, AsyncCallback<LinkedList<MemoryUsage>> callback);

    void getCpuUsageHistory(Integer serverCode, AsyncCallback<LinkedList<Double>> callback);
}
