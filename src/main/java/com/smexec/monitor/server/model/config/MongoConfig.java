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
    @XmlElement(name = "hostAddress")
    @XmlElementWrapper
    private List<HostAddress> hosts = new ArrayList<MongoConfig.HostAddress>(0);
    private String username = "";
    private String password = "";
    private String databaseName = "test";

    public MongoConfig() {}

    public Boolean getEnabled() {
        return enabled;
    }

    public List<HostAddress> getHosts() {
        return hosts;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setHosts(List<HostAddress> hosts) {
        this.hosts = hosts;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MongoConfig [enabled=")
               .append(enabled)
               .append(", hosts=")
               .append(hosts)
               .append(", username=")
               .append(username)
               .append(", password=")
               .append(password)
               .append(", databaseName=")
               .append(databaseName)
               .append("]");
        return builder.toString();
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "hostAddress")
    public static class HostAddress {

        private String hostName = "";
        private Integer port = -1;

        public HostAddress() {}

        public HostAddress(String hostName, Integer port) {
            super();
            this.hostName = hostName;
            this.port = port;
        }

        public String getHostName() {
            return hostName;
        }

        public Integer getPort() {
            return port;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("HostAddress [hostName=").append(hostName).append(", port=").append(port).append("]");
            return builder.toString();
        }

    }

}
