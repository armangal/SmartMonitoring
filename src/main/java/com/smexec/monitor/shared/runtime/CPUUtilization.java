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

    public double evolve(final long lastMeasurementAfter,
                         final int availableProcessors,
                         final long lastMeasureTimeAfter,
                         final double systemLoadAverage,
                         final int systemHistoryToKeep) {
        double percent;

        if (lastMeasureTimeAfter > lastMeasureTime) {
            percent = ((lastMeasurementAfter - lastMeasurement) * 100L) / (lastMeasureTimeAfter - lastMeasureTime) / availableProcessors;
        } else {
            percent = 0;
        }

        this.lastMeasurement = lastMeasurementAfter;
        this.lastMeasureTime = lastMeasureTimeAfter;
        list.add(new CpuUtilizationChunk(percent, systemLoadAverage, DateUtils.roundDate(new Date())));
        if (list.size() > systemHistoryToKeep) {
            list.remove();
        }

        return percent;
    }

    public CpuUtilizationChunk getLastPercent() {
        return list.isEmpty() ? new CpuUtilizationChunk() : list.getLast();
    }

    public LinkedList<CpuUtilizationChunk> getPercentList(Integer chunks) {
        LinkedList<CpuUtilizationChunk> ret = new LinkedList<CpuUtilizationChunk>();
        // get last X chunks
        for (int i = Math.max(0, list.size() - chunks); i < list.size(); i++) {
            ret.add(list.get(i));
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CPUUtilization [lastMeasurement=").append(lastMeasurement).append(", lastMeasureTime=").append(lastMeasureTime).append("]");
        return builder.toString();
    }

}
