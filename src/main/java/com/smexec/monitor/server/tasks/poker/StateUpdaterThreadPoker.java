package com.smexec.monitor.server.tasks.poker;

import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.poker.ServerStatausPoker;
import com.smexec.monitor.server.tasks.AbstractStateUpdaterThread;
import com.smexec.monitor.shared.poker.ConnectedServerPoker;
import com.smexec.monitor.shared.poker.RefreshResultPoker;

public class StateUpdaterThreadPoker
    extends AbstractStateUpdaterThread<ServerStatausPoker, RefresherPoker, ConnectedServerPoker, RefreshResultPoker> {

    @Override
    public RefresherPoker getRefresher(ServerStatausPoker ss) {
        return new RefresherPoker(ss);
    }

    @Override
    public ConnectedServerPoker getConnectedServer(ServerStatausPoker ss) {
        ServerConfig sc = ss.getServerConfig();
        return new ConnectedServerPoker(sc.getName(),
                                        sc.getServerCode(),
                                        sc.getIp(),
                                        sc.getJmxPort(),
                                        ss.isConnected(),
                                        ss.getLastMemoryUsage(),
                                        ss.getLastGCHistory(),
                                        ss.getUpTime(),
                                        ss.getChannelSeverStats(),
                                        ss.getCpuUtilization());
    }

}
