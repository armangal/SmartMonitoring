package com.smexec.monitor.server.tasks;

import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;

public interface IJMXConnectorThread
    extends Runnable {

    ServerStataus getServerStatus(final ServerConfig sc);
}
