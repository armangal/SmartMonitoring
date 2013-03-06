package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.LinkedList;

public class CPUUtilization
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long lastMeasurement = 0L;
    private long lastMeasureTime = 0L;
    private LinkedList<Double> percentList = new LinkedList<Double>();

    public CPUUtilization() {}

    public void evolve(final long lastMeasurementAfter, final long lastMeasureTimeAfter) {
        double percent;
        if (lastMeasureTimeAfter > lastMeasureTime) {
            percent = ((lastMeasurementAfter - lastMeasurement) * 100L) / (lastMeasureTimeAfter - lastMeasureTime);
        } else {
            percent = 0;
        }

        this.lastMeasurement = lastMeasurementAfter;
        this.lastMeasureTime = lastMeasureTimeAfter;
        percentList.add(percent);
        if (percentList.size() > 100) {
            percentList.remove();
        }
    }

    public long getLastMeasurement() {
        return lastMeasurement;
    }

    public long getLastMeasureTime() {
        return lastMeasureTime;
    }

    public double getLastPercent() {
        return percentList.getLast();
    }
    
    
    public LinkedList<Double> getPercentList() {
        return percentList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CPUUtilization [lastMeasurement=")
               .append(lastMeasurement)
               .append(", lastMeasureTime=")
               .append(lastMeasureTime)
               .append("]");
        return builder.toString();
    }

}
