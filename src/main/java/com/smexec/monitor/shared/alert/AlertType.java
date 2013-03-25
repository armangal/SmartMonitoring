package com.smexec.monitor.shared.alert;

public enum AlertType implements IAlertType {

    MEMORY("mem", 1, AlertGroup.SYSTEM), CPU("cpu", 2, AlertGroup.SYSTEM);

    private String name;
    private Integer id;
    private AlertGroup alertGroup;

    private AlertType(String name, Integer id, AlertGroup alertGroup) {
        this.name = name;
        this.id = id;
        this.alertGroup = alertGroup;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public AlertGroup getAlertGroup() {
        return alertGroup;
    }
}
