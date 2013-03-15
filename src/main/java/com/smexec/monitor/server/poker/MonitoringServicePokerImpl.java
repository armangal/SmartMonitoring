package com.smexec.monitor.server.poker;

import java.util.LinkedList;

import com.smexec.monitor.server.AbstractMonitoringService;
import com.smexec.monitor.server.model.poker.ServerStatausPoker;
import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.poker.ConnectedServerPoker;
import com.smexec.monitor.shared.poker.RefreshResultPoker;

public class MonitoringServicePokerImpl
    extends
    AbstractMonitoringService<ServerStatausPoker, ConnectedServerPoker, RefreshResultPoker, FullRefreshResult<RefreshResultPoker, ConnectedServerPoker>> {

    private static final long serialVersionUID = 1L;

    public MonitoringServicePokerImpl() {}

    @Override
    public FullRefreshResult<RefreshResultPoker, ConnectedServerPoker> createFullRefreshResult(RefreshResultPoker refreshResult,
                                                                                               LinkedList<Alert> alerts,
                                                                                               String version) {
        return new FullRefreshResult<RefreshResultPoker, ConnectedServerPoker>(refreshResult, alerts, version);
    }
}
