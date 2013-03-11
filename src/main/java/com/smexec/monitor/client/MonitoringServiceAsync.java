package com.smexec.monitor.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smexec.monitor.shared.FullRefreshResult;

public interface MonitoringServiceAsync {

    void refresh(int lastAlertId, AsyncCallback<FullRefreshResult> callback);

    void getThreadDump(Integer serverCode, AsyncCallback<String> callback);

    void getGCHistory(Integer serverCode, AsyncCallback<String> callback);

    void authenticate(String userName, String password, AsyncCallback<Boolean> callback);
}