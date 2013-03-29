package com.smexec.monitor.client;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.config.ClientConfigurations;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;

public interface MonitoringServiceAsync<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>> {

    void refresh(int lastAlertId, AsyncCallback<FR> callback);

    void getThreadDump(Integer serverCode, AsyncCallback<ThreadDump> callback);

    void getGCHistory(Integer serverCode, AsyncCallback<String> callback);

    void authenticate(String userName, String password, AsyncCallback<Boolean> callback);

    void getMemoryStats(Integer serverCode, Integer chunks, AsyncCallback<LinkedList<MemoryUsage>> callback);

    void getCpuUsageHistory(Integer serverCode, Integer chunks, AsyncCallback<LinkedList<CpuUtilizationChunk>> callback);

    void getClientConfigurations(AsyncCallback<ClientConfigurations> callback);

    void getRuntimeInfo(Integer serverCode, AsyncCallback<RuntimeInfo> callback);
}
