package com.smexec.monitor.client.smartpool;

import com.smexec.monitor.client.widgets.ILineType;

public enum TasksLineType implements ILineType {
    SUBMITED(0, "Submited", "grey"), EXECUTED(1, "Executed", "CC9900"), COMPLETED(2, "Completed", "green"), FAILED(3, "Failed", "FF0000"), REJECTED(4,
                                                                                                                                                    "Rejected",
                                                                                                                                                    "FF00CC");

    int index;
    String name;
    String lineColor;

    private TasksLineType(int index, String name, String lineColor) {
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
