package com.smexec.monitor.server.model.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "mongoConfig")
public class MongoConfig {

    private Boolean enabled = false;
    @XmlElement(name = "host")
    @XmlElementWrapper
    private List<String> hosts;
    private String username;
    private String password;

    public MongoConfig() {}

    public Boolean getEnabled() {
        return enabled;
    }

    public List<String> getHosts() {
        return hosts;
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
        builder.append("MongoConfig [enabled=");
        builder.append(enabled);
        builder.append(", hosts=");
        builder.append(hosts);
        builder.append(", username=");
        builder.append(username);
        builder.append(", password=");
        builder.append(password);
        builder.append("]");
        return builder.toString();
    }

}
