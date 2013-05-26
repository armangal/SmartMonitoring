/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smexec.monitor.server.tasks.impl;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.ServerConfig;
import com.smexec.monitor.server.model.config.ServerGroup;
import com.smexec.monitor.server.model.config.AbstractServersConfig;
import com.smexec.monitor.server.services.alert.IAlertService;
import com.smexec.monitor.server.services.config.IConfigurationService;
import com.smexec.monitor.server.tasks.ConnectionSynch;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.alert.AlertType;

public abstract class AbstractJMXConnectorThread<SS extends ServerStatus, CS extends ConnectedServer, SC extends AbstractServersConfig>
    implements IJMXConnectorThread {

    public static Logger logger = LoggerFactory.getLogger("JMXConnectorThread");

    private static ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private AtomicInteger num = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "JMX_CONNECTOR_" + num.getAndIncrement());
        }

    });

    @Inject
    private IConnectedServersState<SS, CS> connectedServersState;

    @Inject
    private IConfigurationService<SC> configurationService;

    @Inject
    private IAlertService<SS> alertService;

    public AbstractJMXConnectorThread() {
        logger.info("AbstractJMXConnectorThread");
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
            String oldName = Thread.currentThread().getName();
            try {
                Thread.currentThread().setName("CONN_" + sc.getName());
                connect(sc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                Thread.currentThread().setName(oldName);
            }
        }
    };

    public void run() {
        try {
            logger.info("Connection loop staeted");
            SC serversConfig = configurationService.getServersConfig();
            if (serversConfig.getServers().size() > 0) {
                for (ServerConfig sc : serversConfig.getServers()) {
                    if (connectedServersState.getServerStataus(sc.getServerCode()) != null) {
                        // server found in memory
                        ServerStatus serverStataus = (ServerStatus) connectedServersState.getServerStataus(sc.getServerCode());

                        if (serverStataus.isConnected()) {
                            // no need to reconnect
                            ServerGroup serverGroup = configurationService.getServersConfig().getServerGroup(sc.getServerGroup());
                            serverStataus.updateSomeServerConfigs(sc);
                            serverStataus.setServerGroup(serverGroup);
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
        boolean sendAlert = false;
        ServerGroup serverGroup = configurationService.getServersConfig().getServerGroup(sc.getServerGroup());
        if (ss == null) {
            // new server, first time
            ss = getServerStatus(sc, serverGroup);
            try {
                ConnectionSynch.connectionLock.lock();
                connectedServersState.addServer(ss);
            } finally {
                ConnectionSynch.connectionLock.unlock();
            }
        } else {
            sendAlert = true;// we send alerts is case the server is reconnected, to keep posted updated
            ss.setServerGroup(serverGroup);
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

            if (sendAlert) {
                Alert alert = alertService.createAlert("(-: Server RE-STARTED :-)",
                                                       "",
                                                       sc.getServerCode(),
                                                       sc.getName(),
                                                       new Date().getTime(),
                                                       AlertType.SERVER_CONNECTED);
                alertService.addAlert(alert, ss);
            }
            /**
             * adding connection listener that should change the server status in case it's braking JMX
             * connection
             */
            jmxConnector.addConnectionNotificationListener(new NotificationListener() {

                @Override
                public void handleNotification(Notification notification, Object key) {
                    logger.info("Notification for key:{}", key);
                    logger.info("Notification:{}" + notification);
                    ServerConfig sc = (ServerConfig) key;
                    SS serverStataus = connectedServersState.getServerStataus(sc.getServerCode());
                    if (notification.getType().contains("closed")) {
                        Alert alert = alertService.createAlert("!!! Server went DOWN !!!",
                                                               "",
                                                               sc.getServerCode(),
                                                               sc.getName(),
                                                               new Date().getTime(),
                                                               AlertType.SERVER_DISCONNECTED);
                        alertService.addAlert(alert, serverStataus);

                        serverStataus.resetOnDisconnect();
                    }
                    if (notification.getType().contains("failed")) {
                        Alert alert = alertService.createAlert("Failed to make JMX comm. with server!",
                                                               "",
                                                               sc.getServerCode(),
                                                               sc.getName(),
                                                               new Date().getTime(),
                                                               AlertType.SERVER_COMM_FAILED);
                        alertService.addAlert(alert, serverStataus);

                    }
                }
            },
                                                           null,
                                                           sc);

            ss.setConnector(jmxConnector);
            ss.setServerConfig(sc);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public abstract SS getServerStatus(final ServerConfig sc, final ServerGroup serverGroup);
}
