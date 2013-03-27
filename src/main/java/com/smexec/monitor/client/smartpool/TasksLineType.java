package com.smexec.monitor.client.smartpool;

import com.googlecode.gwt.charts.client.ColumnType;
import com.smexec.monitor.client.widgets.ILineType;

public enum TasksLineType implements ILineType {
    SUBMITED(0, "Submited", "grey", ColumnType.NUMBER), EXECUTED(1, "Executed", "CC9900", ColumnType.NUMBER), //
    COMPLETED(2, "Completed", "green", ColumnType.NUMBER), FAILED(3, "Failed", "FF0000", ColumnType.NUMBER), //
    REJECTED(4, "Rejected", "FF00CC", ColumnType.NUMBER);

    int index;
    String name;
    String lineColor;
    ColumnType columnType;

    private TasksLineType(int index, String name, String lineColor, ColumnType columnType) {
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
