package com.smexec.monitor.server.model.config;

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
    private boolean authenticate = false;
    private String username;
    private String password;
    private String serverGroup = "DEFAULT";

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

    public String getServerGroup() {
        return serverGroup;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerConfig [name=")
               .append(name)
               .append(", serverCode=")
               .append(serverCode)
               .append(", ip=")
               .append(ip)
               .append(", jmxPort=")
               .append(jmxPort)
               .append(", authenticate=")
               .append(authenticate)
               .append(", username=")
               .append(username)
               .append(", password=")
               .append(password)
               .append(", serverGroup=")
               .append(serverGroup)
               .append("]");
        return builder.toString();
    }



}
