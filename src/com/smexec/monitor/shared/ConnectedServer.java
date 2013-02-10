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

    public ConnectedServer() {}

    public ConnectedServer(String name,
                           Integer serverCode,
                           String ip,
                           Integer jmxPort,
                           Boolean status,
                           MemoryUsage memoryUsage,
                           ArrayList<GCHistory> gcHistories,
                           long upTime) {
        super();
        this.name = name;
        this.serverCode = serverCode;
        this.ip = ip;
        this.jmxPort = jmxPort;
        this.status = status;
        this.memoryUsage = memoryUsage;
        this.gcHistories = gcHistories;
        this.upTime = upTime;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectedServer [name=");
        builder.append(name);
        builder.append(", serverCode=");
        builder.append(serverCode);
        builder.append(", ip=");
        builder.append(ip);
        builder.append(", jmxPort=");
        builder.append(jmxPort);
        builder.append(", status=");
        builder.append(status);
        builder.append(", memoryUsage=");
        builder.append(memoryUsage);
        builder.append(", gcHistories=");
        builder.append(gcHistories);
        builder.append(", upTime=");
        builder.append(upTime);
        builder.append("]");
        return builder.toString();
    }

}
