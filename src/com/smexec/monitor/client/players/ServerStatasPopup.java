package com.smexec.monitor.client.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.smexec.monitor.client.widgets.LineType;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.ChannelChunkStats;
import com.smexec.monitor.shared.ChannelSeverStats;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.ConnectedServer;

public class ServerStatasPopup
    extends DialogBox {

    private FlowPanel fp = new FlowPanel();

    private ConnectedServer cs;

    public ServerStatasPopup(ConnectedServer cs) {
        this.cs = cs;
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        setModal(true);
        setSize("760px", "450px");

        fp.add(new HTML("<h1>Server:" + cs.getServerCode() + ", " + cs.getName()+"</h1>"));

        setWidget(fp);
    }

    @Override
    public void center() {
        super.center();
        ChannelSeverStats css = cs.getChannelSeverStats();
        ArrayList<ChannelChunkStats> values = new ArrayList<ChannelChunkStats>(css.getChannelStats());
        Collections.sort(values, new Comparator<ChannelChunkStats>() {

            @Override
            public int compare(ChannelChunkStats o1, ChannelChunkStats o2) {
                return o1.getStartTime() - o2.getStartTime();
            }
        });
        updateOnlinePlayers(values);
        updatedropsAndConnections(values);

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

        Log.debug("ServerStatasPopup.Updating drops, values size:" + dropsAndConnections.getValuesLenght());
        MonitoringLineChart reconnected = new MonitoringLineChart(new LineType[] {LineType.DROPPED, LineType.OPENED}, "Players", "Time", "Drops vs. New");
        fp.add(reconnected);
        reconnected.updateChart(dropsAndConnections, true);
    }

    private void updateOnlinePlayers(ArrayList<ChannelChunkStats> values) {
        ChartFeed online = new ChartFeed(values.size(), 3);
        for (int k = 0; k < 3; k++) {
            for (int j = 0; j < values.size(); j++) {
                if (k == 0) {
                    // connected
                    online.getValues()[k][j] = values.get(j).getOpenBinarySessions() + values.get(j).getOpenStringSessions();
                } else if (k == 1) {
                    // playing
                    online.getValues()[k][j] = values.get(j).getPlaying();
                } else if (k == 2) {
                    online.getValues()[k][j] = values.get(j).getStartTimeForChart();
                }
            }
        }
        Log.debug("ServerStatasPopup.Updating connected, values size:" + online.getValuesLenght());
        MonitoringLineChart connected = new MonitoringLineChart(new LineType[] {LineType.CONNECTED, LineType.PLAYING}, "Players", "Time", "Online Players");
        fp.add(connected);
        connected.updateChart(online, true);
    }
}
