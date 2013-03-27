package com.smexec.monitor.shared;

import java.io.Serializable;

public abstract class AbstractChunkStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long startTime;
    private long endTime;

    public AbstractChunkStats() {}

    public AbstractChunkStats(long startTime, long endTime) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStartTimeForChart() {
        return startTime % 10000L;
    }

    public long getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("st=");
        builder.append(startTime);
        builder.append(", et=");
        builder.append(endTime);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (endTime ^ (endTime >>> 32));
        result = prime * result + (int) (startTime ^ (startTime >>> 32));
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
        AbstractChunkStats other = (AbstractChunkStats) obj;
        if (endTime != other.endTime)
            return false;
        if (startTime != other.startTime)
            return false;
        return true;
    }

}
