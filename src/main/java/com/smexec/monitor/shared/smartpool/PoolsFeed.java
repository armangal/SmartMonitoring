package com.smexec.monitor.shared.smartpool;

import java.io.Serializable;

import com.smexec.monitor.shared.ChartFeed;

public class PoolsFeed
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String poolName;
    private ChartFeed<Long, Long> timeChartFeeds;
    private ChartFeed<Long, Long> tasksChartFeeds;
    private long avgGenTime;
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

    public long getAvgGenTime() {
        return avgGenTime;
    }

    public void setAvgGenTime(long avgGenTime) {
        this.avgGenTime = avgGenTime;
    }

    public long getMaxGenTime() {
        return maxGenTime;
    }

    public void setMaxGenTime(long maxGenTime) {
        this.maxGenTime = maxGenTime;
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

    public PoolsFeed() {}

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public ChartFeed<Long, Long> getTimeChartFeeds() {
        return timeChartFeeds;
    }

    public void setTimeChartFeeds(ChartFeed<Long, Long> timeChartFeeds) {
        this.timeChartFeeds = timeChartFeeds;
    }

    public ChartFeed<Long, Long> getTasksChartFeeds() {
        return tasksChartFeeds;
    }

    public void setTasksChartFeeds(ChartFeed<Long, Long> tasksChartFeeds) {
        this.tasksChartFeeds = tasksChartFeeds;
    }

    public int getHosts() {
        return hosts;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PoolsFeed [poolName=");
        builder.append(poolName);
        builder.append(", timeChartFeeds=");
        builder.append(timeChartFeeds);
        builder.append(", tasksChartFeeds=");
        builder.append(tasksChartFeeds);
        builder.append(", avgGenTime=");
        builder.append(avgGenTime);
        builder.append(", maxGenTime=");
        builder.append(maxGenTime);
        builder.append(", minGenTime=");
        builder.append(minGenTime);
        builder.append(", executed=");
        builder.append(executed);
        builder.append(", submitted=");
        builder.append(submitted);
        builder.append(", rejected=");
        builder.append(rejected);
        builder.append(", completed=");
        builder.append(completed);
        builder.append(", failed=");
        builder.append(failed);
        builder.append(", totoalGenTime=");
        builder.append(totoalGenTime);
        builder.append(", activeThreads=");
        builder.append(activeThreads);
        builder.append(", largestPoolSize=");
        builder.append(largestPoolSize);
        builder.append(", poolSize=");
        builder.append(poolSize);
        builder.append(", hosts=");
        builder.append(hosts);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((poolName == null) ? 0 : poolName.hashCode());
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
        return true;
    }

    public void merge(PoolsFeed pf) {
        this.avgGenTime = (this.avgGenTime * this.hosts + pf.getAvgGenTime()) / (this.hosts + 1);
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
}
