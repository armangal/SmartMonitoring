package com.smexec.monitor.client.servers;

import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.MonitoringService;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.ILineType;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.GCHistory;

public class ServerStatasPopup
    extends DialogBox {

    private final MonitoringServiceAsync service = GWT.create(MonitoringService.class);

    private FlowPanel fp = new FlowPanel();

    private ConnectedServer cs;

    public ServerStatasPopup(ConnectedServer cs) {
        this.cs = cs;
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        setModal(true);
        setSize("760px", "450px");

        fp.add(new HTML("<h1>Server:" + cs.getServerCode() + ", " + cs.getName() + "</h1>"));

        fp.add(new HTML("<h2>Up Time:" + ClientStringFormatter.formatMilisecondsToHours(cs.getUpTime()) + "</h2>"));

        String gcs = "";
        for (GCHistory gch : cs.getGcHistories()) {
            gcs += ClientStringFormatter.formatMillisShort(gch.getCollectionTime()) + ":(" + gch.getCollectionCount() + ");";
        }
        HTML tech = new HTML("<h2>Memory:" + ClientStringFormatter.formatMBytes(cs.getMemoryUsage().getUsed()) + " of "
                             + ClientStringFormatter.formatMBytes(cs.getMemoryUsage().getMax()) + " MB, Usage:"
                             + ClientStringFormatter.formatMillisShort(cs.getMemoryUsage().getPercentage()) + "%, GC Time:" + gcs + "</h2>");

        fp.add(tech);

        fp.getElement().setId("channelServerPopup");
        setWidget(fp);
    }

    @Override
    public void center() {
        super.center();
        updateMemoryChart();
        updateCpuChart();
        Button threadDump = new Button("Get Thread Dump");
        fp.add(threadDump);

        threadDump.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                service.getThreadDump(cs.getServerCode(), new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        DialogBox db = new DialogBox();
                        db.setAnimationEnabled(true);
                        db.setAutoHideEnabled(true);
                        db.setModal(true);
                        db.setSize("900px", "600px");
                        TextArea textArea = new TextArea();
                        textArea.setText(result);
                        textArea.setSize("990px", "590px");
                        db.setWidget(textArea);
                        db.center();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Can't get thread dump:" + caught.getMessage());
                    }
                });
            }
        });

    }

    private void updateCpuChart() {
        LinkedList<Double> percentList = cs.getCpuUtilization().getPercentList();
        ChartFeed cpuHistory = new ChartFeed(percentList.size(), 2);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < percentList.size(); j++) {
                if (k == 0) {
                    cpuHistory.getValues()[k][j] = percentList.get(j).longValue();
                } else if (k == 1) {
                    cpuHistory.getValues()[k][j] = j;
                }
            }
        }

        Log.debug("ServerStatasPopup.Updating CPU, values size:" + cpuHistory.getValuesLenght());
        MonitoringLineChart cpuChart = new MonitoringLineChart(new ILineType[] {ServersLineType.CPU}, "CPU", "Time", "CPU Load");
        cpuChart.setStyleName("reconnectionsChart");
        fp.add(cpuChart);
        cpuChart.updateChart(cpuHistory, true);
    }

    private void updateMemoryChart() {
        ChartFeed memoryHistory = new ChartFeed(1, 2);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < 1; j++) {
                if (k == 0) {
                    memoryHistory.getValues()[k][j] = 5;
                } else if (k == 1) {
                    memoryHistory.getValues()[k][j] = 1;
                }
            }
        }
        Log.debug("ServerStatasPopup.Updating memry, values size:" + memoryHistory.getValuesLenght());
        MonitoringLineChart connected = new MonitoringLineChart(new ILineType[] {ServersLineType.MEMORY}, "Memory", "Time", "Memory Usage");
        connected.setStyleName("playersChart");
        fp.add(connected);
        connected.updateChart(memoryHistory, true);
    }
}
