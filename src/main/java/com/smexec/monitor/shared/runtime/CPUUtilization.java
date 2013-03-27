package com.smexec.monitor.shared.runtime;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import com.smexec.monitor.server.utils.DateUtils;

public class CPUUtilization
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long lastMeasurement = 0L;
    private long lastMeasureTime = 0L;

    private LinkedList<CpuUtilizationChunk> list = new LinkedList<CpuUtilizationChunk>();

    public CPUUtilization() {}

    public double evolve(final long lastMeasurementAfter, final int availableProcessors, final long lastMeasureTimeAfter) {
        double percent;

        if (lastMeasureTimeAfter > lastMeasureTime) {
            percent = ((lastMeasurementAfter - lastMeasurement) * 100L) / (lastMeasureTimeAfter - lastMeasureTime) / availableProcessors;
        } else {
            percent = 0;
        }

        this.lastMeasurement = lastMeasurementAfter;
        this.lastMeasureTime = lastMeasureTimeAfter;
        list.add(new CpuUtilizationChunk(percent, DateUtils.roundDate(new Date())));
        if (list.size() > 100) {
            list.remove();
        }

        return percent;
    }

    public long getLastMeasurement() {
        return lastMeasurement;
    }

    public long getLastMeasureTime() {
        return lastMeasureTime;
    }

    public CpuUtilizationChunk getLastPercent() {
        return list.isEmpty() ? new CpuUtilizationChunk() : list.getLast();
    }

    public LinkedList<CpuUtilizationChunk> getPercentList() {
        return list;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CPUUtilization [lastMeasurement=").append(lastMeasurement).append(", lastMeasureTime=").append(lastMeasureTime).append("]");
        return builder.toString();
    }

}
