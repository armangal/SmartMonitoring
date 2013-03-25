package com.smexec.monitor.shared.alert;

public enum AlertGroup {

    SYSTEM(1);

    int id;

    private AlertGroup(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
