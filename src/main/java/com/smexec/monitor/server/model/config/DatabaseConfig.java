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
@XmlRootElement(name = "database")
public class DatabaseConfig {

    private String name;
    private DatabaseType type;
    private String ip;
    private Integer port;
    private String service;
    private String user;
    private String password;
    private String pingStatement = "select sysdate from dual";

    public DatabaseConfig() {}

    public DatabaseConfig(String name, DatabaseType type, String ip, Integer port, String service, String user, String password) {
        super();
        this.name = name;
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.service = service;
        this.user = user;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DatabaseType getType() {
        return type;
    }

    public void setType(DatabaseType type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPingStatement() {
        return pingStatement;
    }

    public void setPingStatement(String pingStatement) {
        this.pingStatement = pingStatement;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DatabaseConfig [name=")
               .append(name)
               .append(", type=")
               .append(type)
               .append(", ip=")
               .append(ip)
               .append(", port=")
               .append(port)
               .append(", service=")
               .append(service)
               .append(", user=")
               .append(user)
               .append(", password=")
               .append(password)
               .append(", pingStatement=")
               .append(pingStatement)
               .append("]");
        return builder.toString();
    }

}
