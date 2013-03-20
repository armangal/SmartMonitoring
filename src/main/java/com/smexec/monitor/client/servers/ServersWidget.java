package com.smexec.monitor.client.servers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.GCHistory;

public class ServersWidget<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends AbstractMonitoringWidget
    implements IMonitoringWidget<CS, R, FR> {

    private final MonitoringServiceAsync<CS, R, FR> service;

    private FlowPanel serversList = new FlowPanel();
    private ScrollPanel sp = new ScrollPanel();

    private ClickHandler getThreadDump = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            String code = ((Widget) event.getSource()).getElement().getAttribute("code");
            CS cs = serversMap.get(Integer.valueOf(code));
            if (cs != null) {
                ServerStatasPopup<CS, R, FR> ssp = new ServerStatasPopup<CS, R, FR>(service, cs);
                ssp.center();
            } else {
                Window.alert("Server couldn't be found:" + code);
            }

        }
    };

    private ClickHandler getGCHistory = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            String code = ((Widget) event.getSource()).getElement().getAttribute("code");
            service.getGCHistory(Integer.valueOf(code), new AsyncCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    DialogBox db = new DialogBox();
                    db.setAnimationEnabled(true);
                    db.setAutoHideEnabled(true);
                    db.setModal(true);
                    db.setSize("600px", "600px");
                    TextArea textArea = new TextArea();
                    textArea.setText(result);
                    textArea.setSize("590px", "590px");
                    db.setWidget(textArea);
                    db.center();
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("Can't get GC history:" + caught.getMessage());
                }
            });
        }
    };

    private MouseOverHandler handCursor = new MouseOverHandler() {

        @Override
        public void onMouseOver(MouseOverEvent event) {
            ((Widget) event.getSource()).getElement().getStyle().setCursor(Cursor.POINTER);
        }
    };

    private Map<Integer, CS> serversMap = new HashMap<Integer, CS>(0);

    public ServersWidget(MonitoringServiceAsync<CS, R, FR> service) {
        super("Connected Servers");
        this.service = service;

        addStyleName("serversWidget");
        serversList.setStyleName("serversWidgetInternal");
        getDataPanel().add(serversList);
        sp.setSize("100%", "100%");
        serversList.add(sp);
    }

    @Override
    public void clear(FR result) {

    }

    @Override
    public void update(FR fullResult) {
        R result = fullResult.getRefreshResult();

        ArrayList<CS> list = (ArrayList<CS>) result.getServers();
        Log.debug("ServersWidget spInterrupted:" + sp.getVerticalScrollPosition());
        serversMap.clear();

        sp.clear();

        FlexTable ft = new FlexTable();
        sp.add(ft);
        ft.getElement().setId("infoTable");
        ft.setCellPadding(0);
        ft.setCellSpacing(0);

        Collections.sort(list, new Comparator<CS>() {

            @Override
            public int compare(CS o1, CS o2) {
                double o1p = 0, o2p = 0;
                if (o1.getStatus() && o2.getStatus()) {
                    o1p = o1.getMemoryUsage().getPercentage();
                    o2p = o2.getMemoryUsage().getPercentage();
                } else if (o1.getStatus()) {
                    o1p = o1.getMemoryUsage().getPercentage();
                    o2p = 100;
                } else if (o2.getStatus()) {
                    o1p = 100;
                    o2p = o2.getMemoryUsage().getPercentage();
                }
                return (int) (o2p * 100d - o1p * 100d);
            }
        });

        int i = 0, j = 0;
        ft.setText(i, j++, "Code, Name");
        ft.setText(i, j++, "Up Time");
        ft.setText(i, j++, "Memory");
        ft.setText(i, j++, "Usage %");
        ft.setText(i, j++, "GC Avg Time");
        ft.setText(i, j++, "CPU%");
        ft.getRowFormatter().getElement(i++).setId("th");

        for (CS cs : list) {
            serversMap.put(cs.getServerCode(), cs);

            j = 0;
            if (cs.getStatus()) {
                final HTML name = new HTML("<a href=#>" + cs.getServerCode() + "," + cs.getName() + "</a>");
                name.getElement().setAttribute("code", "" + cs.getServerCode());
                name.setTitle("JMX >> " + cs.getIp() + ":" + cs.getJmxPort() + "\nClick for more info");
                name.addMouseOverHandler(handCursor);

                name.addClickHandler(getThreadDump);

                ft.setWidget(i, j++, name);
                ft.setText(i, j++, ClientStringFormatter.formatMilisecondsToHours(cs.getUpTime()));

                HTML usage = new HTML(ClientStringFormatter.formatMBytes(cs.getMemoryUsage().getUsed()) + " of "
                                      + ClientStringFormatter.formatMBytes(cs.getMemoryUsage().getMax()) + " MB");
                usage.setTitle(cs.getMemoryUsage().getMemoryState());
                usage.addMouseOverHandler(handCursor);
                ft.setWidget(i, j++, usage);

                HTML percent = new HTML(ClientStringFormatter.formatMillisShort(cs.getMemoryUsage().getPercentage()) + "%");
                percent.setTitle(cs.getMemoryUsage().getMemoryState());
                ft.setWidget(i, j++, percent);

                String gcs = "";
                double max = Double.MIN_VALUE;

                // Iterating over all available pools
                for (GCHistory gch : cs.getGcHistories()) {
                    if (gch.getLastColleactionTime() > 0 && gch.getCollectionCount() > 0) {
                        double time = gch.getLastColleactionTime() / 1000d;
                        gcs += ClientStringFormatter.formatMillisShort(time) + ", ";
                        if (time > max) {
                            max = time;
                        }
                    }
                }
                final HTML memory = new HTML(gcs);
                memory.setTitle("Click to see GC history");
                memory.addMouseOverHandler(handCursor);
                memory.addClickHandler(getGCHistory);
                memory.getElement().setAttribute("code", "" + cs.getServerCode());

                ft.setWidget(i, j++, memory);
                if (max > 5) {
                    Style style = ft.getFlexCellFormatter().getElement(i, j - 1).getStyle();
                    style.setBackgroundColor("#C00000");
                    style.setFontWeight(FontWeight.BOLDER);
                    style.setColor("white");
                }

                if (cs.getMemoryUsage().getPercentage() > 90) {
                    ft.getRowFormatter().getElement(i).setId("memoryVeryHigh");
                } else if (cs.getMemoryUsage().getPercentage() > 80) {
                    ft.getRowFormatter().getElement(i).setId("memoryHigh");
                } else if (cs.getMemoryUsage().getPercentage() > 70) {
                    ft.getRowFormatter().getElement(i).setId("memoryWarn");
                }

                HTML cpu = new HTML(cs.getCpuUtilization() + "%");
                ft.setWidget(i++, j++, cpu);

            } else {
                final HTML name = new HTML("<a href=#>" + cs.getServerCode() + ", " + cs.getName() + "</a>");
                name.setTitle("JMX >> " + cs.getIp() + ":" + cs.getJmxPort());
                ft.setWidget(i, j++, name);
                ft.setText(i, j++, "Offline");
                ft.setText(i, j++, "Offline");
                ft.setText(i, j++, "Offline");
                ft.setText(i, j++, "Offline");
                ft.setText(i, j++, "0.0%");
                ft.getRowFormatter().getElement(i++).setId("offline");
            }
        }

        ft.getColumnFormatter().setWidth(0, "100px");

    }
}
