package com.smexec.monitor.client.smartpool;

import com.smexec.monitor.client.widgets.ILineType;

public enum ExecutionTimeLineType implements ILineType {
    MIN(2, "Min", "green"), MAX(0, "Max", "red"), AVG(1, "Avg", "orange");

    int index;
    String name;
    String lineColor;

    private ExecutionTimeLineType(int index, String name, String lineColor) {
        this.index = index;
        this.name = name;
        this.lineColor = lineColor;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLineColor() {
        return lineColor;
    }
}
