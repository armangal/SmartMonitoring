package com.smexec.monitor.shared.alert;

import java.io.Serializable;

public class AlertThreshold
    implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * when we don't want to send alert immediately after it occurs but want to make sure that X alerts
     * occurred during Y period of time. So this's the Y
     */
    private long alertTriggerTime;

    /**
     * this's the X from above
     */
    private int alertTriggerCount;

    AlertThreshold() {}

    public AlertThreshold(long alertTriggerTime, int alertTriggerCount) {
        super();
        this.alertTriggerTime = alertTriggerTime;
        this.alertTriggerCount = alertTriggerCount;
    }

    public long getAlertTriggerTime() {
        return alertTriggerTime;
    }

    public int getAlertTriggerCount() {
        return alertTriggerCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AlertThreshold [alertTriggerTime=").append(alertTriggerTime).append(", alertTriggerCount=").append(alertTriggerCount).append("]");
        return builder.toString();
    }

}
