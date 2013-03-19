package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.LinkedList;

public class FullRefreshResult<RR extends AbstractRefreshResult<CS>, CS extends ConnectedServer>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private RR refreshResult;

    private LinkedList<Alert> alerts;

    private String version;

    public FullRefreshResult() {}

    public FullRefreshResult(RR refreshResult, LinkedList<Alert> alerts, String version) {
        super();
        this.refreshResult = refreshResult;
        this.alerts = alerts;
        this.version = version;
    }

    public LinkedList<Alert> getAlerts() {
        return alerts;
    }

    public RR getRefreshResult() {
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

    public Integer getLastAlertId() {
        int id = 0;
        if (alerts != null && !alerts.isEmpty()) {
            for (Alert alert : alerts) {
                if (alert.getId() > id) {
                    id = alert.getId();
                }
            }
        }
        return id;
    }

}
