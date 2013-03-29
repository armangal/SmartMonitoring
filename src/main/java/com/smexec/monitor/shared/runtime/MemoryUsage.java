package com.smexec.monitor.shared.runtime;

import java.io.Serializable;

import com.smexec.monitor.shared.AbstractChunkStats;

/**
 * represents one memory measurement
 * 
 * @author armang
 */
public class MemoryUsage
    extends AbstractChunkStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long committed;
    private long init;
    private long max;
    private long used;
    private String memoryState;

    public MemoryUsage() {}

    public MemoryUsage(long init, long used, long committed, long max, String memoryState, long time) {
        super(time, time);
        this.committed = committed;
        this.init = init;
        this.max = max;
        this.used = used;
        this.memoryState = memoryState;
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

    public double getPercentage() {
        return used * 100d / max;
    }

    public String getMemoryState() {
        return memoryState;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MemoryUsage [cm=");
        builder.append((committed));
        builder.append(", in=");
        builder.append((init));
        builder.append(", max=");
        builder.append((max));
        builder.append(", us=");
        builder.append((used));
        builder.append(", pr=");
        builder.append((getPercentage()));
        builder.append("%, ").append(super.toString()).append("]");
        return builder.toString();
    }
    

}
