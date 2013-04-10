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
package com.smexec.monitor.shared;

import java.io.Serializable;

import com.smexec.monitor.shared.runtime.MemoryUsage;

/**
 * Class represents one connected server that hold all the relevant information for client representation.
 * 
 * @author armang
 */
public class ConnectedServer
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Integer serverCode;
    private String ip;
    private Integer jmxPort;
    private Boolean status;
    private MemoryUsage memoryUsage;
    private Double[] gcHistories;
    private long upTime;

    private double cpuUsage;
    private double systemLoadAverage;

    public ConnectedServer() {}

    public ConnectedServer(String name,
                           Integer serverCode,
                           String ip,
                           Integer jmxPort,
                           Boolean status,
                           MemoryUsage memoryUsage,
                           Double[] gcHistories,
                           long upTime,
                           double cpuUsage,
                           double systemLoadAverage) {
        super();
        this.name = name;
        this.serverCode = serverCode;
        this.ip = ip;
        this.jmxPort = jmxPort;
        this.status = status;
        this.memoryUsage = memoryUsage;
        this.gcHistories = gcHistories;
        this.upTime = upTime;
        this.cpuUsage = cpuUsage;
        this.systemLoadAverage = systemLoadAverage;
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

    public Boolean getStatus() {
        return status;
    }

    public MemoryUsage getMemoryUsage() {
        return memoryUsage;
    }

    public Double[] getGcHistories() {
        return gcHistories;
    }

    public long getUpTime() {
        return upTime;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectedServer [n=")
               .append(name)
               .append(", sc=")
               .append(serverCode)
               .append(", ip=")
               .append(ip)
               .append(", jp=")
               .append(jmxPort)
               .append(", st=")
               .append(status)
               .append(", mu=")
               .append(memoryUsage)
               .append(", gc=")
               .append(gcHistories)
               .append(", ut=")
               .append(upTime)
               .append(", cp=")
               .append(cpuUsage)
               .append(", sla=")
               .append(systemLoadAverage)
               .append("]");
        return builder.toString();
    }

}
