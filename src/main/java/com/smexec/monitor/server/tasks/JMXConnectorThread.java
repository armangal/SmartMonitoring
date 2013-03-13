package com.smexec.monitor.server.tasks;

import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;

public class JMXConnectorThread
    extends AbstractJMXConnectorThread<ServerStataus> {

    @Override
    public ServerStataus getServerStatus(ServerConfig sc) {
        return new ServerStataus(sc);
    }
}
