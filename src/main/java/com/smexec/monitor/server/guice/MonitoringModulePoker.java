package com.smexec.monitor.server.guice;

import com.smexec.monitor.server.model.poker.ConnectedServersStatePoker;
import com.smexec.monitor.server.tasks.poker.JMXConnectorThreadPoker;
import com.smexec.monitor.server.tasks.poker.StateUpdaterThreadPoker;

@SuppressWarnings({"rawtypes"})
public class MonitoringModulePoker
    extends MonitoringModule {

    public Class getConnectedServersStateClass() {
        return ConnectedServersStatePoker.class;
    }

    public Class getStateUpdaterThreadClass() {
        return StateUpdaterThreadPoker.class;
    }

    public Class getJMXConnectorThreadClass() {
        return JMXConnectorThreadPoker.class;
    }

}
