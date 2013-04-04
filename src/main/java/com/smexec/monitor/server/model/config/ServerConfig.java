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
               .append("]\n");
        return builder.toString();
    }



}
