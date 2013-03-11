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
    private boolean authenticate;
    private String username;
    private String password;

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

    public boolean isAuthenticate() {
        return authenticate;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerConfig [name=");
        builder.append(name);
        builder.append(", serverCode=");
        builder.append(serverCode);
        builder.append(", ip=");
        builder.append(ip);
        builder.append(", jmxPort=");
        builder.append(jmxPort);
        builder.append(", authenticate=");
        builder.append(authenticate);
        builder.append(", username=");
        builder.append(username);
        builder.append(", password=");
        builder.append(password);
        builder.append("]\n");
        return builder.toString();
    }

}
