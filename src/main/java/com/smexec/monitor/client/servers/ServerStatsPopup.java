/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smexec.monitor.client.servers;

import java.util.HashMap;
import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.DynamicLine;
import com.smexec.monitor.client.widgets.ILineType;
import com.smexec.monitor.client.widgets.MonitoringDynamicLinesChart;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.runtime.CpuUtilizationChunk;
import com.smexec.monitor.shared.runtime.GCHistory;
import com.smexec.monitor.shared.runtime.MemoryState;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.smexec.monitor.shared.runtime.ThreadDump;

public class ServerStatsPopup<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends DialogBox {

    private final MonitoringServiceAsync<CS, R, FR> service;

    private MonitoringLineChart<Double, Long> cpuChart = new MonitoringLineChart<Double, Long>(new ILineType[] {ServersLineType.CPU},
                                                                                               "CPU%",
                                                                                               "Time",
                                                                                               "CPU Load");
    private MonitoringLineChart<Double, Long> sysLoadChart = new MonitoringLineChart<Double, Long>(new ILineType[] {ServersLineType.SYS_LOAD},
                                                                                                   "SysLoadAvg",
                                                                                                   "Time",
                                                                                                   "System Load Average");
    private MonitoringLineChart<Double, Long> memoryChart = new MonitoringLineChart<Double, Long>(new ILineType[] {ServersLineType.MEMORY},
                                                                                                  "Memory%",
                                                                                                  "Time",
                                                                                                  "Memory Usage");

    private MonitoringDynamicLinesChart<Long, Long> memoryDetailsChart = new MonitoringDynamicLinesChart<Long, Long>("M.B.", "Time", "Memory Details");

    private FlowPanel fp = new FlowPanel();
    private FlowPanel cpu = new FlowPanel();
    private FlowPanel memory = new FlowPanel();
    private FlowPanel memoryDetails = new FlowPanel();
    private FlowPanel sysLoad = new FlowPanel();
    private FlowPanel details = new FlowPanel();

    private CS cs;

    private Integer chunks = 100;
    private boolean refresh = true;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ServerStatsPopup(MonitoringServiceAsync service, CS cs) {
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

        fp.getElement().setId("xxx");
        setWidget(fp);

        memoryChart.setStyleName("serverPopupChart");
        memory.add(memoryChart);

        memoryDetailsChart.setStyleName("serverPopupChart");
        memoryDetails.add(memoryDetailsChart);

        cpuChart.setStyleName("serverPopupChart");
        cpu.add(cpuChart);

        sysLoadChart.setStyleName("serverPopupChart");
        sysLoad.add(sysLoadChart);

    }

    private void addRadioButtons(HorizontalPanel hp) {
        RadioButton r30m = getRadioButton("m30", 100);
        r30m.setValue(true);
        hp.add(r30m);
        hp.add(getRadioButton("1h", 200));
        hp.add(getRadioButton("2h", 400));
        hp.add(getRadioButton("6h", 1200));
        hp.add(getRadioButton("12h", 2400));
        hp.add(getRadioButton("1d", 4800));
    }

    private RadioButton getRadioButton(final String name, int chunksToSet) {
        RadioButton r = new RadioButton("chunks", name);
        r.getElement().setAttribute("chunks", "" + chunksToSet);
        r.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Widget w = (Widget) event.getSource();
                chunks = Integer.valueOf(w.getElement().getAttribute("chunks"));
                getMemoryStats(chunks);
                getCpuStats(chunks);
                getExtraData(chunks);
            }
        });
        return r;
    }

    /**
     * for extending popups to add additional elements to the panel
     */
    public void addExtraElements(FlowPanel fp) {

    }

    @Override
    public void center() {
        setSize("760px", "450px");

        super.center();

        HorizontalPanel hp = new HorizontalPanel();
        Button threadDump = new Button("Get Thread Dump");
        hp.add(threadDump);
        addRadioButtons(hp);
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
        Style style = close.getElement().getStyle();
        style.setColor("orange");
        style.setFontWeight(FontWeight.BOLDER);
        fp.add(hp);
        fp.add(cpu);
        fp.add(memory);
        fp.add(memoryDetails);
        fp.add(sysLoad);
        addExtraElements(fp);
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

        getMemoryStats(chunks);
        getCpuStats(chunks);

        service.getRuntimeInfo(cs.getServerCode(), new AsyncCallback<RuntimeInfo>() {

            @Override
            public void onSuccess(RuntimeInfo result) {
                updateRuntimeInfo(result);
                int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
                setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), 26);

            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("error while getting server cpu stats:" + caught.getMessage());

            }
        });

        getExtraData(chunks);

        RepeatingCommand refreshCommand = new RepeatingCommand() {

            @Override
            public boolean execute() {
                if (refresh) {
                    getMemoryStats(chunks);
                    getCpuStats(chunks);
                    getExtraData(chunks);
                    Log.debug("Reschedule refresh");
                }
                return refresh;
            }
        };
        Scheduler.get().scheduleFixedDelay(refreshCommand, 20000);
    }

    public void getExtraData(Integer chunks) {

    }

    @Override
    public void hide() {
        super.hide();
        refresh = false;
    }

    private void getCpuStats(int chunks) {
        service.getCpuUsageHistory(cs.getServerCode(), chunks, new AsyncCallback<LinkedList<CpuUtilizationChunk>>() {

            @Override
            public void onSuccess(LinkedList<CpuUtilizationChunk> result) {
                updateCpuChart(result);
                updateSysLoadChart(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("error while getting server cpu stats:" + caught.getMessage());
            }
        });
    }

    private void getMemoryStats(int chunks) {
        service.getMemoryStats(cs.getServerCode(), chunks, new AsyncCallback<LinkedList<MemoryUsage>>() {

            @Override
            public void onSuccess(LinkedList<MemoryUsage> result) {
                updateMemoryChart(result);
                updateMemoryDetailsChart(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("error while getting server memory stats:" + caught.getMessage());
            };
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
        ChartFeed<Double, Long> cpuHistory = new ChartFeed<Double, Long>(new Double[1][percentList.size()], new Long[percentList.size()]);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < percentList.size(); j++) {
                if (k == 0) {
                    cpuHistory.getValues()[k][j] = percentList.get(j).getUsage();
                } else if (k == 1) {
                    cpuHistory.getXLineValues()[j] = percentList.get(j).getEndTime();
                }
            }
        }

        Log.debug("ServerStatsPopup.Updating CPU, values size:" + cpuHistory.getValuesLenght());
        cpuChart.updateChart(cpuHistory, true);
    }

    private void updateSysLoadChart(LinkedList<CpuUtilizationChunk> percentList) {
        ChartFeed<Double, Long> sysLoadFeed = new ChartFeed<Double, Long>(new Double[1][percentList.size()], new Long[percentList.size()]);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < percentList.size(); j++) {
                if (k == 0) {
                    sysLoadFeed.getValues()[k][j] = percentList.get(j).getSystemLoadAverage();
                } else if (k == 1) {
                    sysLoadFeed.getXLineValues()[j] = percentList.get(j).getEndTime();
                }
            }
        }

        Log.debug("ServerStatsPopup.Updating SysLoad, values size:" + sysLoadFeed.getValuesLenght());
        sysLoadChart.updateChart(sysLoadFeed, true);
    }

    private void updateMemoryChart(LinkedList<MemoryUsage> result) {

        if (result == null) {
            Log.warn("Empty result in memmory stats");
            return;
        }

        ChartFeed<Double, Long> memoryHistory = new ChartFeed<Double, Long>(new Double[1][result.size()], new Long[result.size()]);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < result.size(); j++) {
                if (k == 0) {
                    memoryHistory.getValues()[k][j] = result.get(j).getPercentage();
                } else if (k == 1) {
                    memoryHistory.getXLineValues()[j] = result.get(j).getEndTime();
                }
            }
        }

        Log.debug("ServerStatsPopup.Updating memry, values size:" + memoryHistory.getValuesLenght());
        memoryChart.updateChart(memoryHistory, true);
    }

    private void updateMemoryDetailsChart(LinkedList<MemoryUsage> result) {

        if (result == null) {
            Log.warn("Empty result in memmory stats");
            return;
        }

        String[] colors = new String[] {"blue", "#EC3B83", "#7C0A02", "#568203", "red", "black", "#00FFFF"};
        HashMap<String, DynamicLine> names = new HashMap<String, DynamicLine>(0);
        int i = 0;
        for (MemoryUsage mu : result) {
            for (MemoryState memoryState : mu.getMemoryState()) {
                if (memoryState.isHeap() && !names.containsKey(memoryState.getName())) {
                    names.put(memoryState.getName(), new DynamicLine(i, memoryState.getName(), colors[i], ColumnType.NUMBER));
                    i++;
                }
            }
        }

        ChartFeed<Long, Long> memoryDetailsHistory = new ChartFeed<Long, Long>(new Long[names.size()][result.size()], new Long[result.size()]);
        for (int j = 0; j < result.size(); j++) {
            LinkedList<MemoryState> memoryState = result.get(j).getMemoryState();
            for (MemoryState ms : memoryState) {
                if (names.containsKey(ms.getName())) {
                    memoryDetailsHistory.getValues()[names.get(ms.getName()).getIndex()][j] = ms.getUsed();
                }
            }

            memoryDetailsHistory.getXLineValues()[j] = result.get(j).getEndTime();
        }

        Log.debug("ServerStatsPopup.Updating memry, values size:" + memoryDetailsHistory.getValuesLenght());
        ILineType[] ilt = new DynamicLine[names.values().size()];
        names.values().toArray(ilt);
        memoryDetailsChart.updateChart(ilt, memoryDetailsHistory, true);
    }

    public CS getConnectedServer() {
        return cs;
    }

    @SuppressWarnings("rawtypes")
    public MonitoringServiceAsync getService() {
        return service;
    }
}
