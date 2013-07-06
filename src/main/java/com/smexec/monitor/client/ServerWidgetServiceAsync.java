/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smexec.monitor.client;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;
import com.smexec.monitor.shared.servers.ConnectedServer;
import com.smexec.monitor.shared.servers.ServersRefreshRequest;
import com.smexec.monitor.shared.servers.ServersRefreshResponse;

public interface ServerWidgetServiceAsync
    extends BasicMonitoringRefreshServiceAsync<ServersRefreshRequest, ServersRefreshResponse> {

    void getConnectedServer(Integer serverCode, AsyncCallback<ConnectedServer> callback);

    void getThreadDump(Integer serverCode, AsyncCallback<ThreadDump> callback);

    void getGCHistory(Integer serverCode, AsyncCallback<String> callback);

    void getMemoryStats(Integer serverCode, Integer chunks, AsyncCallback<LinkedList<MemoryUsage>> callback);

    void getCpuUsageHistory(Integer serverCode, Integer chunks, AsyncCallback<LinkedList<CpuUtilizationChunk>> callback);

    void getRuntimeInfo(Integer serverCode, AsyncCallback<RuntimeInfo> callback);

}
