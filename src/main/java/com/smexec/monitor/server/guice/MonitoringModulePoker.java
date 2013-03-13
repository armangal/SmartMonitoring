package com.smexec.monitor.server.guice;

import com.google.inject.Singleton;
import com.smexec.monitor.server.model.poker.ConnectedServersStatePoker;
import com.smexec.monitor.server.tasks.poker.JMXConnectorThreadPoker;
import com.smexec.monitor.server.tasks.poker.StateUpdaterThreadPoker;
import com.smexec.monitor.server.utils.poker.JMXChannelServerStats;
import com.smexec.monitor.server.utils.poker.JMXGameServerStats;
import com.smexec.monitor.server.utils.poker.JMXLobbyServerStats;

@SuppressWarnings({"rawtypes"})
public class MonitoringModulePoker
    extends MonitoringModule {

    @Override
    protected void configure() {
        super.configure();
        bind(JMXLobbyServerStats.class).in(Singleton.class);
        bind(JMXChannelServerStats.class).in(Singleton.class);
        bind(JMXGameServerStats.class).in(Singleton.class);

    }

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
