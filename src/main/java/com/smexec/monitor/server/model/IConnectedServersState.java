package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

public interface IConnectedServersState<S extends ServerStataus, C extends ConnectedServer> {

    void addAlert(Alert alert);

    ConcurrentHashMap<Integer, S> getMap();

    void mergeStats(ArrayList<C> servers);

    void setServersConfig(ServersConfig sc);

    RefreshResult<C> getRefreshResult();

    LinkedList<Alert> getAlertsAfter(int alertId);

    ServersConfig getServersConfig();
}
