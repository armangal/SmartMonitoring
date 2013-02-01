package com.smexec.monitor.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.smexec.monitor.client.MonitoringService;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.RefreshResult;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MonitoringServiceImpl
    extends RemoteServiceServlet
    implements MonitoringService {

    private static Map<JMXServiceURL, JMXConnector> connectors = new HashMap<JMXServiceURL, JMXConnector>();

    public List<String> connect(String address) {
        List<String> list = new ArrayList<String>();
        try {
            JMXServiceURL u = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + address + "/jmxrmi");
            JMXConnector c = JMXConnectorFactory.connect(u);

            System.out.println("Conneted to:" + c);

            c.addConnectionNotificationListener(new NotificationListener() {

                @Override
                public void handleNotification(Notification notification, Object key) {
                    System.out.println("Notification:" + notification);
                    if (notification.getType().contains("closed") || notification.getType().contains("failed")) {
                        connectors.remove(key);
                    }
                }
            }, null, u);

            connectors.put(u, c);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (JMXConnector c : connectors.values()) {
            list.add(c.toString());
        }

        return list;
    }

    @Override
    public RefreshResult refresh() {
        List<PoolsFeed> poolFeeds = new ArrayList<PoolsFeed>();
        List<String> servers = new ArrayList<String>();
        RefreshResult result = new RefreshResult(poolFeeds, servers);

        for (JMXConnector c : connectors.values()) {
            servers.add(c.toString());
        }

        try {

            if (connectors.size() <= 0) {
                System.out.println("Returning empty result");
                return result;
            }
            JMXConnector jmxConnector = connectors.values().iterator().next();
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            System.out.println(mbsc.getMBeanCount());

            System.out.println(Arrays.toString(mbsc.getDomains()));

            Set<ObjectInstance> names = new HashSet<ObjectInstance>(mbsc.queryMBeans(null, null));

            for (ObjectInstance on : names) {
                if ("SmartExecutor.Pools".equals(on.getObjectName().getKeyProperty("type"))) {
                    System.out.println(on.getObjectName().getKeyProperty("name"));

                    String chunks = (String) mbsc.getAttribute(on.getObjectName(), "Chunks");
                    System.out.println(chunks);
                    chunks = chunks.replace("[", "");
                    String[] split = chunks.split("]");
                    PoolsFeed pf = new PoolsFeed();
                    pf.setPoolName(on.getObjectName().getKeyProperty("name"));

                    List<ChartFeed> l = new ArrayList<ChartFeed>();
                    for (int i = 0; i < split.length; i++) {
                        String[] values = split[i].split(",");
                        l.add(new ChartFeed(Long.valueOf(values[2]), Long.valueOf(values[0]), Long.valueOf(values[1])));
                    }
                    pf.setChartFeeds(l);

                    poolFeeds.add(pf);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
