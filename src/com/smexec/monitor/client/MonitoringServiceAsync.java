package com.smexec.monitor.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smexec.monitor.shared.RefreshResult;

public interface MonitoringServiceAsync {

    void refresh(AsyncCallback<RefreshResult> callback);
}
