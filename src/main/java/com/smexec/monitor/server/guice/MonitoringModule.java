package com.smexec.monitor.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;
import com.smexec.monitor.server.tasks.IStateUpdaterThread;
import com.smexec.monitor.server.tasks.JMXConnectorThread;
import com.smexec.monitor.server.tasks.StateUpdaterThread;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MonitoringModule<SS extends ServerStataus, CS extends ConnectedServer, RR extends RefreshResult<CS>>
    extends AbstractModule {

    @Override
    protected void configure() {
        bind(getIconnectedKey()).to(getConnectedServersStateClass()).in(Singleton.class);

        bind(IJMXConnectorThread.class).to(getJMXConnectorThreadClass()).in(Singleton.class);
        bind(IStateUpdaterThread.class).to(getStateUpdaterThreadClass()).in(Singleton.class);

        bind(JMXThreadDumpUtils.class).in(Singleton.class);
    }

    public TypeLiteral<?> getIconnectedKey() {
        return new TypeLiteral<IConnectedServersState<ServerStataus, ConnectedServer, RefreshResult<ConnectedServer>>>() {};
    }

    public Class getJMXConnectorThreadClass() {
        return JMXConnectorThread.class;
    }

    public Class getStateUpdaterThreadClass() {
        return StateUpdaterThread.class;
    }

    public Class getConnectedServersStateClass() {
        return ConnectedServersState.class;
    }

}
