package com.smexec.monitor.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;
import com.smexec.monitor.server.tasks.IStateUpdaterThread;
import com.smexec.monitor.server.tasks.AbstractJMXConnectorThread;
import com.smexec.monitor.server.tasks.StateUpdaterThread;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MonitoringModule
    extends AbstractModule {

    @Override
    protected void configure() {
        bind(IConnectedServersState.class).to(getConnectedServersStateClass()).in(Singleton.class);
        bind(IJMXConnectorThread.class).to(getJMXConnectorThreadClass()).in(Singleton.class);
        bind(IStateUpdaterThread.class).to(getStateUpdaterThreadClass()).in(Singleton.class);

        bind(JMXThreadDumpUtils.class).in(Singleton.class);
    }

    public  Class getJMXConnectorThreadClass() {
        return AbstractJMXConnectorThread.class;
    }

    public Class getStateUpdaterThreadClass() {
        return StateUpdaterThread.class;
    }

    public Class getConnectedServersStateClass() {
        return ConnectedServersState.class;
    }

}
