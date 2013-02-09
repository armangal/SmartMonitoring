package com.smexec.monitor.client.widgets;


public enum LineType {
    MIN(2), MAX(0), AVG(1), SUBMITED(0), EXECUTED(1), FAILED(2), REJECTED(3), COMPLETED(4);

    int index;

    private LineType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
