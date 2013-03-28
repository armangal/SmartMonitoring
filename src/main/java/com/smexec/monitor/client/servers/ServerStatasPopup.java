package com.smexec.monitor.client.servers;

import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.ILineType;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.GCHistory;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;

public class ServerStatasPopup<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends DialogBox {

    private final MonitoringServiceAsync<CS, R, FR> service;

    private FlowPanel fp = new FlowPanel();
    private FlowPanel cpu = new FlowPanel();
    private FlowPanel memory = new FlowPanel();
    private FlowPanel details = new FlowPanel();

    private CS cs;

    public ServerStatasPopup(MonitoringServiceAsync<CS, R, FR> service, CS cs) {
        this.cs = cs;
        this.service = service;
        setAnimationEnabled(true);
        setModal(true);
        setSize("760px", "450px");
        setGlassEnabled(true);

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
        setSize("760px", "450px");

        super.center();
        setPopupPosition(200, 26);

        HorizontalPanel hp = new HorizontalPanel();
        Button threadDump = new Button("Get Thread Dump");
        hp.add(threadDump);
        Button close = new Button("Close");
        hp.add(close);
        close.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        hp.setWidth("100%");
        hp.setCellHorizontalAlignment(close, HorizontalAlignmentConstant.endOf(Direction.LTR));
        close.getElement().getStyle().setColor("orange");
        fp.add(hp);
        fp.add(cpu);
        fp.add(memory);
        fp.add(details);

        threadDump.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                final ThreadDumpPopup tdp = new ThreadDumpPopup();
                tdp.center();

                service.getThreadDump(cs.getServerCode(), new AsyncCallback<ThreadDump>() {

                    @Override
                    public void onSuccess(ThreadDump result) {
                        tdp.setDump(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        tdp.setText("Can't get thread dump:" + caught.getMessage());
                    }
                });
            }
        });

        service.getMemoryStats(cs.getServerCode(), new AsyncCallback<LinkedList<MemoryUsage>>() {

            @Override
            public void onSuccess(LinkedList<MemoryUsage> result) {
                updateMemoryChart(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("error while getting server memory stats:" + caught.getMessage());
            };
        });
        service.getCpuUsageHistory(cs.getServerCode(), new AsyncCallback<LinkedList<CpuUtilizationChunk>>() {

            @Override
            public void onSuccess(LinkedList<CpuUtilizationChunk> result) {
                updateCpuChart(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("error while getting server cpu stats:" + caught.getMessage());
            }
        });

        service.getRuntimeInfo(cs.getServerCode(), new AsyncCallback<RuntimeInfo>() {

            @Override
            public void onSuccess(RuntimeInfo result) {
                updateRuntimeInfo(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("error while getting server cpu stats:" + caught.getMessage());

            }
        });
    }

    private void updateRuntimeInfo(RuntimeInfo rti) {
        FlexTable ft = new FlexTable();
        ft.getElement().setId("infoTable");
        ft.setCellPadding(0);
        ft.setCellSpacing(0);
        ft.setText(0, 0, "Name/Value");
        ft.setText(0, 1, "Value");

        int i = 1;
        ft.setText(i++, 0, "Name");
        ft.setText(i++, 0, "BootClassPath");
        ft.setText(i++, 0, "ClassPath");
        ft.setText(i++, 0, "LibraryPath");
        ft.setText(i++, 0, "AvailableProcessors");
        ft.setText(i++, 0, "SystemLoadAverage");
        ft.setText(i++, 0, "InputArguments");
        ft.setText(i++, 0, "SystemProperties");

        ft.getRowFormatter().getElement(0).setId("th");

        i = 1;
        ft.setText(i++, 1, rti.getName());
        createWrappedHTML(ft, i++, rti.getBootClassPath());
        createWrappedHTML(ft, i++, rti.getClassPath());
        createWrappedHTML(ft, i++, rti.getLibraryPath());

        ft.setText(i++, 1, "" + rti.getAvailableProcessors());
        ft.setText(i++, 1, "" + rti.getSystemLoadAverage());

        TextArea ta = new TextArea();
        StringBuilder sb = new StringBuilder();
        for (String p : rti.getInputArguments()) {
            sb.append(p).append("\n");
        }
        ta.setText(sb.toString());
        ta.setSize("700px", "300px");
        ft.setWidget(i++, 1, ta);

        ta = new TextArea();
        sb = new StringBuilder();
        for (String key : rti.getSystemProperties().keySet()) {
            sb.append(key + " = " + rti.getSystemProperties().get(key)).append("\n");
        }
        ta.setText(sb.toString());
        ta.setSize("700px", "300px");
        ft.setWidget(i++, 1, ta);

        details.add(ft);
    }

    private void createWrappedHTML(FlexTable ft, int index, String text) {
        Widget h = getWrappedHtml(text);
        ft.setWidget(index, 1, h);
        ft.getCellFormatter().getElement(index, 1).setId("wrapContent");

    }

    private Widget getWrappedHtml(String text) {
        TextArea ta = new TextArea();
        ta.setText(text);
        ta.setSize("700px", "100px");
        return ta;
    }

    private void updateCpuChart(LinkedList<CpuUtilizationChunk> percentList) {
        ChartFeed cpuHistory = new ChartFeed(percentList.size(), 2);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < percentList.size(); j++) {
                if (k == 0) {
                    cpuHistory.getValues()[k][j] = (long) percentList.get(j).getUsage();
                } else if (k == 1) {
                    cpuHistory.getValues()[k][j] = percentList.get(j).getStartTime();
                }
            }
        }

        Log.debug("ServerStatasPopup.Updating CPU, values size:" + cpuHistory.getValuesLenght());
        MonitoringLineChart cpuChart = new MonitoringLineChart(new ILineType[] {ServersLineType.CPU}, "CPU%", "Time", "CPU Load");
        cpuChart.setStyleName("reconnectionsChart");
        cpu.add(cpuChart);
        cpuChart.updateChart(cpuHistory, true);
    }

    private void updateMemoryChart(LinkedList<MemoryUsage> result) {

        if (result == null) {
            Log.warn("Empty result in memmory stats");
            return;
        }

        ChartFeed memoryHistory = new ChartFeed(result.size(), 2);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < result.size(); j++) {
                if (k == 0) {
                    memoryHistory.getValues()[k][j] = (long) result.get(j).getPercentage();
                } else if (k == 1) {
                    memoryHistory.getValues()[k][j] = result.get(j).getStartTime();
                }
            }
        }
        Log.debug("ServerStatasPopup.Updating memry, values size:" + memoryHistory.getValuesLenght());
        MonitoringLineChart connected = new MonitoringLineChart(new ILineType[] {ServersLineType.MEMORY}, "Memory%", "Time", "Memory Usage");
        connected.setStyleName("playersChart");
        memory.add(connected);
        connected.updateChart(memoryHistory, true);
    }
}
