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

import org.smexec.SmartExecutor;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.server.services.alert.IAlertService;
import com.smexec.monitor.server.services.alert.StandardAlertService;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.server.services.config.IConfigurationService;
import com.smexec.monitor.server.services.mail.IMailService;
import com.smexec.monitor.server.services.mail.StandardMailService;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;
import com.smexec.monitor.server.tasks.IPeriodicalUpdater;
import com.smexec.monitor.server.tasks.IStateUpdaterThread;
import com.smexec.monitor.server.tasks.impl.PeriodicalUpdater;
import com.smexec.monitor.server.tasks.impl.ServersConnectorThread;
import com.smexec.monitor.server.tasks.impl.StateUpdaterThread;
import com.smexec.monitor.server.utils.IJMXGeneralStats;
import com.smexec.monitor.server.utils.JMXGeneralStats;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;

public class MonitoringModule
    extends AbstractModule {

    @Override
    protected void configure() {
        installSpecialServices();
        bind(SmartExecutor.class).toProvider(SmartExecutorProvider.class).in(Singleton.class);

        bind(JMXThreadDumpUtils.class).in(Singleton.class);
    }

    public void installSpecialServices() {
        bind(new TypeLiteral<IConfigurationService<ServersConfig>>() {}).toInstance(ConfigurationService.getInstance());

        bind(new TypeLiteral<IConnectedServersState<ServerStatus, DatabaseServer>>() {}).to(ConnectedServersState.class).in(Singleton.class);
        
        bind(new TypeLiteral<IMailService<ServerStatus>>() {}).to(StandardMailService.class).in(Singleton.class);

        bind(new TypeLiteral<IAlertService<ServerStatus>>() {}).to(StandardAlertService.class).in(Singleton.class);

        bind(new TypeLiteral<IJMXGeneralStats<ServerStatus>>() {}).to(JMXGeneralStats.class).in(Singleton.class);


        bind(IJMXConnectorThread.class).to(ServersConnectorThread.class).in(Singleton.class);
        bind(IStateUpdaterThread.class).to(StateUpdaterThread.class).in(Singleton.class);
        bind(IPeriodicalUpdater.class).to(PeriodicalUpdater.class).in(Singleton.class);
        install(new MongoDbModule<ServersConfig>(ConfigurationService.getInstance()));
    }

}
