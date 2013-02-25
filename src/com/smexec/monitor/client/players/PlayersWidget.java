package com.smexec.monitor.client.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.LineType;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.ChannelChunkStats;
import com.smexec.monitor.shared.ChannelSeverStats;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.LobbyChunkStats;
import com.smexec.monitor.shared.LobbySeverStats;
import com.smexec.monitor.shared.RefreshResult;

public class PlayersWidget
    extends AbstractMonitoringWidget {

    private FlowPanel fp = new FlowPanel();
    private MonitoringLineChart connected = new MonitoringLineChart(new LineType[] {LineType.CONNECTED}, "Players", "Time", "Online Players");
    private MonitoringLineChart reconnected = new MonitoringLineChart(new LineType[] {LineType.DROPPED, LineType.OPENED}, "Players", "Time", "Drops vs. New");
    private FlexTable playersTable = new FlexTable();

    private FlexTable channelServers = new FlexTable();
    private ScrollPanel channelScrollPanel = new ScrollPanel();
    private ArrayList<ConnectedServer> servers;
    private ClickHandler getServerStats = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            String code = ((Widget) event.getSource()).getElement().getAttribute("code");
            if (servers != null && !servers.isEmpty()) {
                ConnectedServer cs = null;
                for (ConnectedServer cs1 : servers) {
                    if (cs1.getServerCode().equals(Integer.valueOf(code))) {
                        cs = cs1;
                        break;
                    }
                }
                if (cs == null) {
                    Window.alert("Server:" + code + " not found in list");
                } else {
                    ChannelServerStatasPopup db = new ChannelServerStatasPopup(cs);

                    db.center();
                }
            }
        }
    };

    public PlayersWidget() {
        super("Online Players");
        addStyleName("playersWidget");
        getDataPanel().add(fp);

        FlowPanel players = getPlayers();
        FlowPanel charts = getCharts();
        FlowPanel servers = getServers();
        fp.add(players);
        fp.add(charts);
        fp.add(servers);
        channelScrollPanel.setHeight("100%");
    }

    public void update(RefreshResult result) {
        ChannelSeverStats css = result.getChannelSeverStats();
        ArrayList<ConnectedServer> servers = result.getServers();
        LobbySeverStats lss = result.getLobbySeverStats();

        this.servers = servers;
        int i = 0;
        Log.debug("Players:" + css);
        ChannelChunkStats lastChunk = css.getLastChunk();
        Log.debug("Last Chunk:" + lastChunk);
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lastChunk.getOpenBinarySessions() + lastChunk.getOpenStringSessions()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lastChunk.getDisconnectedBinarySessions() + lastChunk.getDisconnectedLegacySessions()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lastChunk.getConnectedBinarySessions() + lastChunk.getConnectedLegacySessions()));

        LobbyChunkStats lobbyStats = lss.getLastChunk();
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lobbyStats.getRealTables()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lobbyStats.getRealActiveTables()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lobbyStats.getFunTables()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lobbyStats.getFunActiveTables()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lobbyStats.getRealCashPLayers()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lobbyStats.getFunCashPlayers()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lobbyStats.getRealSpeedRooms()));
        playersTable.setText(i++, 1, ClientStringFormatter.formatNumber(lobbyStats.getRealActiveSpeedRoomPlayers()));

        ArrayList<ChannelChunkStats> values = new ArrayList<ChannelChunkStats>(css.getChannelStats());
        Collections.sort(values, new Comparator<ChannelChunkStats>() {

            @Override
            public int compare(ChannelChunkStats o1, ChannelChunkStats o2) {
                return o1.getStartTime() - o2.getStartTime();
            }
        });
        updateOnlinePlayers(values);
        updatedropsAndConnections(values);
        updateChannelServers(servers);

    }

    private void updateChannelServers(ArrayList<ConnectedServer> servers) {
        channelServers.removeFromParent();
        channelServers = new FlexTable();
        channelScrollPanel.add(channelServers);
        channelServers.getElement().setId("infoTable");

        channelServers.setText(0, 0, "Server");
        channelServers.setText(0, 1, "Online");
        channelServers.setText(0, 2, "Dropped");
        channelServers.setText(0, 3, "New");
        channelServers.setText(0, 4, "Total Drops");
        channelServers.setText(0, 5, "Total New");
        channelServers.getRowFormatter().getElement(0).setId("th");

        int row = 1, col = 0;
        for (ConnectedServer cs : servers) {
            if (cs.getStatus() && cs.isChannelServer()) {
                col = 0;
                ChannelSeverStats css = cs.getChannelSeverStats();
                final HTML name = new HTML("<a href=#>" + cs.getServerCode() + ", " + cs.getName() + "</a>");
                name.getElement().setAttribute("code", "" + cs.getServerCode());
                name.addClickHandler(getServerStats);

                channelServers.setWidget(row, col++, name);
                channelServers.setText(row,
                                       col++,
                                       ClientStringFormatter.formatNumber(css.getLastChunk().getOpenBinarySessions()
                                                                          + css.getLastChunk().getOpenStringSessions()));
                channelServers.setText(row,
                                       col++,
                                       ClientStringFormatter.formatNumber(css.getLastChunk().getDisconnectedBinarySessions()
                                                                          + css.getLastChunk().getDisconnectedLegacySessions()));
                channelServers.setText(row,
                                       col++,
                                       ClientStringFormatter.formatNumber(css.getLastChunk().getConnectedBinarySessions()
                                                                          + css.getLastChunk().getConnectedLegacySessions()));
                channelServers.setText(row, col++, ClientStringFormatter.formatNumber(css.getTotalDrops()));
                channelServers.setText(row++, col++, ClientStringFormatter.formatNumber(css.getTotalConnections()));
            }
        }
    }

    private void updatedropsAndConnections(ArrayList<ChannelChunkStats> values) {
        ChartFeed dropsAndConnections = new ChartFeed(values.size(), 3);
        for (int k = 0; k < 3; k++) {
            for (int j = 0; j < values.size(); j++) {
                if (k == 0) {
                    // drops
                    dropsAndConnections.getValues()[k][j] = values.get(j).getDisconnectedBinarySessions() + values.get(j).getDisconnectedLegacySessions();
                } else if (k == 1) {
                    // new connetions
                    dropsAndConnections.getValues()[k][j] = values.get(j).getConnectedBinarySessions() + values.get(j).getConnectedLegacySessions();
                } else if (k == 2) {
                    dropsAndConnections.getValues()[k][j] = values.get(j).getStartTimeForChart();
                }
            }
        }

        Log.debug("Updating drops, values size:" + values.size());
        reconnected.updateChart(dropsAndConnections, true);
    }

    private void updateOnlinePlayers(ArrayList<ChannelChunkStats> values) {
        ChartFeed online = new ChartFeed(values.size(), 2);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < values.size(); j++) {
                if (k == 0) {
                    // connected
                    online.getValues()[k][j] = values.get(j).getOpenBinarySessions() + values.get(j).getOpenStringSessions();
                } else if (k == 1) {
                    online.getValues()[k][j] = values.get(j).getStartTimeForChart();
                }
            }
        }
        Log.debug("Updating connected, values size:" + values.size());

        connected.updateChart(online, true);
    }

    private FlowPanel getServers() {
        FlowPanel fp = new FlowPanel();
        fp.setStyleName("servers");

        channelScrollPanel.add(channelServers);
        fp.add(channelScrollPanel);
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
        playersTable.setText(i++, 0, "Disc last min");
        playersTable.setText(i++, 0, "Conn last min");

        playersTable.setText(i++, 0, "Real Tables");
        playersTable.setText(i++, 0, "Real Active Tab.");

        playersTable.setText(i++, 0, "Fun Table");
        playersTable.setText(i++, 0, "Fun Active Tab.");

        playersTable.setText(i++, 0, "Real Players");
        playersTable.setText(i++, 0, "Fun Player");

        playersTable.setText(i++, 0, "S.P. Rooms");
        playersTable.setText(i++, 0, "S.P. Players");

        return fp;
    }

}
