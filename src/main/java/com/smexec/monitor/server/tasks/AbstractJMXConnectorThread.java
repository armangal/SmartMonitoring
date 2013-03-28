package com.smexec.monitor.server.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
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
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServerConfig;
import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.server.services.alert.AlertService;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.Version;
import com.smexec.monitor.shared.alert.AlertType;

public abstract class AbstractJMXConnectorThread<SS extends ServerStataus, CS extends ConnectedServer, RR extends AbstractRefreshResult<CS>>
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

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private AlertService alertService;

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
            configurationService.setServersConfig(serversConfig);

            Version.setEnvName(serversConfig.getName());

            if (serversConfig.getServers().size() > 0) {
                for (ServerConfig sc : serversConfig.getServers()) {
                    if (connectedServersState.getServerStataus(sc.getServerCode()) != null) {
                        // server found in memory
                        ServerStataus serverStataus = (ServerStataus) connectedServersState.getServerStataus(sc.getServerCode());

                        if (serverStataus.isConnected()) {
                            // no need to reconnect
                            continue;
                        }
                    }
                    // reconnect or make first connection attempt
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

        JMXConnector jmxConnector;
        SS ss = connectedServersState.getServerStataus(sc.getServerCode());
        if (ss == null) {
            // new server, first time
            ss = getServerStatus(sc);
            try {
                ConnectionSynch.connectionLock.lock();
                connectedServersState.addServer(ss);
            } finally {
                ConnectionSynch.connectionLock.unlock();
            }
        }

        try {

            logger.info("Coonecting to:{}", sc);
            JMXServiceURL u = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + sc.getIp() + ":" + sc.getJmxPort() + "/jmxrmi");
            if (sc.isAuthenticate()) {
                Map<String, String[]> env = new HashMap<String, String[]>(0);
                String[] creds = {sc.getUsername(), sc.getPassword()};
                env.put(JMXConnector.CREDENTIALS, creds);
                jmxConnector = JMXConnectorFactory.connect(u, env);
            } else {
                jmxConnector = JMXConnectorFactory.connect(u);
            }

            logger.info("Conneted to:{}", jmxConnector);

            /**
             * adding connection listener that should change the server status in case it's braking JMX
             * connection
             */
            jmxConnector.addConnectionNotificationListener(new NotificationListener() {

                @Override
                public void handleNotification(Notification notification, Object key) {
                    logger.info("Notification for key:{}", key);
                    logger.info("Notificatio n:{}" + notification);
                    if (notification.getType().contains("closed") || notification.getType().contains("failed")) {
                        ServerConfig sc = (ServerConfig) key;
                        SS serverStataus = connectedServersState.getServerStataus(sc.getServerCode());
                        serverStataus.resetOnDisconnect();

                        alertService.addAlert(new Alert("Server went down:" + sc.getName(),
                                                        sc.getServerCode(),
                                                        new Date().toString(),
                                                        AlertType.SERVER_DISCONNECTED));
                    }
                }
            }, null, sc);

            ss.setConnector(jmxConnector);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public abstract SS getServerStatus(final ServerConfig sc);
}
