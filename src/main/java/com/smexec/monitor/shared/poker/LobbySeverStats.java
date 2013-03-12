package com.smexec.monitor.shared;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LobbySeverStats
    implements IsSerializable {

    private int lastUpdateTime;
    /**
     * key: timestamp in mmddHHMM format coming from server value: the info retreived from server
     */
    private LinkedList<LobbyChunkStats> list = new LinkedList<LobbyChunkStats>();

    public LobbySeverStats() {}

    public LinkedList<LobbyChunkStats> getLobbyStats() {
        return list;
    }

    public void addChunk(LobbyChunkStats lcs) {
        list.add(lcs);
        if (list.size() > 100) {
            list.remove();
        }
        this.lastUpdateTime = lcs.getStartTime();
    }

    public LobbyChunkStats getLastChunk() {
        if (list.size() == 0) {
            return new LobbyChunkStats();
        }
        return list.getLast();
    }

    public int getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LobbySeverStats [lastUpdateTime=").append(lastUpdateTime).append(", list=").append(list).append("]");
        return builder.toString();
    }

}
