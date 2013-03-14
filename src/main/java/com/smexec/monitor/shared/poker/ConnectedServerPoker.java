package com.smexec.monitor.shared.poker;

import java.io.Serializable;
import java.util.ArrayList;

import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.GCHistory;
import com.smexec.monitor.shared.MemoryUsage;

public class ConnectedServerPoker
    extends ConnectedServer
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean channelServer;
    /**
     * here we communicate none aggregated stats in order to be able to show them separatelly per server.
     */
    private ChannelSeverStats channelSeverStats;

    public ConnectedServerPoker() {}

    public ConnectedServerPoker(String name,
                                Integer serverCode,
                                String ip,
                                Integer jmxPort,
                                Boolean status,
                                MemoryUsage memoryUsage,
                                ArrayList<GCHistory> gcHistories,
                                long upTime,
                                ChannelSeverStats channelSeverStats,
                                double cpuUtilization) {
        
        super(name, serverCode, ip, jmxPort, status, memoryUsage, gcHistories, upTime, cpuUtilization);
        this.channelSeverStats = channelSeverStats;
        this.channelServer = channelSeverStats != null;
    }

    public boolean isChannelServer() {
        return channelServer;
    }

    public void setChannelServer(boolean channelServer) {
        this.channelServer = channelServer;
    }

    public ChannelSeverStats getChannelSeverStats() {
        return channelSeverStats;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectedServerPoker [channelServer=");
        builder.append(channelServer);
        builder.append(", channelSeverStats=");
        builder.append(channelSeverStats);
        builder.append("]").append(super.toString());
        return builder.toString();
    }

}
