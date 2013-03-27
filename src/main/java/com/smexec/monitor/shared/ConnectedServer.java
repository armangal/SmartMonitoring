package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.smexec.monitor.shared.runtime.GCHistory;
import com.smexec.monitor.shared.runtime.MemoryUsage;

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

    private double cpuUtilization;

    public ConnectedServer() {}

    public ConnectedServer(String name,
                           Integer serverCode,
                           String ip,
                           Integer jmxPort,
                           Boolean status,
                           MemoryUsage memoryUsage,
                           ArrayList<GCHistory> gcHistories,
                           long upTime,
                           double cpuUtilization) {
        super();
        this.name = name;
        this.serverCode = serverCode;
        this.ip = ip;
        this.jmxPort = jmxPort;
        this.status = status;
        this.memoryUsage = memoryUsage;
        this.gcHistories = gcHistories;
        this.upTime = upTime;
        this.cpuUtilization = cpuUtilization;
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

    public double getCpuUtilization() {
        return cpuUtilization;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectedServer [n=")
               .append(name)
               .append(", sc=")
               .append(serverCode)
               .append(", ip=")
               .append(ip)
               .append(", jp=")
               .append(jmxPort)
               .append(", st=")
               .append(status)
               .append(", mu=")
               .append(memoryUsage)
               .append(", gc=")
               .append(gcHistories)
               .append(", ut=")
               .append(upTime)
               .append(", cp=")
               .append(cpuUtilization)
               .append("]");
        return builder.toString();
    }

}
