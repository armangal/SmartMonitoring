package com.smexec.monitor.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smexec.monitor.shared.RefreshResult;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface MonitoringServiceAsync {

    void connect(String address, AsyncCallback<List<String>> callback);

    void refresh(AsyncCallback<RefreshResult> callback);
}
