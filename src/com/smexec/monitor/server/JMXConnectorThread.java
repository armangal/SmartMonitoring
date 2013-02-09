package com.smexec.monitor.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.bind.JAXBContext;

import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.ServersConfig;

public class JMXConnectorThread
    implements Runnable {

    public void run() {
        String location = System.getProperty("servers.config", "servers.xml");

        try {
            File file = new File(location);
            if (!file.canRead()) {
                System.err.println("Configuration file wasn't found at:" + location);
            }
            InputStream configXML = new FileInputStream(file);
            JAXBContext context = JAXBContext.newInstance(ServersConfig.class);
            ServersConfig serversConfig = (ServersConfig) context.createUnmarshaller().unmarshal(configXML);

            System.out.println("Initilized:" + serversConfig);
            ConnectedServersState.setServersConfig(serversConfig);

            if (serversConfig.getServers().size() > 0) {
                for (ServerConfig sc : serversConfig.getServers()) {
                    if (ConnectedServersState.getMap().containsKey(sc.getServerCode())) {
                        ServerStataus serverStataus = ConnectedServersState.getMap().get(sc.getServerCode());
                        if (!serverStataus.isConnected()) {
                            // try to connect again
                            ServerStataus ss = connect(sc);
                            ConnectedServersState.getMap().put(sc.getServerCode(), ss);
                        }
                    } else {
                        ServerStataus ss = connect(sc);
                        ConnectedServersState.getMap().put(sc.getServerCode(), ss);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private ServerStataus connect(final ServerConfig sc) {

        JMXConnector c;
        ServerStataus ss = new ServerStataus();
        ss.setServerConfig(sc);
        try {
            JMXServiceURL u = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + sc.getIp() + ":" + sc.getJmxPort() + "/jmxrmi");
            c = JMXConnectorFactory.connect(u);

            System.out.println("Conneted to:" + c);

            c.addConnectionNotificationListener(new NotificationListener() {

                @Override
                public void handleNotification(Notification notification, Object key) {
                    System.out.println("Notification:" + notification);
                    if (notification.getType().contains("closed") || notification.getType().contains("failed")) {
                        ConnectedServersState.getMap().remove(((ServerConfig) key).getServerCode());
                        // TODO clear state
                    }
                }
            }, null, sc);

            ss.setConnector(c);
            ss.setConnected(true);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return ss;
    };
}
