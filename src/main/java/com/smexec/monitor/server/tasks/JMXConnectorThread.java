package com.smexec.monitor.server.tasks;

import javax.xml.bind.JAXBException;

import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;
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
        return new ServerStataus(sc);
    }
}
