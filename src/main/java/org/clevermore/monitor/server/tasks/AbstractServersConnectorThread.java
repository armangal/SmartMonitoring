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
package org.clevermore.monitor.server.tasks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.clevermore.SmartExecutor;
import org.clevermore.TaskMetadata;
import org.clevermore.monitor.server.constants.SmartPoolsMonitoring;
import org.clevermore.monitor.server.model.DatabaseServer;
import org.clevermore.monitor.server.model.IConnectedServersState;
import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.server.model.config.AbstractServersConfig;
import org.clevermore.monitor.server.model.config.DatabaseConfig;
import org.clevermore.monitor.server.model.config.ServerConfig;
import org.clevermore.monitor.server.model.config.ServerGroup;
import org.clevermore.monitor.server.services.alert.IAlertService;
import org.clevermore.monitor.server.services.config.IConfigurationService;
import org.clevermore.monitor.shared.alert.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * main connection manager, makes sure that all servers that are configured in configuration XML are actually
 * connected
 * 
 * @author armang
 * @param <SS>
 * @param <CS>
 * @param <SC>
 */
public abstract class AbstractServersConnectorThread<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    implements IJMXConnectorThread {

    public static Logger logger = LoggerFactory.getLogger("JMXConnectorThread");

    @Inject
    private SmartExecutor smartExecutor;

    @Inject
    private IConnectedServersState<SS, DS> connectedServersState;

    @Inject
    private IConfigurationService<SC> configurationService;

    @Inject
    private IAlertService<SS> alertService;

    public AbstractServersConnectorThread() {
        logger.info("AbstractJMXConnectorThread");
    }

    private class DbConnector
        implements Runnable {

        private DatabaseConfig dc;

        public DbConnector(DatabaseConfig dc) {
            super();
            this.dc = dc;
        }

        @Override
        public void run() {
            try {
                connectDb(dc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    public abstract DS getDatabaseServer(DatabaseConfig dc);

    public void connectDb(DatabaseConfig dc)
        throws ClassNotFoundException, SQLException {
        DS ds = connectedServersState.getDatabaseServer(dc.getName());
        if (ds == null) {
            ds = getDatabaseServer(dc);
            connectedServersState.addDatabaseServer(ds);
        }
        logger.info("Coonecting to:{}", dc);
        Connection connection = null;
        switch (dc.getType()) {
            case ORACLE:
                Class.forName("oracle.jdbc.OracleDriver");
                connection = DriverManager.getConnection("jdbc:oracle:thin:@//" + dc.getIp() + ":" + dc.getPort() + "/" + dc.getService(),
                                                         dc.getUser(),
                                                         dc.getPassword());

                break;
            default:
                throw new RuntimeException("Unknown database type:" + dc.getType());
        }

        ds.setConnection(connection);
        ds.setDatabaseConfig(dc);
        logger.info("Conneted to DB:{}", connection);

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
            try {
                connect(sc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
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
                    logger.info("Configured server:{} was found disconnected, scheduling reconnection attempt.", sc);
                    smartExecutor.execute(new Connector(sc),
                                          TaskMetadata.newMetadata(SmartPoolsMonitoring.CONNECTOR, "CONN_" + sc.getName(), "CC_" + sc.getName()));
                }
            }

            logger.info("JMX Connection watchdog loop finished, Starting database check.");

            for (DatabaseConfig dc : serversConfig.getDatabases()) {
                DS ds = connectedServersState.getDatabaseServer(dc.getName());
                if (ds == null || !ds.isConnected()) {
                    // connect
                    logger.info("DB server: {} was found disconnected, scheduling reconnection task", dc);
                    smartExecutor.execute(new DbConnector(dc),
                                          TaskMetadata.newMetadata(SmartPoolsMonitoring.CONNECTOR, "CONN_DB_" + dc.getName(), "DC_" + dc.getName()));
                } else if (!ds.getDatabaseConfig().getUser().equals(dc.getUser())) {
                    // settings changed username, reconnect
                    try {
                        logger.info("User name for DB:{} chenaged, reconnecting.", dc);
                        ds.getConnection().close();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    smartExecutor.execute(new DbConnector(dc),
                                          TaskMetadata.newMetadata(SmartPoolsMonitoring.CONNECTOR, "CONN_DB_" + dc.getName(), "DC_" + dc.getName()));
                }
            }

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
                alertService.createAndAddAlert("(-: Server RE-STARTED :-)",
                                               "",
                                               sc.getServerCode(),
                                               sc.getName(),
                                               new Date().getTime(),
                                               AlertType.SERVER_CONNECTED,
                                               ss);
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
                        alertService.createAndAddAlert("!!! Server went DOWN !!!",
                                                       "",
                                                       sc.getServerCode(),
                                                       sc.getName(),
                                                       new Date().getTime(),
                                                       AlertType.SERVER_DISCONNECTED,
                                                       serverStataus);

                        serverStataus.resetOnDisconnect();
                    }
                    if (notification.getType().contains("failed")) {
                        alertService.createAndAddAlert("Failed to make JMX comm. with server!",
                                                       "",
                                                       sc.getServerCode(),
                                                       sc.getName(),
                                                       new Date().getTime(),
                                                       AlertType.SERVER_COMM_FAILED,
                                                       serverStataus);

                    }
                }
            }, null, sc);

            ss.setConnector(jmxConnector);
            ss.setServerConfig(sc);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public abstract SS getServerStatus(final ServerConfig sc, final ServerGroup serverGroup);
}
