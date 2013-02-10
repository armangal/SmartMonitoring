package com.smexec.monitor.shared;

import java.io.Serializable;

import com.google.gwt.i18n.client.NumberFormat;

public class MemoryUsage
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long committed;
    private long init;
    private long max;
    private long used;
    private String memoryState;

    public MemoryUsage() {}

    public MemoryUsage(long init, long used, long committed, long max, String memoryState) {
        super();
        this.committed = committed;
        this.init = init;
        this.max = max;
        this.used = used;
        this.memoryState = memoryState;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
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
        NumberFormat formatLong = NumberFormat.getDecimalFormat();

        StringBuilder builder = new StringBuilder();
        builder.append("MemoryUsage [committed=");
        builder.append(formatLong.format(committed));
        builder.append(", init=");
        builder.append(formatLong.format(init));
        builder.append(", max=");
        builder.append(formatLong.format(max));
        builder.append(", used=");
        builder.append(formatLong.format(used));
        builder.append(", percent=");
        builder.append(formatLong.format(getPercentage()));
        builder.append("%]");
        return builder.toString();
    }

}
