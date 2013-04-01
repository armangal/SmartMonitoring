package com.smexec.monitor.shared.alert;

public enum AlertType implements IAlertType {

    MEMORY("mem", 1, AlertGroup.SYSTEM, true, new AlertThreshold(2 * 60 * 1000L, 5)), //
    CPU("cpu", 2, AlertGroup.SYSTEM, true, new AlertThreshold(2 * 60 * 1000L, 5)), //
    SERVER_DISCONNECTED("srvDisc", 3, AlertGroup.SYSTEM, true, new AlertThreshold(Long.MAX_VALUE, -1));

    private String name;
    private Integer id;
    private AlertGroup alertGroup;
    private boolean sendMail;
    private AlertThreshold alertThreshold;

    private AlertType(String name, Integer id, AlertGroup alertGroup, boolean sendMail, AlertThreshold alertThreshold) {
        this.name = name;
        this.id = id;
        this.alertGroup = alertGroup;
        this.sendMail = sendMail;
        this.alertThreshold = alertThreshold;
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

    @Override
    public AlertThreshold getAlertThreshold() {
        return alertThreshold;
    }
}
