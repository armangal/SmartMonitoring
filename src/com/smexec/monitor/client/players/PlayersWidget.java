package com.smexec.monitor.client.players;

import java.util.Random;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.LineChart;

public class PlayersWidget
    extends AbstractMonitoringWidget {

    private FlowPanel fp = new FlowPanel();
    private LineChart connected = new LineChart(300, 70);
    private LineChart reconnected = new LineChart(300, 70);

    public PlayersWidget() {
        super("Online Players");
        setStyleName("playersWidget");
        getScrollPanel().add(fp);
    }

    public void update() {
        fp.clear();
        FlowPanel players = getPlayers();
        FlowPanel charts = getCharts();
        FlowPanel servers = getServers();
        fp.add(players);
        fp.add(charts);
        fp.add(servers);
        
        connected.update();
        reconnected.update();

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
        return fp;
    }

    private FlowPanel getPlayers() {

        FlowPanel fp = new FlowPanel();
        fp.setStyleName("players");
        FlexTable ft = new FlexTable();
        ft.getElement().setId("infoTable");
        fp.add(ft);
        Random r = new Random();

        int i = 0, j = 0;
        ft.setText(i++, j, "Connected:");
        ft.setText(i++, j, "Playing:");
        ft.setText(i++, j, "Disconnected (min)");
        ft.setText(i++, j++, "Connected (min)");

        i = 0;
        ft.setText(i++, j, ClientStringFormatter.formatNumber(r.nextInt(10000)));
        ft.setText(i++, j, ClientStringFormatter.formatNumber(r.nextInt(4000)));
        ft.setText(i++, j, ClientStringFormatter.formatNumber(r.nextInt(100)));
        ft.setText(i++, j, ClientStringFormatter.formatNumber(r.nextInt(100)));

        return fp;
    }

}
