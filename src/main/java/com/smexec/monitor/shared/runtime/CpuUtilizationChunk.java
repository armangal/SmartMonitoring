package com.smexec.monitor.shared.runtime;

import java.io.Serializable;

import com.smexec.monitor.shared.AbstractChunkStats;

/**
 * data holder for CPU usage stats
 * @author armang
 *
 */
public class CpuUtilizationChunk
    extends AbstractChunkStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private double usage;

    public CpuUtilizationChunk() {}

    public CpuUtilizationChunk(double usage, long time) {
        super(time, time);
        this.usage = usage;
    }

    public double getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CUC [u=");
        builder.append(usage);
        builder.append(", ");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }

}
