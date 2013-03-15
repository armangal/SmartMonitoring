package com.smexec.monitor.server.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.ServersConfig;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

public abstract class AbstractJMXConnectorThread<SS extends ServerStataus, CS extends ConnectedServer, RR extends RefreshResult<CS>>
    implements IJMXConnectorThread {

    public static Logger logger = LoggerFactory.getLogger(AbstractJMXConnectorThread.class);

    private static ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private AtomicInteger num = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "JMX_CONNECTOR_" + num.getAndIncrement());
        }

    });

    private JAXBContext context;

    @Inject
    private IConnectedServersState<SS, CS, RR> connectedServersState;

    public AbstractJMXConnectorThread()
        throws JAXBException {
        logger.info("AbstractJMXConnectorThread");
        context = JAXBContext.newInstance(ServersConfig.class);
    }

    private class Connector
        implements Runnable {

        private ServerConfig sc;

        public Connector(ServerConfig sc) {
            super();
            this.sc = sc;
        }

        @Override
        public void run() {
            // new in mappings or try to connect to offline server reconnection
            connect(sc);
        }
    };

    public void run() {
        String location = System.getProperty("servers.config", "servers.xml");
        if (location == null || location.length() < 0) {
            location = "/opt/local/bex/conf/monitoring.xml";
        }

        logger.info("Loading configuraiotns:{}", location);
        try {
            File file = new File(location);
            if (!file.canRead()) {
                logger.error("Configuration file wasn't found at:{}", location);
            }
            logger.info("Configuration file:{}", file);

            InputStream configXML = new FileInputStream(file);
            ServersConfig serversConfig = (ServersConfig) context.createUnmarshaller().unmarshal(configXML);

            serversConfig.validate();

            logger.info("Initilized:{}", serversConfig);
            connectedServersState.setServersConfig(serversConfig);

            if (serversConfig.getServers().size() > 0) {
                for (ServerConfig sc : serversConfig.getServers()) {
                    if (connectedServersState.getMap().containsKey(sc.getServerCode())) {
                        ServerStataus serverStataus = (ServerStataus) connectedServersState.getMap().get(sc.getServerCode());

                        if (serverStataus.isConnected()) {
                            continue;
                        }
                    }
                    threadPool.execute(new Connector(sc));
                }
            }

            logger.info("Connection loop finished");

        } catch (Throwable e) {
            logger.error("Error loading config:{}", e.getMessage());
            logger.error(e.getMessage(), e);
            Runtime.getRuntime().exit(1);
        }
    }

    private void connect(final ServerConfig sc) {

        JMXConnector c;
        SS ss = getServerStatus(sc);

        try {

            logger.info("Coonecting to:{}", sc);
            JMXServiceURL u = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + sc.getIp() + ":" + sc.getJmxPort() + "/jmxrmi");
            if (sc.isAuthenticate()) {
                Map<String, String[]> env = new HashMap<String, String[]>(0);
                String[] creds = {sc.getUsername(), sc.getPassword()};
                env.put(JMXConnector.CREDENTIALS, creds);
                c = JMXConnectorFactory.connect(u, env);
            } else {
                c = JMXConnectorFactory.connect(u);
            }

            logger.info("Conneted to:{}", c);

            c.addConnectionNotificationListener(new NotificationListener() {

                @Override
                public void handleNotification(Notification notification, Object key) {
                    logger.info("Notification for key:{}", key);
                    logger.info("Notificatio n:{}" + notification);
                    if (notification.getType().contains("closed") || notification.getType().contains("failed")) {
                        connectedServersState.getMap().remove(((ServerConfig) key).getServerCode());
                    }
                }
            }, null, sc);

            ss.setConnector(c);
            ss.setConnected(true);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            ConnectionSynch.connectionLock.lock();
            connectedServersState.getMap().put(sc.getServerCode(), ss);
        } finally {
            ConnectionSynch.connectionLock.unlock();
        }

    }

    @Override
    public abstract SS getServerStatus(final ServerConfig sc);
}
