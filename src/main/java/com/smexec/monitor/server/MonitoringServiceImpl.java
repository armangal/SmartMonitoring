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
package com.smexec.monitor.server;

import com.smexec.monitor.client.MonitoringServiceStd;
import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.shared.config.ClientConfigurations;
import com.smexec.monitor.shared.config.Version;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public class MonitoringServiceImpl
    extends AbstractMonitoringService<ServerStatus, ServersConfig, DatabaseServer>
    implements MonitoringServiceStd {

    public MonitoringServiceImpl() {}

    public ClientConfigurations getClientConfigurations() {

        return new ClientConfigurations(Version.getEnvName(), Version.getVersion(), getConfigurationService().getServersConfig().getAlertsConfig().isEnabled());
    }

}
