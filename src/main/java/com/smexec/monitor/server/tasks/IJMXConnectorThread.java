package com.smexec.monitor.server.tasks;

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServerConfig;

public interface IJMXConnectorThread
    extends Runnable {

    ServerStataus getServerStatus(final ServerConfig sc);
}
