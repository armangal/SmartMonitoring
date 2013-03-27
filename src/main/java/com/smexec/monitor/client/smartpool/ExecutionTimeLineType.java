package com.smexec.monitor.client.smartpool;

import com.googlecode.gwt.charts.client.ColumnType;
import com.smexec.monitor.client.widgets.ILineType;

public enum ExecutionTimeLineType implements ILineType {
    MIN(2, "Min", "green", ColumnType.NUMBER), MAX(0, "Max", "red", ColumnType.NUMBER), AVG(1, "Avg", "orange", ColumnType.NUMBER);

    int index;
    String name;
    String lineColor;
    ColumnType columnType;

    private ExecutionTimeLineType(int index, String name, String lineColor, ColumnType columnType) {
        this.index = index;
        this.name = name;
        this.lineColor = lineColor;
        this.columnType = columnType;
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

    @Override
    public ColumnType getColumnType() {
        return columnType;
    }
}
