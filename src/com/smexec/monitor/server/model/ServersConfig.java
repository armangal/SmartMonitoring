package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ServersConfig", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServersConfig {

    @XmlElement(name = "server")
    @XmlElementWrapper
    private List<ServerConfig> servers;

    private String name;

    public ServersConfig() {}

    public List<ServerConfig> getServers() {
        return servers;
    }

    public void setServers(List<ServerConfig> servers) {
        this.servers = servers;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServersConfig [servers=");
        builder.append(servers);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

    public static void main(String[] args) {
        try {
            JAXBContext context = JAXBContext.newInstance(ServersConfig.class);
            ServerConfig s = new ServerConfig("a", 11, "ip", 1111);
            List<ServerConfig> l = new ArrayList<ServerConfig>();
            l.add(s);
            ServersConfig sc = new ServersConfig();
            sc.setServers(l);

            context.createMarshaller().marshal(sc, System.out);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
