package com.smexec.monitor.shared.alert;

import com.smexec.monitor.client.AbstractRefreshRequest;

public class RefreshAlertsRequest
    extends AbstractRefreshRequest {

    private static final long serialVersionUID = 1L;

    private int lastAlertId;

    public RefreshAlertsRequest() {}

    public RefreshAlertsRequest(int lastAlertId) {
        super();
        this.lastAlertId = lastAlertId;
    }

    public int getLastAlertId() {
        return lastAlertId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RefreshAlertsRequest [lastAlertId=").append(lastAlertId).append("]");
        return builder.toString();
    }

}
