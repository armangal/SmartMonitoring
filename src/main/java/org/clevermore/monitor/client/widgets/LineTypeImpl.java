package org.clevermore.monitor.client.widgets;

import com.googlecode.gwt.charts.client.ColumnType;

public class LineTypeImpl
    implements ILineType {

    private int index;
    private String name;
    private String color;
    private ColumnType columnType;

    public LineTypeImpl() {}

    public LineTypeImpl(int index, String name, String color, ColumnType columnType) {
        super();
        this.index = index;
        this.name = name;
        this.color = color;
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
        return color;
    }

    @Override
    public ColumnType getColumnType() {
        return columnType;
    }

}
