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
package org.clevermore.monitor.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.clevermore.monitor.shared.certificate.Certificate;
import org.clevermore.monitor.shared.errors.AuthenticationException;
import org.clevermore.monitor.shared.runtime.CpuUtilizationChunk;
import org.clevermore.monitor.shared.runtime.MemoryUsage;
import org.clevermore.monitor.shared.runtime.RuntimeInfo;
import org.clevermore.monitor.shared.runtime.ThreadDump;
import org.clevermore.monitor.shared.servers.ConnectedServer;
import org.clevermore.monitor.shared.servers.ServersRefreshRequest;
import org.clevermore.monitor.shared.servers.ServersRefreshResponse;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("../serverWidgetService")
public interface ServerWidgetService
    extends BasicMonitoringRefreshService<ServersRefreshRequest, ServersRefreshResponse> {

    ConnectedServer getConnectedServer(Integer serverCode)
        throws AuthenticationException;

    ThreadDump getThreadDump(Integer serverCode)
        throws AuthenticationException;

    String getGCHistory(Integer serverCode)
        throws AuthenticationException;

    LinkedList<MemoryUsage> getMemoryStats(Integer serverCode, Integer chunks)
        throws AuthenticationException;

    LinkedList<CpuUtilizationChunk> getCpuUsageHistory(Integer serverCode, Integer chunks)
        throws AuthenticationException;

    RuntimeInfo getRuntimeInfo(Integer serverCode)
        throws AuthenticationException;

    HashMap<String, List<Certificate>> getCertificates()
        throws AuthenticationException;

}
