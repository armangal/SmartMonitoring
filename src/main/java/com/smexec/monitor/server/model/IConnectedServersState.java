package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.ConnectedServer;

public interface IConnectedServersState<SS extends ServerStataus, CS extends ConnectedServer, RR extends AbstractRefreshResult<CS>> {

    void addAlert(Alert alert);

    ConcurrentHashMap<Integer, SS> getMap();

    void mergeStats(ArrayList<CS> servers);

    void setServersConfig(ServersConfig sc);

    RR getRefreshResult();

    LinkedList<Alert> getAlertsAfter(int alertId);

    ServersConfig getServersConfig();
}
