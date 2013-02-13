package com.smexec.monitor.shared;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ChannelSeverStats
    implements IsSerializable {

    /**
     * currently open binary sessions
     */
    private int openBinarySessions = 0;

    /**
     * currently open string sessions
     */
    private int openStringSessions = 0;

    /**
     * total drops sessions recorded since the monitoring system is running
     */
    private int totalDrops = 0;

    /**
     * total newly opened sessions recorded since the monitoring system is running
     */
    private int totalConnections = 0;

    private int lastUpdateTime;
    /**
     * key: timestamp in HHMM format coming from server value: the info retreived from server
     */
    private Map<Integer, ChannelChunkStats> map = new HashMap<Integer, ChannelChunkStats>();

    public ChannelSeverStats() {}

    public int getOpenBinarySessions() {
        return openBinarySessions;
    }

    public void setOpenBinarySessions(int openBinarySessions) {
        this.openBinarySessions = openBinarySessions;
    }

    public int getOpenStringSessions() {
        return openStringSessions;
    }

    public void setOpenStringSessions(int openStringSessions) {
        this.openStringSessions = openStringSessions;
    }

    public int getTotalDrops() {
        return totalDrops;
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    public Collection<ChannelChunkStats> getMapValues() {
        return map.values();
    }

    public void addChunk(ChannelChunkStats cscs) {
        map.put(cscs.getStartTime(), cscs);
        this.totalConnections += cscs.getConnectedBinarySessions() + cscs.getConnectedLegacySessions();
        this.totalDrops += cscs.getDisconnectedBinarySessions() + cscs.getDisconnectedLegacySessions();
        this.lastUpdateTime = cscs.getStartTime();
    }

    public void setMap(LinkedHashMap<Integer, ChannelChunkStats> map) {
        this.map = map;
    }

    public ChannelChunkStats getLastChunk() {
        ChannelChunkStats channelChunkStats = map.get(lastUpdateTime);
        if (channelChunkStats == null) {
            channelChunkStats = new ChannelChunkStats();
        }
        return channelChunkStats;
    }

    public void merge(ChannelSeverStats css) {
        this.openBinarySessions += css.getOpenBinarySessions();
        this.openStringSessions += css.getOpenStringSessions();
        this.totalConnections += css.getTotalConnections();
        this.totalDrops += css.getTotalDrops();
        for (ChannelChunkStats cscs : css.getMapValues()) {
            if (map.containsKey(cscs.getStartTime())) {
                // merge
                ChannelChunkStats agg = map.get(cscs.getStartTime());
                cscs = new ChannelChunkStats(agg.getConnectedBinarySessions() + cscs.getConnectedBinarySessions(),
                                             agg.getConnectedLegacySessions() + cscs.getConnectedLegacySessions(),
                                             agg.getDisconnectedBinarySessions() + cscs.getDisconnectedBinarySessions(),
                                             agg.getDisconnectedLegacySessions() + cscs.getDisconnectedLegacySessions(),
                                             agg.getStartTime(),
                                             agg.getEndTime(),
                                             agg.getOpenBinarySessions() + cscs.getOpenBinarySessions(),
                                             agg.getOpenStringSessions() + cscs.getOpenStringSessions());

            }
            map.put(cscs.getStartTime(), cscs);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChannelSeverStats [openBinarySessions=");
        builder.append(openBinarySessions);
        builder.append(", openStringSessions=");
        builder.append(openStringSessions);
        builder.append(", totalDrops=");
        builder.append(totalDrops);
        builder.append(", totalConnections=");
        builder.append(totalConnections);
        builder.append(", map=");
        builder.append(map.size());
        builder.append("]");
        return builder.toString();
    }

}
