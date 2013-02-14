package com.smexec.monitor.shared;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

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
     * key: timestamp in mmddHHMM format coming from server value: the info retreived from server
     */
    private LinkedList<ChannelChunkStats> list = new LinkedList<ChannelChunkStats>();

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

    public LinkedList<ChannelChunkStats> getMapValues() {
        return list;
    }

    public void addChunk(ChannelChunkStats cscs) {
        list.add(cscs);
        if (list.size() > 100) {
            list.remove();
        }
        this.totalConnections += cscs.getConnectedBinarySessions() + cscs.getConnectedLegacySessions();
        this.totalDrops += cscs.getDisconnectedBinarySessions() + cscs.getDisconnectedLegacySessions();
        this.lastUpdateTime = cscs.getStartTime();
    }

    public ChannelChunkStats getLastChunk() {
        if (list.size() == 0) {
            return new ChannelChunkStats();
        }
        return list.getLast();
    }

    public void merge(ChannelSeverStats css) {
        this.openBinarySessions += css.getOpenBinarySessions();
        this.openStringSessions += css.getOpenStringSessions();
        this.totalConnections += css.getTotalConnections();
        this.totalDrops += css.getTotalDrops();

        HashMap<Integer, ChannelChunkStats> map = copyListToMap();

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
                                             agg.getOpenStringSessions() + cscs.getOpenStringSessions(),
                                             -1);

            }
            if (cscs.getStartTime() > this.lastUpdateTime) {
                this.lastUpdateTime = cscs.getStartTime();
            }
            map.put(cscs.getStartTime(), cscs);
        }
        copyMapToList(map);
    }

    private void copyMapToList(HashMap<Integer, ChannelChunkStats> map) {
        list.clear();
        list.addAll(map.values());
        Collections.sort(list, new Comparator<ChannelChunkStats>() {

            @Override
            public int compare(ChannelChunkStats o1, ChannelChunkStats o2) {
                return o1.getStartTime() - o2.getStartTime();
            }
        });
    }

    private HashMap<Integer, ChannelChunkStats> copyListToMap() {
        HashMap<Integer, ChannelChunkStats> map = new HashMap<Integer, ChannelChunkStats>(list.size());
        for (ChannelChunkStats css : list) {
            map.put(css.getStartTime(), css);
        }
        return map;
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
        builder.append(", lastUpdate=");
        builder.append(lastUpdateTime);
        builder.append(", list=");
        builder.append(list.size());
        builder.append("]");
        return builder.toString();
    }

}
