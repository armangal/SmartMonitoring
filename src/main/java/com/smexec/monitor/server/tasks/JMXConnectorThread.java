package com.smexec.monitor.server.tasks;

import javax.xml.bind.JAXBException;

import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;

public class JMXConnectorThread
    extends AbstractJMXConnectorThread<ServerStataus> {

    public JMXConnectorThread() throws JAXBException {
        super();
    }

    @Override
    public ServerStataus getServerStatus(ServerConfig sc) {
        return new ServerStataus(sc);
    }
}
