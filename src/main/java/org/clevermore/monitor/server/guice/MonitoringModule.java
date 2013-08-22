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
package org.clevermore.monitor.server.guice;

import org.clevermore.SmartExecutor;
import org.clevermore.monitor.server.model.ConnectedServersState;
import org.clevermore.monitor.server.model.DatabaseServer;
import org.clevermore.monitor.server.model.IConnectedServersState;
import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.server.model.config.ServersConfig;
import org.clevermore.monitor.server.services.alert.IAlertService;
import org.clevermore.monitor.server.services.alert.StandardAlertService;
import org.clevermore.monitor.server.services.config.ConfigurationService;
import org.clevermore.monitor.server.services.config.IConfigurationService;
import org.clevermore.monitor.server.services.mail.IMailService;
import org.clevermore.monitor.server.services.mail.StandardMailService;
import org.clevermore.monitor.server.tasks.ICertificateScanner;
import org.clevermore.monitor.server.tasks.IJMXConnectorThread;
import org.clevermore.monitor.server.tasks.IPeriodicalUpdater;
import org.clevermore.monitor.server.tasks.IStateUpdaterThread;
import org.clevermore.monitor.server.tasks.impl.CertificateScanner;
import org.clevermore.monitor.server.tasks.impl.PeriodicalUpdater;
import org.clevermore.monitor.server.tasks.impl.ServersConnectorThread;
import org.clevermore.monitor.server.tasks.impl.StateUpdaterThread;
import org.clevermore.monitor.server.utils.IJMXGeneralStats;
import org.clevermore.monitor.server.utils.JMXGeneralStats;
import org.clevermore.monitor.server.utils.JMXThreadDumpUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

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
        bind(ICertificateScanner.class).to(CertificateScanner.class).in(Singleton.class);
        
        install(new MongoDbModule<ServersConfig>(ConfigurationService.getInstance()));
    }

}
