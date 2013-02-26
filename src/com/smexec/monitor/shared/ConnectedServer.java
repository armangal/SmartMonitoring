package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class represents one connected server that hold all the relevant information for client representation.
 * 
 * @author armang
 */
public class ConnectedServer
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Integer serverCode;
    private String ip;
    private Integer jmxPort;
    private Boolean status;
    private MemoryUsage memoryUsage;
    private ArrayList<GCHistory> gcHistories;
    private long upTime;
    private boolean channelServer;

    /**
     * here we communicate none aggregated stats in order to be able to show them separatelly per server.
     */
    private ChannelSeverStats channelSeverStats;

    private CPUUtilization cpuUtilization;

    public ConnectedServer() {}

    public ConnectedServer(String name,
                           Integer serverCode,
                           String ip,
                           Integer jmxPort,
                           Boolean status,
                           MemoryUsage memoryUsage,
                           ArrayList<GCHistory> gcHistories,
                           long upTime,
                           ChannelSeverStats channelSeverStats,
                           CPUUtilization cpuUtilization) {
        super();
        this.name = name;
        this.serverCode = serverCode;
        this.ip = ip;
        this.jmxPort = jmxPort;
        this.status = status;
        this.memoryUsage = memoryUsage;
        this.gcHistories = gcHistories;
        this.upTime = upTime;
        this.channelSeverStats = channelSeverStats;
        this.cpuUtilization = cpuUtilization;
        this.channelServer = channelSeverStats != null;
    }

    public String getName() {
        return name;
    }

    public Integer getServerCode() {
        return serverCode;
    }

    public String getIp() {
        return ip;
    }

    public Integer getJmxPort() {
        return jmxPort;
    }

    public Boolean getStatus() {
        return status;
    }

    public MemoryUsage getMemoryUsage() {
        return memoryUsage;
    }

    public ArrayList<GCHistory> getGcHistories() {
        return gcHistories;
    }

    public long getUpTime() {
        return upTime;
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

    public CPUUtilization getCpuUtilization() {
        return cpuUtilization;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectedServer [name=")
               .append(name)
               .append(", serverCode=")
               .append(serverCode)
               .append(", ip=")
               .append(ip)
               .append(", jmxPort=")
               .append(jmxPort)
               .append(", status=")
               .append(status)
               .append(", memoryUsage=")
               .append(memoryUsage)
               .append(", gcHistories=")
               .append(gcHistories)
               .append(", upTime=")
               .append(upTime)
               .append(", channelServer=")
               .append(channelServer)
               .append(", channelSeverStats=")
               .append(channelSeverStats)
               .append(", cpuUtilization=")
               .append(cpuUtilization)
               .append("]");
        return builder.toString();
    }

}
