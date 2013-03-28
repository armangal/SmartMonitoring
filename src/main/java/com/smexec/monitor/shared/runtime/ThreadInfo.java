package com.smexec.monitor.shared.runtime;

import java.io.Serializable;

public class ThreadInfo
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private long id;
    private String state;

    public ThreadInfo() {}

    public ThreadInfo(String name, long id, String state) {
        super();
        this.name = name;
        this.id = id;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getState() {
        return state;
    }

}
