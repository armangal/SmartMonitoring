package com.smexec.monitor.shared;

import java.io.Serializable;

public class CopyOfChartFeed
    implements Serializable {

    private static final long serialVersionUID = 1L;
    long min;
    long max;
    long avg;

    public CopyOfChartFeed() {}

    public CopyOfChartFeed(long min, long max, long avg) {
        super();
        this.min = min;
        this.max = max;
        this.avg = avg;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public long getAvg() {
        return avg;
    }

}
