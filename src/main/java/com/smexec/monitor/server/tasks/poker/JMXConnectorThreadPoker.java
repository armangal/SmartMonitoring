package com.smexec.monitor.server.tasks.poker;

import javax.xml.bind.JAXBException;

import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.poker.ServerStatausPoker;
import com.smexec.monitor.server.tasks.AbstractJMXConnectorThread;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;

public class JMXConnectorThreadPoker
    extends AbstractJMXConnectorThread<ServerStatausPoker>
    implements IJMXConnectorThread {

    public JMXConnectorThreadPoker() throws JAXBException {
        super();
        logger.info("JMXConnectorThreadPoker");
    }

    @Override
    public ServerStatausPoker getServerStatus(ServerConfig sc) {
        return new ServerStatausPoker(sc);
    }
}
