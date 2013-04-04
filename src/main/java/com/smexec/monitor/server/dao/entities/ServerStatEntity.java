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
package com.smexec.monitor.server.dao.entities;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity("serverstat")
public class ServerStatEntity {

    @Id
    private ObjectId id;
    private Long time;
    private Long upTime;
    private Integer serverCode;
    private String serverName;
    private Double cpuUsage;
    private Double systemLoadAverage;
    private long committed;
    private long init;
    private long max;
    private long used;
    private String memoryState;
    private Boolean status;

    public ServerStatEntity() {}

    public ServerStatEntity(Long time,
                            Long upTime,
                            Integer serverCode,
                            String serverName,
                            Double cpuUsage,
                            Double systemLoadAverage,
                            long committed,
                            long init,
                            long max,
                            long used,
                            String memoryState,
                            Boolean status) {
        super();
        this.time = time;
        this.upTime = upTime;
        this.serverCode = serverCode;
        this.serverName = serverName;
        this.cpuUsage = cpuUsage;
        this.systemLoadAverage = systemLoadAverage;
        this.committed = committed;
        this.init = init;
        this.max = max;
        this.used = used;
        this.memoryState = memoryState;
        this.status = status;
    }

    public ObjectId getId() {
        return id;
    }

    public Long getUpTime() {
        return upTime;
    }

    public Integer getServerCode() {
        return serverCode;
    }

    public String getServerName() {
        return serverName;
    }

    public Long getTime() {
        return time;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public Double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    public long getCommitted() {
        return committed;
    }

    public long getInit() {
        return init;
    }

    public long getMax() {
        return max;
    }

    public long getUsed() {
        return used;
    }

    public String getMemoryState() {
        return memoryState;
    }

    public Boolean getStatus() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerStatEntity [id=")
               .append(id)
               .append(", serverCode=")
               .append(serverCode)
               .append(", serverName=")
               .append(serverName)
               .append(", time=")
               .append(time)
               .append(", cpuUsage=")
               .append(cpuUsage)
               .append(", systemLoadAverage=")
               .append(systemLoadAverage)
               .append(", committed=")
               .append(committed)
               .append(", init=")
               .append(init)
               .append(", max=")
               .append(max)
               .append(", used=")
               .append(used)
               .append(", memoryState=")
               .append(memoryState)
               .append(", status=")
               .append(status)
               .append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (committed ^ (committed >>> 32));
        result = prime * result + ((cpuUsage == null) ? 0 : cpuUsage.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (init ^ (init >>> 32));
        result = prime * result + (int) (max ^ (max >>> 32));
        result = prime * result + ((memoryState == null) ? 0 : memoryState.hashCode());
        result = prime * result + ((serverCode == null) ? 0 : serverCode.hashCode());
        result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((systemLoadAverage == null) ? 0 : systemLoadAverage.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + (int) (used ^ (used >>> 32));
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
        ServerStatEntity other = (ServerStatEntity) obj;
        if (committed != other.committed)
            return false;
        if (cpuUsage == null) {
            if (other.cpuUsage != null)
                return false;
        } else if (!cpuUsage.equals(other.cpuUsage))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (init != other.init)
            return false;
        if (max != other.max)
            return false;
        if (memoryState == null) {
            if (other.memoryState != null)
                return false;
        } else if (!memoryState.equals(other.memoryState))
            return false;
        if (serverCode == null) {
            if (other.serverCode != null)
                return false;
        } else if (!serverCode.equals(other.serverCode))
            return false;
        if (serverName == null) {
            if (other.serverName != null)
                return false;
        } else if (!serverName.equals(other.serverName))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (systemLoadAverage == null) {
            if (other.systemLoadAverage != null)
                return false;
        } else if (!systemLoadAverage.equals(other.systemLoadAverage))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        if (used != other.used)
            return false;
        return true;
    }

}
