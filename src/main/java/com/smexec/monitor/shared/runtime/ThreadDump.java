package com.smexec.monitor.shared.runtime;

import java.io.Serializable;
import java.util.LinkedList;

public class ThreadDump
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dump;
    private LinkedList<ThreadInfo> threads;

    public ThreadDump() {}

    public ThreadDump(String dump, LinkedList<ThreadInfo> threads) {
        super();
        this.dump = dump;
        this.threads = threads;
    }

    public String getDump() {
        return dump;
    }

    public void setDump(String dump) {
        this.dump = dump;
    }

    public LinkedList<ThreadInfo> getThreads() {
        return threads;
    }

    public void setThreads(LinkedList<ThreadInfo> threads) {
        this.threads = threads;
    }

}
