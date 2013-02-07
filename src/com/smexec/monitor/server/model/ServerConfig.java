package com.smexec.monitor.server.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "server")
public class ServerConfig {

    private String name;
    private Integer serverCode;
    private String ip;
    private Integer jmxPort;

    public ServerConfig() {}

    
    public ServerConfig(String name, Integer serverCode, String ip, Integer jmxPort) {
        super();
        this.name = name;
        this.serverCode = serverCode;
        this.ip = ip;
        this.jmxPort = jmxPort;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nServerConfig [name=");
        builder.append(name);
        builder.append(", serverCode=");
        builder.append(serverCode);
        builder.append(", ip=");
        builder.append(ip);
        builder.append(", jmxPort=");
        builder.append(jmxPort);
        builder.append("]");
        return builder.toString();
    }

}
