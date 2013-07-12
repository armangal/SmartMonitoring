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
package com.smexec.monitor.shared.smartpool;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PoolsFeed
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String poolName;
    private String smartExecutorName;
    private long maxGenTime;
    private long minGenTime;
    private long executed;
    private long submitted;
    private long rejected;
    private long completed;
    private long failed;
    private long totoalGenTime;
    private long activeThreads;
    private long largestPoolSize;
    private long poolSize;
    private int hosts = 1; // monitoring X servers at the same time

    private LinkedList<TaskExecutionChunk> chunks = new LinkedList<TaskExecutionChunk>();

    private HashSet<String> taskNames = new HashSet<String>();

    PoolsFeed() {}

    public PoolsFeed(String poolName, String smartExecutorName) {
        super();
        this.poolName = poolName;
        this.smartExecutorName = smartExecutorName;
    }

    public String getSmartExecutorName() {
        return smartExecutorName;
    }

    public long getMaxGenTime() {
        return maxGenTime;
    }

    public void setMaxGenTime(long maxGenTime) {
        this.maxGenTime = maxGenTime;
    }

    public long getAvgGenTime() {
        long totTask = completed + failed;
        return totoalGenTime / (totTask > 0 ? totTask : 1);
    }

    public long getMinGenTime() {
        return minGenTime;
    }

    public void setMinGenTime(long minGenTime) {
        this.minGenTime = minGenTime;
    }

    public long getExecuted() {
        return executed;
    }

    public void setExecuted(long executed) {
        this.executed = executed;
    }

    public long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(long submitted) {
        this.submitted = submitted;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getFailed() {
        return failed;
    }

    public void setFailed(long failed) {
        this.failed = failed;
    }

    public long getTotoalGenTime() {
        return totoalGenTime;
    }

    public void setTotoalGenTime(long totoalGenTime) {
        this.totoalGenTime = totoalGenTime;
    }

    public long getActiveThreads() {
        return activeThreads;
    }

    public void setActiveThreads(long activeThreads) {
        this.activeThreads = activeThreads;
    }

    public long getLargestPoolSize() {
        return largestPoolSize;
    }

    public void setLargestPoolSize(long largestPoolSize) {
        this.largestPoolSize = largestPoolSize;
    }

    public long getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(long poolSize) {
        this.poolSize = poolSize;
    }

    public String getPoolName() {
        return poolName;
    }

    public int getHosts() {
        return hosts;
    }

    public HashSet<String> getTaskNames() {
        return taskNames;
    }

    public void addTaskNames(String[] taskNames) {
        for (String name : taskNames) {
            this.taskNames.add(name);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PoolsFeed [poolName=")
               .append(poolName)
               .append(", smartExecutorName=")
               .append(smartExecutorName)
               .append(", maxGenTime=")
               .append(maxGenTime)
               .append(", minGenTime=")
               .append(minGenTime)
               .append(", executed=")
               .append(executed)
               .append(", submitted=")
               .append(submitted)
               .append(", rejected=")
               .append(rejected)
               .append(", completed=")
               .append(completed)
               .append(", failed=")
               .append(failed)
               .append(", totoalGenTime=")
               .append(totoalGenTime)
               .append(", activeThreads=")
               .append(activeThreads)
               .append(", largestPoolSize=")
               .append(largestPoolSize)
               .append(", poolSize=")
               .append(poolSize)
               .append(", hosts=")
               .append(hosts)
               .append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((poolName == null) ? 0 : poolName.hashCode());
        result = prime * result + ((smartExecutorName == null) ? 0 : smartExecutorName.hashCode());
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
        PoolsFeed other = (PoolsFeed) obj;
        if (poolName == null) {
            if (other.poolName != null)
                return false;
        } else if (!poolName.equals(other.poolName))
            return false;
        if (smartExecutorName == null) {
            if (other.smartExecutorName != null)
                return false;
        } else if (!smartExecutorName.equals(other.smartExecutorName))
            return false;
        return true;
    }

    public void merge(PoolsFeed pf) {
        this.maxGenTime = this.maxGenTime > pf.getMaxGenTime() ? this.maxGenTime : pf.getMaxGenTime();
        this.minGenTime = this.minGenTime < pf.getMinGenTime() ? this.minGenTime : pf.getMinGenTime();
        this.totoalGenTime += pf.getTotoalGenTime();

        this.submitted += pf.getSubmitted();
        this.executed += pf.getExecuted();
        this.rejected += pf.getRejected();
        this.completed += pf.getCompleted();
        this.failed += pf.getFailed();

        this.activeThreads = this.activeThreads > pf.getActiveThreads() ? this.activeThreads : pf.getActiveThreads();
        this.largestPoolSize = this.largestPoolSize > pf.getLargestPoolSize() ? this.largestPoolSize : pf.getLargestPoolSize();
        this.poolSize = this.poolSize > pf.getPoolSize() ? this.poolSize : pf.getPoolSize();

        this.hosts++;
    }

    public void updateMe(final PoolsFeed pf) {
        this.maxGenTime = pf.maxGenTime;
        this.minGenTime = pf.minGenTime;
        this.executed = pf.executed;
        this.submitted = pf.submitted;
        this.rejected = pf.rejected;
        this.completed = pf.completed;
        this.failed = pf.failed;
        this.totoalGenTime = pf.totoalGenTime;
        this.activeThreads = pf.activeThreads;
        this.largestPoolSize = pf.largestPoolSize;
        this.poolSize = pf.poolSize;
    }

    public LinkedList<TaskExecutionChunk> getChunks() {
        return chunks;
    }

    public void addChunk(List<TaskExecutionChunk> chunks) {
        this.chunks.addAll(chunks);
        while (this.chunks.size() > 100) {
            this.chunks.remove();
        }
    }

    public long getLastChunkTime() {
        if (chunks.size() > 0) {
            return chunks.getLast().getFullEndTime();
        }
        return 0;
    }
}
