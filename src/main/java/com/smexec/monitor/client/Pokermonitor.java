package com.smexec.monitor.client;

import com.allen_sauer.gwt.log.client.Log;
import com.smexec.monitor.client.alerts.AlertsWidget;
import com.smexec.monitor.client.poker.netty.NettyWidget;
import com.smexec.monitor.client.poker.players.PlayersWidget;
import com.smexec.monitor.client.poker.tournaments.TournamentsWidget;
import com.smexec.monitor.client.servers.ServersWidget;
import com.smexec.monitor.client.smartpool.ThreadPoolsWidget;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.poker.ConnectedServerPoker;
import com.smexec.monitor.shared.poker.RefreshResultPoker;

public class Pokermonitor
    extends Smartexecutormonitor<ConnectedServerPoker, RefreshResultPoker, FullRefreshResult<RefreshResultPoker, ConnectedServerPoker>> {

    public Pokermonitor() {
        super();
        Log.debug("Pokermonitor initilized");
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        Log.debug("Starting Pokermonitor client.");

        super.onModuleLoad();

    }

    @Override
    public void registerWidgets() {
        addMonitoringWidget(new TournamentsWidget());
        addMonitoringWidget(new PlayersWidget());
        addMonitoringWidget(new ThreadPoolsWidget());
        addMonitoringWidget(new ServersWidget());
        addMonitoringWidget(new AlertsWidget());
        addMonitoringWidget(new NettyWidget());
    }

}
