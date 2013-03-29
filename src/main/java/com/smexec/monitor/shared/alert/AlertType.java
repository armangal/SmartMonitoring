package com.smexec.monitor.shared.alert;

public enum AlertType implements IAlertType {

    MEMORY("mem", 1, AlertGroup.SYSTEM, true, 2 * 60 * 1000L), //
    CPU("cpu", 2, AlertGroup.SYSTEM, true, 2 * 60 * 1000L), //
    SERVER_DISCONNECTED("srvDisc", 3, AlertGroup.SYSTEM, true, 5 * 1000L);

    private String name;
    private Integer id;
    private AlertGroup alertGroup;
    private boolean sendMail;
    private long alertFrequency;

    private AlertType(String name, Integer id, AlertGroup alertGroup, boolean sendMail, long alertFrequency) {
        this.name = name;
        this.id = id;
        this.alertGroup = alertGroup;
        this.sendMail = sendMail;
        this.alertFrequency = alertFrequency;
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
    public long getAlertFrequency() {
        return alertFrequency;
    }
}
