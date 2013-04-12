/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import javax.xml.bind.annotation.XmlTransient;

import com.smexec.monitor.server.model.config.MongoConfig.HostAddress;

@XmlRootElement(name = "ServersConfig", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServersConfig {

    @XmlElement(name = "server")
    @XmlElementWrapper
    private List<ServerConfig> servers;

    @XmlElement(name = "group")
    @XmlElementWrapper
    private List<ServerGroup> serverGroups;

    @XmlTransient
    private Map<String, ServerGroup> groupsMap = null;

    private String name;

    private String username;
    private String password;

    private AlertsConfig alertsConfig = new AlertsConfig();

    private MongoConfig mongoConfig = new MongoConfig();

    private MailUpdaterConfig mailUpdaterConfig = new MailUpdaterConfig();

    public ServersConfig() {
        this.serverGroups = new ArrayList<ServerGroup>(0);
        this.serverGroups.add(ServerGroup.DEFAULT_GROUP);

    }

    public List<ServerConfig> getServers() {
        return servers;
    }

    private void setServers(List<ServerConfig> servers) {
        this.servers = servers;
    }

    public List<ServerGroup> getServerGroups() {
        return serverGroups;
    }

    private void setServerGroups(List<ServerGroup> serverGroups) {
        this.serverGroups = serverGroups;
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

    public AlertsConfig getAlertsConfig() {
        return alertsConfig;
    }

    public void setAlertsConfig(AlertsConfig alertsConfig) {
        this.alertsConfig = alertsConfig;
    }

    public MongoConfig getMongoConfig() {
        return mongoConfig;
    }

    public void setMongoConfig(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }

    public MailUpdaterConfig getMailUpdaterConfig() {
        return mailUpdaterConfig;
    }

    public void setMailUpdaterConfig(MailUpdaterConfig mailUpdaterConfig) {
        this.mailUpdaterConfig = mailUpdaterConfig;
    }

    /**
     * returns the server group by name
     * 
     * @param groupName
     * @return
     */
    public ServerGroup getServerGroup(String groupName) {
        if (groupsMap == null) {
            synchronized (this.getClass()) {
                if (groupsMap == null) {
                    groupsMap = new HashMap<String, ServerGroup>(1);
                    for (ServerGroup sg : serverGroups) {
                        ServerGroup put = groupsMap.put(sg.getName(), sg);
                        if (put != null) {
                            throw new RuntimeException("Server group names should ne unique:" + sg.getName());
                        }
                    }
                    groupsMap.put(ServerGroup.DEFAULT_GROUP_NAME, new ServerGroup());
                }
            }
        }
        if (groupsMap.containsKey(groupName)) {
            return groupsMap.get(groupName);
        }

        return new ServerGroup();// default in case something goes wrong
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServersConfig [servers=");
        builder.append(servers);
        builder.append(",\nGroups=");
        builder.append(serverGroups);
        builder.append(",\n name=");
        builder.append(name);
        builder.append(", username=");
        builder.append(username);
        builder.append(", password=");
        builder.append(password);
        builder.append(",\nalertsConfig=");
        builder.append(alertsConfig);
        builder.append(",\nmongoConfig=");
        builder.append(mongoConfig);
        builder.append(",\nMailUpdaterConfig=");
        builder.append(mailUpdaterConfig);
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

            ArrayList<String> toAddressList = new ArrayList<String>();
            toAddressList.add("t1");
            toAddressList.add("t2");
            AlertsConfig ac = new AlertsConfig(true, "fromAd", "FromN", "sub", "mailServerAddress", "mailServerport", 10000, toAddressList);
            sc.setAlertsConfig(ac);

            ServerGroup sg = new ServerGroup("test", 55d, 66d);
            List<ServerGroup> sgList = new ArrayList<ServerGroup>();
            sgList.add(sg);
            sc.setServerGroups(sgList);

            MongoConfig mc = new MongoConfig();
            sc.setMongoConfig(mc);
            mc.setEnabled(false);
            mc.setDatabaseName("smartDB");
            mc.setPassword("pass");
            mc.setUsername("user");
            List<HostAddress> hosts = new ArrayList<MongoConfig.HostAddress>(0);
            hosts.add(new HostAddress("localhost", 27017));
            mc.setHosts(hosts);

            MailUpdaterConfig muc = new MailUpdaterConfig();
            muc.setEnabled(true);
            muc.setPeriod(20);
            List<String> to = new ArrayList<String>();
            to.add("arman.gal@playtech.com");
            muc.setTo(to);

            sc.setMailUpdaterConfig(muc);

            context.createMarshaller().marshal(sc, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void validate() {
        // validating unique server code.
        Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        for (ServerConfig sc : servers) {
            Boolean put = map.put(sc.getServerCode(), Boolean.TRUE);
            if (put != null) {
                throw new RuntimeException("Duplicate server code:" + sc.getServerCode());
            }
        }
    }
}
