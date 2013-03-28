package com.smexec.monitor.server.tasks;

import java.util.Date;

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServerConfig;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

public class StateUpdaterThread
    extends AbstractStateUpdaterThread<ServerStataus, Refresher<ServerStataus>, ConnectedServer, RefreshResult> {

    @Override
    public Refresher<ServerStataus> getRefresher(ServerStataus ss, Date executionDate, int excutionNumber) {
        return new Refresher<ServerStataus>(ss, executionDate, excutionNumber);
    }

    @Override
    public ConnectedServer getConnectedServer(ServerStataus ss) {
        ServerConfig sc = ss.getServerConfig();
        return new ConnectedServer(sc.getName(),
                                   sc.getServerCode(),
                                   sc.getIp(),
                                   sc.getJmxPort(),
                                   ss.isConnected(),
                                   ss.getLastMemoryUsage(),
                                   ss.getLastGCHistory(),
                                   ss.getUpTime(),
                                   ss.getCpuUtilization().getLastPercent());
    }

}
