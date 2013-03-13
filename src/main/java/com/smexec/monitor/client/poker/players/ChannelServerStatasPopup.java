package com.smexec.monitor.client.poker.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.ILineType;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.GCHistory;
import com.smexec.monitor.shared.poker.ChannelChunkStats;
import com.smexec.monitor.shared.poker.ChannelSeverStats;
import com.smexec.monitor.shared.poker.ConnectedServerPoker;

public class ChannelServerStatasPopup
    extends DialogBox {

    private FlowPanel fp = new FlowPanel();

    private ConnectedServerPoker cs;

    public ChannelServerStatasPopup(ConnectedServerPoker cs) {
        this.cs = cs;
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        setModal(true);
        setSize("760px", "450px");

        fp.add(new HTML("<h1>Server:" + cs.getServerCode() + ", " + cs.getName() + "</h1>"));

        fp.add(new HTML("<h2>Total Connected:" + cs.getChannelSeverStats().getLastChunk().getOpenBinarySessions()
                        + cs.getChannelSeverStats().getLastChunk().getOpenStringSessions() + ", Playing:---" + "</h2>"));

        fp.add(new HTML("<h2>Total Drops:" + cs.getChannelSeverStats().getTotalDrops() + ", Total New Conn.:" + cs.getChannelSeverStats().getTotalConnections()
                        + "</h2>"));

        String gcs = "";
        for (GCHistory gch : cs.getGcHistories()) {
            gcs += ClientStringFormatter.formatMillisShort(gch.getCollectionTime()) + ":(" + gch.getCollectionCount() + ");";
        }
        HTML tech = new HTML("<h2>Up Time:" + ClientStringFormatter.formatMilisecondsToHours(cs.getUpTime()) + ", Memory:"
                             + ClientStringFormatter.formatMBytes(cs.getMemoryUsage().getUsed()) + " of "
                             + ClientStringFormatter.formatMBytes(cs.getMemoryUsage().getMax()) + " MB, Usage:"
                             + ClientStringFormatter.formatMillisShort(cs.getMemoryUsage().getPercentage()) + "%, GC Time:" + gcs + "</h2>");

        fp.add(tech);

        fp.getElement().setId("channelServerPopup");
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

        Log.debug("ChannelServerStatasPopup.Updating drops, values size:" + dropsAndConnections.getValuesLenght());
        MonitoringLineChart reconnected = new MonitoringLineChart(new ILineType[] {PlayersLineType.DROPPED, PlayersLineType.OPENED},
                                                                  "Players",
                                                                  "Time",
                                                                  "Drops vs. New");
        reconnected.setStyleName("reconnectionsChart");
        fp.add(reconnected);
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
        Log.debug("ChannelServerStatasPopup.Updating connected, values size:" + online.getValuesLenght());
        MonitoringLineChart connected = new MonitoringLineChart(new ILineType[] {PlayersLineType.CONNECTED}, "Players", "Time", "Online Players");
        connected.setStyleName("playersChart");
        fp.add(connected);
        connected.updateChart(online, true);
    }
}
