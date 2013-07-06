package com.smexec.monitor.shared.alert;

import java.util.Date;
import java.util.LinkedList;

import com.smexec.monitor.client.AbstractRefreshResponse;

public class RefreshAlertsResponse
    extends AbstractRefreshResponse {

    private static final long serialVersionUID = 1L;

    private LinkedList<Alert> alerts;

    public RefreshAlertsResponse() {
        super(new Date().toString());
    }

    public RefreshAlertsResponse(String serverTime, LinkedList<Alert> alerts) {
        super(serverTime);
        this.alerts = alerts;
    }

    public LinkedList<Alert> getAlerts() {
        return alerts;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RefreshAlertsResponse [alerts=").append(alerts).append(super.toString()).append("]");
        return builder.toString();
    }

}
