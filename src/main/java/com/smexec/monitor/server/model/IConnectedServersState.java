package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;

public interface IConnectedServersState<SS extends ServerStataus, CS extends ConnectedServer, RR extends AbstractRefreshResult<CS>> {

    ConcurrentHashMap<Integer, SS> getMap();

    void mergeStats(ArrayList<CS> servers);

    void setServersConfig(ServersConfig sc);

    RR getRefreshResult();

    ServersConfig getServersConfig();
}
