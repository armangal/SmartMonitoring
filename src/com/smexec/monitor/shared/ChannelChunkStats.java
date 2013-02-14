package com.smexec.monitor.shared;

import java.io.Serializable;

public class ChannelChunkStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private int connectedBinarySessions;
    private int connectedLegacySessions;
    private int disconnectedBinarySessions;
    private int disconnectedLegacySessions;
    private int startTime;
    private int endTime;
    private int openBinarySessions;
    private int openStringSessions;
    private int playing;

    public ChannelChunkStats() {}

    public ChannelChunkStats(int connectedBinarySessions,
                             int connectedLegacySessions,
                             int disconnectedBinarySessions,
                             int disconnectedLegacySessions,
                             int startTime,
                             int endTime,
                             int openBinarySessions,
                             int openStringSessions,
                             int playing) {
        super();
        this.connectedBinarySessions = connectedBinarySessions;
        this.connectedLegacySessions = connectedLegacySessions;
        this.disconnectedBinarySessions = disconnectedBinarySessions;
        this.disconnectedLegacySessions = disconnectedLegacySessions;
        this.startTime = startTime;
        this.endTime = endTime;
        this.openBinarySessions = openBinarySessions;
        this.openStringSessions = openStringSessions;
        this.playing = playing;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getConnectedBinarySessions() {
        return connectedBinarySessions;
    }

    public int getConnectedLegacySessions() {
        return connectedLegacySessions;
    }

    public int getDisconnectedBinarySessions() {
        return disconnectedBinarySessions;
    }

    public int getDisconnectedLegacySessions() {
        return disconnectedLegacySessions;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getStartTimeForChart() {
        return startTime % 10000;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getOpenBinarySessions() {
        return openBinarySessions;
    }

    public int getOpenStringSessions() {
        return openStringSessions;
    }

    public int getPlaying() {
        return playing;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + startTime;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChannelChunkStats other = (ChannelChunkStats) obj;
        if (startTime != other.startTime)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChannelChunkStats [connectedBinarySessions=");
        builder.append(connectedBinarySessions);
        builder.append(", connectedLegacySessions=");
        builder.append(connectedLegacySessions);
        builder.append(", disconnectedBinarySessions=");
        builder.append(disconnectedBinarySessions);
        builder.append(", disconnectedLegacySessions=");
        builder.append(disconnectedLegacySessions);
        builder.append(", startTime=");
        builder.append(startTime);
        builder.append(", endTime=");
        builder.append(endTime);
        builder.append(", openBinarySessions=");
        builder.append(openBinarySessions);
        builder.append(", openStringSessions=");
        builder.append(openStringSessions);
        builder.append(", playing=");
        builder.append(playing);
        builder.append("]");
        return builder.toString();
    }

}
