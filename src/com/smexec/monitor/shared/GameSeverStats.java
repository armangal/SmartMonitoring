package com.smexec.monitor.shared;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GameSeverStats
    implements IsSerializable {

    private int lastCanceledUpdateTime;
    
    private int lastInterruptedUpdateTime;

    private LinkedList<Tournament> canceled = new LinkedList<Tournament>();

    private LinkedList<Tournament> interrupted = new LinkedList<Tournament>();

    public GameSeverStats() {}

    public LinkedList<Tournament> getCanceled() {
        return canceled;
    }

    public LinkedList<Tournament> getInterrupted() {
        return interrupted;
    }

    public void addCanceledChunk(Tournament tr) {
        canceled.add(tr);
        if (canceled.size() > 100) {
            canceled.remove();
        }
        this.lastCanceledUpdateTime = tr.getStartTime();
    }

    public void addInterruptedChunk(Tournament tr) {
        interrupted.add(tr);
        if (interrupted.size() > 100) {
            interrupted.remove();
        }
        this.lastInterruptedUpdateTime = tr.getStartTime();
    }

    public Tournament getLastCanceledChunk() {
        if (canceled.size() == 0) {
            return new Tournament();
        }
        return canceled.getLast();
    }

    public Tournament getLastInterruptedChunk() {
        if (interrupted.size() == 0) {
            return new Tournament();
        }
        return interrupted.getLast();
    }

    public int getLastCanceledUpdateTime() {
        return lastCanceledUpdateTime;
    }

    public int getLastInterruptedUpdateTime() {
        return lastInterruptedUpdateTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GameSeverStats [lastCanceledUpdateTime=");
        builder.append(lastCanceledUpdateTime);
        builder.append(", lastInterruptedUpdateTime=");
        builder.append(lastInterruptedUpdateTime);
        builder.append(", canceled=");
        builder.append(canceled);
        builder.append(", interrupted=");
        builder.append(interrupted);
        builder.append("]");
        return builder.toString();
    }

}
