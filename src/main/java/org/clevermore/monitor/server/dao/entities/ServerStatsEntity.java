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
package org.clevermore.monitor.server.dao.entities;

import com.github.jmkgreen.morphia.annotations.Entity;

@Entity("serverStats")
public class ServerStatsEntity
    extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private Long upTime;
    private Double cpuUsage;
    private Double systemLoadAverage;
    private long committed;
    private long init;
    private long max;
    private long used;
    private String memoryState;
    private Boolean status;

    ServerStatsEntity() {
        super();
    }

    public ServerStatsEntity(Long time,
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
        super(time, serverCode, serverName);
        this.upTime = upTime;
        this.cpuUsage = cpuUsage;
        this.systemLoadAverage = systemLoadAverage;
        this.committed = committed;
        this.init = init;
        this.max = max;
        this.used = used;
        this.memoryState = memoryState;
        this.status = status;
    }

    public Long getUpTime() {
        return upTime;
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
        builder.append("ServerStatsEntity [")
               .append(super.toString())
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
        int result = super.hashCode();
        result = prime * result + (int) (committed ^ (committed >>> 32));
        result = prime * result + ((cpuUsage == null) ? 0 : cpuUsage.hashCode());
        result = prime * result + (int) (init ^ (init >>> 32));
        result = prime * result + (int) (max ^ (max >>> 32));
        result = prime * result + ((memoryState == null) ? 0 : memoryState.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((systemLoadAverage == null) ? 0 : systemLoadAverage.hashCode());
        result = prime * result + ((upTime == null) ? 0 : upTime.hashCode());
        result = prime * result + (int) (used ^ (used >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServerStatsEntity other = (ServerStatsEntity) obj;
        if (committed != other.committed)
            return false;
        if (cpuUsage == null) {
            if (other.cpuUsage != null)
                return false;
        } else if (!cpuUsage.equals(other.cpuUsage))
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
        if (upTime == null) {
            if (other.upTime != null)
                return false;
        } else if (!upTime.equals(other.upTime))
            return false;
        if (used != other.used)
            return false;
        return true;
    }

}
