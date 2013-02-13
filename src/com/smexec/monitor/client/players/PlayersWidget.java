package com.smexec.monitor.client.players;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.LineType;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.ChannelChunkStats;
import com.smexec.monitor.shared.ChannelSeverStats;
import com.smexec.monitor.shared.ChartFeed;

public class PlayersWidget
    extends AbstractMonitoringWidget {

    private FlowPanel fp = new FlowPanel();
    private MonitoringLineChart connected = new MonitoringLineChart(new LineType[] {LineType.CONNECTED, LineType.PLAYING}, "Players", "Time", "Online Players");
    private MonitoringLineChart reconnected = new MonitoringLineChart(new LineType[] {LineType.DROPPED, LineType.OPENED}, "Players", "Time", "Drops vs. New");
    private FlexTable playersTable = new FlexTable();

    public PlayersWidget() {
        super("Online Players");
        setStyleName("playersWidget");
        getScrollPanel().add(fp);

        FlowPanel players = getPlayers();
        FlowPanel charts = getCharts();
        FlowPanel servers = getServers();
        fp.add(players);
        fp.add(charts);
        fp.add(servers);
    }

    public void update(ChannelSeverStats css) {
        int i = 0;
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(css.getOpenBinarySessions() + css.getOpenStringSessions()));
        playersTable.setText(i++, 1, "????");
        ChannelChunkStats lastChunk = css.getLastChunk();
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lastChunk.getDisconnectedBinarySessions() + lastChunk.getDisconnectedLegacySessions()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lastChunk.getConnectedBinarySessions() + lastChunk.getConnectedLegacySessions()));

        connected.updateChart(new ChartFeed());
        reconnected.updateChart(new ChartFeed());

    }

    private FlowPanel getServers() {
        FlowPanel fp = new FlowPanel();
        fp.setStyleName("servers");
        return fp;
    }

    private FlowPanel getCharts() {
        FlowPanel fp = new FlowPanel();
        fp.setStyleName("charts");
        fp.add(connected);
        fp.add(reconnected);
        connected.setStyleName("playersChart");
        reconnected.setStyleName("reconnectionsChart");

        return fp;
    }

    private FlowPanel getPlayers() {

        FlowPanel fp = new FlowPanel();
        fp.setStyleName("players");

        playersTable.getElement().setId("infoTable");
        fp.add(playersTable);

        int i = 0;
        playersTable.setText(i++, 0, "Connected:");
        playersTable.setText(i++, 0, "Playing:");
        playersTable.setText(i++, 0, "Disconnected (min)");
        playersTable.setText(i++, 0, "Connected (min)");

        return fp;
    }

}
