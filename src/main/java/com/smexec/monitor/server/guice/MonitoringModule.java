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
package com.smexec.monitor.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.services.alert.AlertService;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.server.services.mail.MailService;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;
import com.smexec.monitor.server.tasks.IStateUpdaterThread;
import com.smexec.monitor.server.tasks.JMXConnectorThread;
import com.smexec.monitor.server.tasks.StateUpdaterThread;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

public class MonitoringModule<SS extends ServerStataus, CS extends ConnectedServer>
    extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfigurationService.class).asEagerSingleton();
        
        bind(new TypeLiteral<IConnectedServersState<ServerStataus, ConnectedServer, RefreshResult>>() {}).to(ConnectedServersState.class).in(Singleton.class);

        bind(IJMXConnectorThread.class).to(JMXConnectorThread.class).in(Singleton.class);
        bind(IStateUpdaterThread.class).to(StateUpdaterThread.class).in(Singleton.class);

        bind(JMXThreadDumpUtils.class).in(Singleton.class);
        bind(AlertService.class).in(Singleton.class);
        bind(MailService.class).in(Singleton.class);
        
    }

}
