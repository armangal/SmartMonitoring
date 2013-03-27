package com.smexec.monitor.client.widgets;

import com.googlecode.gwt.charts.client.ColumnType;

public interface ILineType {

    public abstract int getIndex();

    public abstract String getName();

    public abstract String getLineColor();
    
    public abstract ColumnType getColumnType();

}
