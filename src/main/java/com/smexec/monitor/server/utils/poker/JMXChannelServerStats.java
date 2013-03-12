package com.smexec.monitor.server.utils.poker;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;

import com.smexec.monitor.server.model.poker.ServerStatausPoker;
import com.smexec.monitor.server.utils.JMXUtils;
import com.smexec.monitor.shared.poker.ChannelChunkStats;
import com.smexec.monitor.shared.poker.ChannelSeverStats;

public class JMXChannelServerStats {

    /**
     * in case the server is channel server, we will collect statistics from it
     * 
     * @param ss
     */
    public static void getChannelStatistics(ServerStatausPoker ss) {

        try {
            JMXConnector jmxConnector = ss.getConnector();
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            ObjectName sessionStats = new ObjectName("com.playtech.poker.jmx:type=Stats,name=ChannelServerStats");
            if (mbsc.isRegistered(sessionStats)) {
                // channel server
                ChannelSeverStats stats = ss.getChannelSeverStats();
                CompositeData[] data;
                if (ss.isFirstTimeAccess()) {
                    // load all stats
                    System.out.println("Requesting full stats from Channel:" + ss.getServerConfig().getServerCode());

                    data = (CompositeData[]) mbsc.getAttribute(sessionStats, "ServerStats");

                } else {
                    // load delta
                    long startTime = stats.getLastChunk().getStartTime();
                    System.out.println("Requesting delta Channel:" + ss.getServerConfig().getServerCode() + ", chunks from:" + startTime);
                    data = (CompositeData[]) mbsc.invoke(sessionStats, "getLastServerStats", new Object[] {startTime}, new String[] {"long"});
                }
                System.out.println("got :" + data.length + " chunks from channel:" + ss.getServerConfig().getServerCode());
                for (int i = data.length - 1; i >= 0; i--) {
                    CompositeData cd = data[i];
                    ChannelChunkStats cscs = new ChannelChunkStats(JMXUtils.getIntAtributeFromComposite(cd, "connectedBinarySessions"),
                                                                   JMXUtils.getIntAtributeFromComposite(cd, "connectedLegacySessions"),
                                                                   JMXUtils.getIntAtributeFromComposite(cd, "disconnectedBinarySessions"),
                                                                   JMXUtils.getIntAtributeFromComposite(cd, "disconnectedLegacySessions"),
                                                                   JMXUtils.getIntAtributeFromComposite(cd, "startTime"),
                                                                   JMXUtils.getIntAtributeFromComposite(cd, "endTime"),
                                                                   JMXUtils.getIntAtributeFromComposite(cd, "totalBinarySessions"),
                                                                   JMXUtils.getIntAtributeFromComposite(cd, "totalLegacySessions"));

                    stats.addChunk(cscs);
                    System.out.println(cscs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
