package com.smexec.monitor.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.smexec.monitor.server.model.poker.ConnectedServersStatePoker;
import com.smexec.monitor.server.tasks.JMXConnectorThread;
import com.smexec.monitor.server.tasks.poker.StateUpdaterThreadPoker;
import com.smexec.monitor.server.utils.JMXThreadDumpUtils;

public class MonitoringModule
    extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConnectedServersStatePoker.class).in(Singleton.class);
        bind(JMXConnectorThread.class).in(Singleton.class);
        bind(StateUpdaterThreadPoker.class).in(Singleton.class);

        bind(JMXThreadDumpUtils.class).in(Singleton.class);
    }
}
