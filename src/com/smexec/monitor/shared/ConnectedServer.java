package com.smexec.monitor.shared;

import java.io.Serializable;

public class ConnectedServer
    implements Serializable {

    private String name;
    private Integer serverCode;
    private String ip;
    private Integer jmxPort;
    private Boolean status;
    private MemoryUsage memoryUsage;

    public ConnectedServer() {}

    public ConnectedServer(String name, Integer serverCode, String ip, Integer jmxPort, Boolean status, MemoryUsage memoryUsage) {
        super();
        this.name = name;
        this.serverCode = serverCode;
        this.ip = ip;
        this.jmxPort = jmxPort;
        this.status = status;
        this.memoryUsage = memoryUsage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getServerCode() {
        return serverCode;
    }

    public void setServerCode(Integer serverCode) {
        this.serverCode = serverCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public MemoryUsage getMemoryUsage() {
        return memoryUsage;
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
        builder.append("]");
        return builder.toString();
    }

}
