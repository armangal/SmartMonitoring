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
package com.smexec.monitor.client.alerts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.client.widgets.ProgressLabel;
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.alert.IAlertType;
import com.smexec.monitor.shared.config.ClientConfigurations;

public class AlertsWidget<CS extends ConnectedServer, FR extends AbstractFullRefreshResult<CS>, CC extends ClientConfigurations>
    extends AbstractMonitoringWidget
    implements IMonitoringWidget<CS, FR> {

    private final MonitoringServiceAsync<CS, FR, CC> service;

    private ScrollPanel sp = new ScrollPanel();
    private FlexTable alertsTable = new FlexTable();
    private HorizontalPanel title = new HorizontalPanel();
    private ListBox typesListBox;
    private ProgressLabel refProg = new ProgressLabel();


    private int lastAlertId = -1;
    private List<IAlertType> alertTypesList = new ArrayList<IAlertType>();
    private LinkedList<Alert> alerts = new LinkedList<Alert>();

    public AlertsWidget(MonitoringServiceAsync<CS, FR, CC> service, IAlertType[]... types) {
        super("Alerts:", 20000);

        this.service = service;

        for (IAlertType[] arr : types) {
            for (IAlertType at : arr) {
                alertTypesList.add(at);
            }
        }

        addStyleName("alertsWidget");
        sp.setStyleName("alertsWidgetData");
        getDataPanel().add(sp);
        sp.add(alertsTable);

        title.setStyleName("serversHeader");
        title.add(new HTML("Alerts:&nbsp;"));
        title.add(new HTML("Filter:"));
        typesListBox = getTypesListBox();
        title.add(typesListBox);
        title.add(getExportButton());
        title.add(refProg);

        setTitleWidget(title);
        initAlertTable();
    }

    private ListBox getTypesListBox() {
        ListBox listBox = new ListBox();
        listBox.addItem("All", "-1");
        for (IAlertType at : alertTypesList) {
            listBox.addItem(at.getName(), "" + at.getId());
        }
        listBox.getElement().getStyle().setFontSize(10, Unit.PX);
        listBox.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                redrawTable();
            }
        });
        return listBox;
    }

    private Button getExportButton() {
        Button export = new Button("Exp.CSV");
        export.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Log.debug("Exporting alerts:" + (GWT.getHostPageBaseURL() + "exp_alerts"));
                Window.open(GWT.getHostPageBaseURL() + "exp_alerts", "Alerts", "");
            }
        });
        export.getElement().getStyle().setPadding(0d, Unit.PX);
        export.setTitle("Export all alerts to CSV file.");
        return export;
    }

    private void initAlertTable() {
        alertsTable.removeAllRows();
        alertsTable.getElement().setId("infoTable");

        int i = 0;
        alertsTable.setText(0, i++, "Msg.Id:");
        alertsTable.setText(0, i++, "Message");
        alertsTable.setText(0, i++, "Server");
        alertsTable.setText(0, i++, "Time");

        alertsTable.getRowFormatter().getElement(0).setId("th");

    }

    @Override
    public void update(FR fullRefreshResult) {
        if (!isRefresh()) {
            setRefresh(true);
            refresh();
        }

    }

    private void redrawTable() {
        Iterator<Alert> it = alerts.iterator();
        initAlertTable();

        int i = 1;
        while (it.hasNext()) {
            Alert a = it.next();
            if (a.getId() > lastAlertId) {
                lastAlertId = a.getId();
            }
            int selInd = typesListBox.getSelectedIndex();
            int val = Integer.valueOf(typesListBox.getValue(selInd));
            if (val == -1 || val == a.getAlertType().getId()) {
                alertsTable.setText(i, 0, "" + a.getId());
                HTML msg = new HTML(a.getMessage() + " [" + a.getServerName() + "]");
                msg.setTitle(a.toString());
                alertsTable.setWidget(i, 1, msg);
                alertsTable.setText(i, 2, "" + a.getServerCode());
                alertsTable.setText(i, 3, a.getAlertTimeStr());
                alertsTable.getRowFormatter().getElement(i).setAttribute("id", "" + a.getId());

                i++;
            }
        }
    }

    @Override
    public void clear(FR result) {
        lastAlertId = -1;
        initAlertTable();
    }

    private int getLastAlertId() {
        return lastAlertId;
    }

    @Override
    public void refresh() {
        refProg.progress();
        service.getAlerts(getLastAlertId(), new AsyncCallback<LinkedList<Alert>>() {

            @Override
            public void onSuccess(LinkedList<Alert> result) {
                refProg.progress();

                try {
                    for (Alert a : result) {
                        if (a.getId() > lastAlertId) {
                            alerts.add(a);
                        }
                    }
                    Collections.sort(alerts, new Comparator<Alert>() {

                        @Override
                        public int compare(Alert o1, Alert o2) {
                            return o2.getId() - o1.getId();
                        }
                    });

                    if (alerts.size() > 1000) {
                        for (int i = 0; i < alerts.size() - 1000; i++) {
                            Alert remove = alerts.remove();
                            Log.debug("Alert widget, removing alert from memory:" + remove);
                        }
                    }

                    redrawTable();

                } catch (Exception e) {
                    refProg.progress();
                    Log.error(e.getMessage(), e);
                }

            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to refresh alerts: " + caught.getMessage(), caught);
            }
        });

    }
}
