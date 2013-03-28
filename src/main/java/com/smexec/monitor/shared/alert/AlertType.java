package com.smexec.monitor.shared.alert;

public enum AlertType implements IAlertType {

    MEMORY("mem", 1, AlertGroup.SYSTEM, true), CPU("cpu", 2, AlertGroup.SYSTEM, true);

    private String name;
    private Integer id;
    private AlertGroup alertGroup;
    private boolean sendMail;

    private AlertType(String name, Integer id, AlertGroup alertGroup, boolean sendMail) {
        this.name = name;
        this.id = id;
        this.alertGroup = alertGroup;
        this.sendMail = sendMail;
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
    
    @Override
    public boolean sendMail() {
        return sendMail;
    }
}
