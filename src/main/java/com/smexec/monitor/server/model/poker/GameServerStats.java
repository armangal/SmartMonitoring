package com.smexec.monitor.shared;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.smexec.monitor.server.model.ConnectedServersState;

public class GameServerStats
    implements IsSerializable {

    private LinkedList<GameServerChunk> chunks = new LinkedList<GameServerChunk>();
    /**
     * to keep interrupted tournaments in a separate list concentrated
     */
    private LinkedList<Tournament> interrupted = new LinkedList<Tournament>();

    /**
     * to keep canceled tournaments in a separate list concentrated
     */
    private LinkedList<Tournament> cancelled = new LinkedList<Tournament>();

    public GameServerStats() {}

    public void addChunk(GameServerChunk gsc) {
        chunks.add(gsc);
        if (chunks.size() > (60 * 24)) {
            chunks.remove();
        }
        if (gsc.getInterrupted() != null) {
            interrupted.addAll(gsc.getInterrupted());
            if (interrupted.size() > 200) {
                for (int i = 0; i < interrupted.size() - 200; i++) {
                    interrupted.remove();
                }
            }

            for (Tournament t : gsc.getInterrupted()) {
                Alert a = new Alert("Interrupted tournament:" + t.getCode(), t.getServerCode(), t.getDate());
                a.setDetails(t.toString());
                ConnectedServersState.addAlert(a);
            }
        }

        if (gsc.getCanceled() != null) {
            cancelled.addAll(gsc.getCanceled());
            if (cancelled.size() > 200) {
                for (int i = 0; i < cancelled.size() - 200; i++) {
                    cancelled.remove();
                }
            }

            for (Tournament t : gsc.getCanceled()) {
                Alert a = new Alert("Canceled tournament:" + t.getCode(), t.getServerCode(), t.getDate());
                a.setDetails(t.toString());
                ConnectedServersState.addAlert(a);
            }
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

    public LinkedList<Tournament> getCancelled() {
        return cancelled;
    }
}
