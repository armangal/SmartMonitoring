package com.smexec.monitor.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.services.alert.AlertService;
import com.smexec.monitor.server.services.config.ConfigurationService;
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
        bind(new TypeLiteral<IConnectedServersState<ServerStataus, ConnectedServer, RefreshResult>>() {}).to(ConnectedServersState.class).in(Singleton.class);

        bind(IJMXConnectorThread.class).to(JMXConnectorThread.class).in(Singleton.class);
        bind(IStateUpdaterThread.class).to(StateUpdaterThread.class).in(Singleton.class);

        bind(JMXThreadDumpUtils.class).in(Singleton.class);
        bind(AlertService.class).in(Singleton.class);
        bind(ConfigurationService.class).in(Singleton.class);
        
    }

}
