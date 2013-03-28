package com.smexec.monitor.server.tasks;

import javax.xml.bind.JAXBException;

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServerConfig;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

public class JMXConnectorThread
    extends AbstractJMXConnectorThread<ServerStataus, ConnectedServer, RefreshResult> {

    public JMXConnectorThread()
        throws JAXBException {
        super();
    }

    @Override
    public ServerStataus getServerStatus(ServerConfig sc) {
        // keeping history stats for 24 hours
        return new ServerStataus(sc, (24 * 60 * 3), (24 * 60 * 3));
    }
}
