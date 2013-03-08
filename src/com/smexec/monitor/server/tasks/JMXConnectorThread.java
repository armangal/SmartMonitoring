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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.ServersConfig;

public class JMXConnectorThread
    implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(JMXConnectorThread.class);

    private static ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private AtomicInteger num = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "JMX_CONNECTOR_" + num.getAndIncrement());
        }

    });

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

        logger.info("Loading configuraiotns:{}", location);
        try {
            File file = new File(location);
            if (!file.canRead()) {
                logger.error("Configuration file wasn't found at:{}", location);
            }
            logger.info("Configuration file:{}", file);

            InputStream configXML = new FileInputStream(file);
            JAXBContext context = JAXBContext.newInstance(ServersConfig.class);
            ServersConfig serversConfig = (ServersConfig) context.createUnmarshaller().unmarshal(configXML);

            serversConfig.validate();

            logger.info("Initilized:{}", serversConfig);
            ConnectedServersState.setServersConfig(serversConfig);

            if (serversConfig.getServers().size() > 0) {
                for (ServerConfig sc : serversConfig.getServers()) {
                    if (ConnectedServersState.getMap().containsKey(sc.getServerCode())) {
                        ServerStataus serverStataus = ConnectedServersState.getMap().get(sc.getServerCode());

                        if (serverStataus.isConnected()) {
                            continue;
                        }
                    }
                    threadPool.execute(new Connector(sc));
                }
            }

            logger.info("Connection loop finished");

        } catch (Throwable e) {
            logger.error("Error loading config:{}" + e.getMessage());
            logger.error(e.getMessage(), e);
            Runtime.getRuntime().exit(1);
        }
    }

    private void connect(final ServerConfig sc) {

        JMXConnector c;
        ServerStataus ss = new ServerStataus(sc);

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
                        ConnectedServersState.getMap().remove(((ServerConfig) key).getServerCode());
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
            ConnectedServersState.getMap().put(sc.getServerCode(), ss);
        } finally {
            ConnectionSynch.connectionLock.unlock();
        }

    };
}
