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
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.config.ClientConfigurations;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;

public interface MonitoringServiceAsync<CS extends ConnectedServer, FR extends AbstractFullRefreshResult<CS>> {

    void refresh(int lastAlertId, AsyncCallback<FR> callback);

    void getThreadDump(Integer serverCode, AsyncCallback<ThreadDump> callback);

    void getGCHistory(Integer serverCode, AsyncCallback<String> callback);

    void authenticate(String userName, String password, AsyncCallback<Boolean> callback);

    void getMemoryStats(Integer serverCode, Integer chunks, AsyncCallback<LinkedList<MemoryUsage>> callback);

    void getCpuUsageHistory(Integer serverCode, Integer chunks, AsyncCallback<LinkedList<CpuUtilizationChunk>> callback);

    void getClientConfigurations(AsyncCallback<ClientConfigurations> callback);

    void getRuntimeInfo(Integer serverCode, AsyncCallback<RuntimeInfo> callback);

    void getSettingsXML(AsyncCallback<String> callback);

    void saveSettingsXML(String xml, AsyncCallback<Boolean> callback);
}
