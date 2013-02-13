package com.smexec.monitor.client.widgets;

public enum LineType {
    MIN(2, "Min", "green"), MAX(0, "Max", "red"), AVG(1, "Avg", "orange"), 
    SUBMITED(0, "Submited", "grey"), EXECUTED(1, "Executed", "CC9900"), COMPLETED(4,"Completed","green"), FAILED(2,"Failed","FF0000"), REJECTED(3,"Rejected", "FF00CC"), 
    CONNECTED(0,"Connected", "green"), PLAYING(1,"Playing", "660066"), DROPPED(0,"Dropped","CC0000"), OPENED(1,"Opened","3333FF");

    int index;
    String name;
    String lineColor;

    private LineType(int index, String name, String lineColor) {
        this.index = index;
        this.name = name;
        this.lineColor = lineColor;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
    
    
    public String getLineColor() {
        return lineColor;
    }
}
