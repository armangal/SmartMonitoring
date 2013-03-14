package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.LinkedList;

public class FullRefreshResult
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private RefreshResult refreshResult;

    private LinkedList<Alert> alerts;

    private String version;

    public FullRefreshResult() {}

    public FullRefreshResult(RefreshResult refreshResult, LinkedList<Alert> alerts, String version) {
        super();
        this.refreshResult = refreshResult;
        this.alerts = alerts;
        this.version = version;
    }

    public LinkedList<Alert> getAlerts() {
        return alerts;
    }

    public RefreshResult getRefreshResult() {
        return refreshResult;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FullRefreshResult [refreshResult=").append(refreshResult).append(", alerts=").append(alerts).append("]");
        return builder.toString();
    }

}
