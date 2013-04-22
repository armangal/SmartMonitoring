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

    public void setName(String name) {
        this.name = name;
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

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (authenticate ? 1231 : 1237);
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((jmxPort == null) ? 0 : jmxPort.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((serverCode == null) ? 0 : serverCode.hashCode());
        result = prime * result + ((serverGroup == null) ? 0 : serverGroup.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServerConfig other = (ServerConfig) obj;
        if (authenticate != other.authenticate)
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (jmxPort == null) {
            if (other.jmxPort != null)
                return false;
        } else if (!jmxPort.equals(other.jmxPort))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (serverCode == null) {
            if (other.serverCode != null)
                return false;
        } else if (!serverCode.equals(other.serverCode))
            return false;
        if (serverGroup == null) {
            if (other.serverGroup != null)
                return false;
        } else if (!serverGroup.equals(other.serverGroup))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

}
