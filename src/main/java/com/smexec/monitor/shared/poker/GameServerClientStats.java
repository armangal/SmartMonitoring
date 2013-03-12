package com.smexec.monitor.shared.poker;

import java.io.Serializable;
import java.util.LinkedList;

public class GameServerClientStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private int period; // the period of interrupted tournaments shown

    private LinkedList<Tournament> interrupted = new LinkedList<Tournament>();

    public GameServerClientStats() {}

    public LinkedList<Tournament> getInterrupted() {
        return interrupted;
    }

    public int getPeriod() {
        return period;
    }
}
