package com.smexec.monitor.client.servers;

import com.googlecode.gwt.charts.client.ColumnType;
import com.smexec.monitor.client.widgets.ILineType;

public enum ServersLineType implements ILineType {
    CPU(0, "CPU", "green", ColumnType.NUMBER), MEMORY(0, "Memory", "blue", ColumnType.NUMBER);

    int index;
    String name;
    String lineColor;
    ColumnType columnType;

    private ServersLineType(int index, String name, String lineColor, ColumnType columnType) {
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
