package com.smexec.monitor.client.servers;

import com.smexec.monitor.client.widgets.ILineType;

public enum ServersLineType implements ILineType {
    CPU(0, "CPU", "green"), MEMORY(0, "Memory", "blue");

    int index;
    String name;
    String lineColor;

    private ServersLineType(int index, String name, String lineColor) {
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
