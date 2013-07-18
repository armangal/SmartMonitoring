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
package org.clevermore.monitor.shared.servers;

import java.io.Serializable;



/**
 * Class represents one connected database server that hold all the relevant information for client
 * representation.
 * 
 * @author armang
 */
public class ConnectedDB
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String ip;
    private Integer port;
    private DatabaseType type;
    private String service;
    private Boolean status;
    private long lastPingTime;

    public ConnectedDB() {}

    public ConnectedDB(String name, String ip, Integer port, DatabaseType type, String service, Boolean status, long lastPingTime) {
        super();
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.status = status;
        this.type = type;
        this.service = service;
        this.lastPingTime = lastPingTime;
    }

    public long getLastPingTime() {
        return lastPingTime;
    }

    public void setLastPingTime(long lastPingTime) {
        this.lastPingTime = lastPingTime;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public Boolean getStatus() {
        return status;
    }

    public DatabaseType getType() {
        return type;
    }

    public String getService() {
        return service;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectedDB [name=")
               .append(name)
               .append(", ip=")
               .append(ip)
               .append(", port=")
               .append(port)
               .append(", type=")
               .append(type)
               .append(", service=")
               .append(service)
               .append(", status=")
               .append(status)
               .append(", lastPingTime=")
               .append(lastPingTime)
               .append("]");
        return builder.toString();
    }

}
