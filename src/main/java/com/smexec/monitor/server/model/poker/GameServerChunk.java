package com.smexec.monitor.server.model.poker;

import java.util.LinkedList;

import com.smexec.monitor.shared.poker.Tournament;

public class GameServerChunk {

    private int startTime;

    private int endTime;

    /**
     * list of canceled tournaments during this period
     */
    private LinkedList<Tournament> canceled;

    /**
     * list of interrupted tournaments during this period
     */
    private LinkedList<Tournament> interrupted;

    public GameServerChunk() {}

    public GameServerChunk(int startTime, int endTime) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public LinkedList<Tournament> getCanceled() {
        return canceled;
    }

    public LinkedList<Tournament> getInterrupted() {
        return interrupted;
    }

    public void setCanceled(LinkedList<Tournament> canceled) {
        this.canceled = canceled;
    }

    public void setInterrupted(LinkedList<Tournament> interrupted) {
        this.interrupted = interrupted;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GameServerChunk [startTime=")
               .append(startTime)
               .append(", endTime=")
               .append(endTime)
               .append(", canceled=")
               .append(canceled)
               .append(", interrupted=")
               .append(interrupted)
               .append("]");
        return builder.toString();
    }

}
