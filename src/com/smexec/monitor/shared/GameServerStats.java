package com.smexec.monitor.shared;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GameServerStats
    implements IsSerializable {

    private LinkedList<GameServerChunk> chunks = new LinkedList<GameServerChunk>();
    /**
     * to keep interrupted tournaments in a separate list concentrated
     */
    private LinkedList<Tournament> interrupted = new LinkedList<Tournament>();

    public GameServerStats() {}

    public void addChunk(GameServerChunk gsc) {
        chunks.add(gsc);
        if (chunks.size() > (60 * 24)) {
            chunks.remove();
        }
        if (gsc.getInterrupted() != null) {
            interrupted.addAll(gsc.getInterrupted());
        }
    }

    public GameServerChunk getLastChunk() {
        if (chunks.size() == 0) {
            return new GameServerChunk();
        }
        return chunks.getLast();
    }

    public LinkedList<GameServerChunk> getChunks() {
        return chunks;
    }

    public LinkedList<Tournament> getInterrupted() {
        return interrupted;
    }
}
