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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;

public class ServersWidget<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends AbstractMonitoringWidget
    implements IMonitoringWidget<CS, R, FR> {

    private static final String SERVERS_SHOW_OFF = "servers.showOff";
    private static final String SERVERS_FILTER = "servers.filter";

    private final MonitoringServiceAsync<CS, R, FR> service;

    private FlowPanel serversList = new FlowPanel();
    private ScrollPanel sp = new ScrollPanel();
    private boolean showOffline = true;

    private ClickHandler getThreadDump = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            String code = ((Widget) event.getSource()).getElement().getAttribute("code");
            CS cs = serversMap.get(Integer.valueOf(code));
            if (cs != null) {
                ServerStatsPopup<CS, R, FR> ssp = new ServerStatsPopup<CS, R, FR>(service, cs);
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
                    GcHistoryPopup ghp = new GcHistoryPopup();
                    ghp.setText(result);
                    ghp.center();
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
    ArrayList<CS> servesList;

    private HorizontalPanel title = new HorizontalPanel();
    private TextBox filter = new TextBox();
    private Label serversLabel = new Label("Servers");

    public ServersWidget(MonitoringServiceAsync<CS, R, FR> service) {
        super("Servers");
        this.service = service;

        addStyleName("serversWidget");
        serversList.setStyleName("serversWidgetInternal");
        getDataPanel().add(serversList);
        sp.setSize("100%", "100%");
        serversList.add(sp);

        title.setStyleName("serversHeader");
        setTitleWidget(title);
        title.add(serversLabel);
        final CheckBox chkShowOffline = new CheckBox("Show Offline");
        chkShowOffline.setValue(true);
        title.add(chkShowOffline);
        title.add(new Label("Filter:"));
        title.add(filter);
        String filterText = Cookies.getCookie(SERVERS_FILTER);
        if (filterText != null) {
            filter.setText(filterText.trim().toLowerCase());
        }

        filter.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent event) {
                Log.debug("Key:" + event.getNativeEvent().getKeyCode());
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    updateServersTable();
                }
            }
        });

        String showOff = Cookies.getCookie(SERVERS_SHOW_OFF);
        if (showOff != null && (showOff.equals("1") || showOff.equals("0"))) {
            chkShowOffline.setValue(showOff.equals("1"));
            showOffline = showOff.equals("1");
        }

        chkShowOffline.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                showOffline = chkShowOffline.getValue();
                Cookies.setCookie(SERVERS_SHOW_OFF, showOffline ? "1" : "0");
            }
        });
    }

    @Override
    public void clear(FR result) {

    }

    private void updateServersTable() {
        sp.clear();
        FlexTable ft = new FlexTable();
        sp.add(ft);
        ft.getElement().setId("infoTable");
        ft.setCellPadding(0);
        ft.setCellSpacing(0);

        Collections.sort(servesList, new Comparator<CS>() {

            @Override
            public int compare(CS o1, CS o2) {
                double o1p = 0, o2p = 0;
                if (o1.getStatus() && o2.getStatus()) {
                    if (o1.getCpuUsage() > 80 || o2.getCpuUsage() > 80) {
                        // sort by CPU
                        o1p = o1.getCpuUsage();
                        o2p = o2.getCpuUsage();
                    } else {
                        // sort by memory
                        o1p = o1.getMemoryUsage().getPercentage();
                        o2p = o2.getMemoryUsage().getPercentage();
                    }
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
        ft.setText(i, j++, "SYSL");
        ft.getRowFormatter().getElement(i++).setId("th");

        int offline = 0;

        serversMap.clear();

        for (CS cs : servesList) {
            serversMap.put(cs.getServerCode(), cs);

            j = 0;
            offline += cs.getStatus() ? 0 : 1;
            if (!toShow(cs.getName())) {
                continue;
            }
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
                usage.addMouseOverHandler(handCursor);
                ft.setWidget(i, j++, usage);

                HTML percent = new HTML(ClientStringFormatter.formatMillisShort(cs.getMemoryUsage().getPercentage()) + "%");
                ft.setWidget(i, j++, percent);

                StringBuilder gcs = new StringBuilder();
                double gcMax = Double.MIN_VALUE;

                // Iterating over all available pools
                for (Double gch : cs.getGcHistories()) {
                    gcs.append(ClientStringFormatter.formatMillisShort(gch)).append(", ");
                    if (gch > gcMax) {
                        gcMax = gch;
                    }
                }
                if (gcs.length() > 0) {
                    gcs.setLength(gcs.length() - 2);
                }
                final HTML memory = new HTML(gcs.toString());
                memory.setTitle("Click to see GC history");
                memory.addMouseOverHandler(handCursor);
                memory.addClickHandler(getGCHistory);
                memory.getElement().setAttribute("code", "" + cs.getServerCode());

                ft.setWidget(i, j++, memory);
                if (gcMax > 10) {
                    Style style = ft.getFlexCellFormatter().getElement(i, j - 1).getStyle();
                    style.setBackgroundColor("#C00000");
                    style.setFontWeight(FontWeight.BOLDER);
                    style.setColor("white");
                }

                if (cs.getMemoryUsage().getPercentage() > 90) {
                    ft.getRowFormatter().getElement(i).setId("memoryVeryHigh");
                }
                // else if (cs.getMemoryUsage().getPercentage() > 80) {
                // ft.getRowFormatter().getElement(i).setId("memoryHigh");
                // }

                HTML cpu = new HTML(cs.getCpuUsage() + "%");
                ft.setWidget(i, j++, cpu);
                if (cs.getCpuUsage() > 90d) {
                    Style style = ft.getFlexCellFormatter().getElement(i, j - 1).getStyle();
                    style.setBackgroundColor("#C00000");
                    style.setFontWeight(FontWeight.BOLDER);
                    style.setColor("white");
                }

                double sla = cs.getSystemLoadAverage();
                HTML sysl = new HTML(sla == -1 ? "N/A" : ClientStringFormatter.formatMillisShort(sla));
                sysl.setTitle("System Load Average");
                ft.setWidget(i, j++, sysl);
                i++;

            } else {
                if (showOffline) {
                    final HTML name = new HTML("<a href=#>" + cs.getServerCode() + ", " + cs.getName() + "</a>");
                    name.setTitle("JMX >> " + cs.getIp() + ":" + cs.getJmxPort());
                    ft.setWidget(i, j++, name);
                    ft.setText(i, j++, "Offline");
                    ft.setText(i, j++, "Offline");
                    ft.setText(i, j++, "Offline");
                    ft.setText(i, j++, "Offline");
                    ft.setText(i, j++, "0.0%");
                    ft.setText(i, j++, "0");
                    ft.getRowFormatter().getElement(i++).setId("offline");
                }
            }
        }

        ft.getColumnFormatter().setWidth(0, "100px");
        serversLabel.setText("Servers:" + servesList.size() + " (" + offline + ")");
    }

    @Override
    public void update(FR fullResult) {
        R result = fullResult.getRefreshResult();

        this.servesList = (ArrayList<CS>) result.getServers();
        Log.debug("ServersWidget spInterrupted:" + sp.getVerticalScrollPosition());

        updateServersTable();

    }

    private boolean toShow(String serverName) {
        filter.setText(filter.getText().trim().toLowerCase().replace(",", " "));
        if (filter.getText().length() > 0) {
            // filter
            Cookies.setCookie(SERVERS_FILTER, filter.getText());
            filter.getElement().getStyle().setBackgroundColor("#66FF00");

            String[] fil = filter.getText().split(" ");
            for (String f : fil) {
                if (f.trim().length() > 0 && serverName.trim().toLowerCase().contains(f)) {
                    return true;
                }
            }

            return false;

        } else {
            filter.getElement().getStyle().setBackgroundColor("white");
            Cookies.removeCookie(SERVERS_FILTER);

            return true;
        }
    }
}
