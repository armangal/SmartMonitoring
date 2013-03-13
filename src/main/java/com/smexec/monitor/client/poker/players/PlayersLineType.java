package com.smexec.monitor.client.poker.players;

import com.smexec.monitor.client.widgets.ILineType;

public enum PlayersLineType implements ILineType {
    CONNECTED(0, "Connected", "green"), DROPPED(0, "Dropped", "CC0000"), OPENED(1, "Opened", "3333FF");

    int index;
    String name;
    String lineColor;

    private PlayersLineType(int index, String name, String lineColor) {
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
