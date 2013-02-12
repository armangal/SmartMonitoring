package com.smexec.monitor.client.widgets;

public enum LineType {
    MIN(2, "Min"), MAX(0, "Max"), AVG(1, "Avg"), SUBMITED(0, "Submited"), EXECUTED(1, "Executed"), FAILED(2, "Failed"), REJECTED(3, "Rejected"), COMPLETED(4,
                                                                                                                                                           "Completed"), CONNECTED(
                                                                                                                                                                                   0,
                                                                                                                                                                                   "Connected"), PLAYING(
                                                                                                                                                                                                         1,
                                                                                                                                                                                                         "Playing"), DROPPED(
                                                                                                                                                                                                                             0,
                                                                                                                                                                                                                             "Dropped"), OPENED(
                                                                                                                                                                                                                                                1,
                                                                                                                                                                                                                                                "Opened");

    int index;
    String name;

    private LineType(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
