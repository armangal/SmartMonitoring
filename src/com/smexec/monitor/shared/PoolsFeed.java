package com.smexec.monitor.shared;

import java.io.Serializable;

public class PoolsFeed
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String poolName;
    private ChartFeed timeChartFeeds;
    private ChartFeed tasksChartFeeds;
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

    public ChartFeed getTimeChartFeeds() {
        return timeChartFeeds;
    }

    public void setTimeChartFeeds(ChartFeed timeChartFeeds) {
        this.timeChartFeeds = timeChartFeeds;
    }
    
    
    public ChartFeed getTasksChartFeeds() {
        return tasksChartFeeds;
    }
    
    
    public void setTasksChartFeeds(ChartFeed tasksChartFeeds) {
        this.tasksChartFeeds = tasksChartFeeds;
    }
    

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PoolsFeed [poolName=");
        builder.append(poolName);
        builder.append(", chartFeeds=");
        builder.append(timeChartFeeds);
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
        builder.append("]");
        return builder.toString();
    }

}
