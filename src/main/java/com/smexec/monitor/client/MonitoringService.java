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

import com.google.gwt.user.client.rpc.RemoteService;
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.config.ClientConfigurations;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;

/**
 * The client side stub for the RPC service.
 */

public interface MonitoringService<CS extends ConnectedServer, FR extends AbstractFullRefreshResult<CS>, CC extends ClientConfigurations>
    extends RemoteService {

    FR refresh();

    ThreadDump getThreadDump(Integer serverCode);

    String getGCHistory(Integer serverCode);

    Boolean authenticate(String userName, String password);

    LinkedList<MemoryUsage> getMemoryStats(Integer serverCode, Integer chunks);

    LinkedList<CpuUtilizationChunk> getCpuUsageHistory(Integer serverCode, Integer chunks);

    CC getClientConfigurations();

    RuntimeInfo getRuntimeInfo(Integer serverCode);
    
    String getSettingsXML();
    
    Boolean saveSettingsXML(String xml);
    
    Boolean stopAlerts(boolean enable);
    
    LinkedList<Alert> getAlerts(final int lastAlertId);
    
    void logout();
}
