package com.smexec.monitor.server.tasks;

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServerConfig;
import com.smexec.monitor.server.model.config.ServerGroup;

public interface IJMXConnectorThread
    extends Runnable {

    ServerStataus getServerStatus(final ServerConfig sc, final ServerGroup serverGroup);
}
