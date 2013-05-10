package com.smexec.monitor.shared.utils;

public enum RefreshProgress {
    ONE("|"), TWO("/"), THREE("-"), FOUR("\\");

    private String value;

    private RefreshProgress(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public RefreshProgress getNext() {
        for (RefreshProgress rps : values()) {
            if (rps.ordinal() == this.ordinal() + 1) {
                return rps;
            }
        }

        return ONE;
    }

}
