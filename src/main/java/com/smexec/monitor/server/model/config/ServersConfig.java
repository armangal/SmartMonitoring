package com.smexec.monitor.server.model.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String username;
    private String password;

    private Integer inMemoryAlerts = 10000;

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

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Integer getInMemoryAlerts() {
        return inMemoryAlerts;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServersConfig [servers=");
        builder.append(servers);
        builder.append(", name=");
        builder.append(name);
        builder.append(", username=");
        builder.append(username);
        builder.append(", password=");
        builder.append(password);
        builder.append(", inMemoryAlerts=");
        builder.append(inMemoryAlerts);
        builder.append("]");
        return builder.toString();
    }

    /**
     * test
     * 
     * @param args
     */
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

    public void validate() {
        Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        for (ServerConfig sc : servers) {
            Boolean put = map.put(sc.getServerCode(), Boolean.TRUE);
            if (put != null) {
                throw new RuntimeException("Duplicate server code:" + sc.getServerCode());
            }
        }
    }
}
