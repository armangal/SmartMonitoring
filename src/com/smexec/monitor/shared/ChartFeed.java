package com.smexec.monitor.shared;

import java.io.Serializable;

public class ChartFeed
    implements Serializable {

    private static final long serialVersionUID = 1L;
    long min;
    long max;
    long avg;

    public ChartFeed() {}

    public ChartFeed(long min, long max, long avg) {
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
