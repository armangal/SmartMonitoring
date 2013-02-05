package com.smexec.monitor.server;

import java.text.NumberFormat;
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
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.google.gwt.core.client.GWT;
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
                ObjectName objectName = on.getObjectName();
                if ("SmartExecutor.Pools".equals(objectName.getKeyProperty("type"))) {
                    String chunks = (String) mbsc.getAttribute(objectName, "Chunks");
                    chunks = chunks.replace("[", "");
                    String[] split = chunks.split("]");
                    PoolsFeed pf = new PoolsFeed();
                    pf.setPoolName(objectName.getKeyProperty("name"));

                    ArrayList<ChartFeed> l = new ArrayList<ChartFeed>();
                    for (int i = 0; i < split.length; i++) {
                        String[] values = split[i].split(",");
                        l.add(new ChartFeed(Long.valueOf(values[2]), Long.valueOf(values[0]), Long.valueOf(values[1])));
                    }
                    pf.setChartFeeds(l);
                    pf.setActiveThreads(getLong(mbsc, objectName, "ActiveCount"));
                    pf.setAvgGenTime(getLong(mbsc, objectName, "AvgTime"));
                    pf.setCompleted(getLong(mbsc, objectName, "Completed"));
                    pf.setExecuted(getLong(mbsc, objectName, "Executed"));
                    pf.setFailed(getLong(mbsc, objectName, "Failed"));
                    pf.setLargestPoolSize(getLong(mbsc, objectName, "LargestPoolSize"));
                    pf.setMaxGenTime(getLong(mbsc, objectName, "MaxTime"));
                    pf.setMinGenTime(getLong(mbsc, objectName, "MinTime"));
                    pf.setPoolSize(getLong(mbsc, objectName, "PoolSize"));
                    pf.setRejected(getLong(mbsc, objectName, "Rejected"));
                    pf.setSubmitted(getLong(mbsc, objectName, "Submitted"));
                    pf.setTotoalGenTime(getLong(mbsc, objectName, "TotalTime"));

                    poolFeeds.add(pf);
                    System.out.println(pf);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private long getLong(MBeanServerConnection mbsc, ObjectName on, String name) {
        try {
            Object property = mbsc.getAttribute(on, name);
            return Long.valueOf(property.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;

        }
    }

}
