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
package org.clevermore.monitor.client.alerts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.clevermore.monitor.client.AlertsService;
import org.clevermore.monitor.client.AlertsServiceAsync;
import org.clevermore.monitor.client.widgets.AbstractMonitoringWidget;
import org.clevermore.monitor.client.widgets.IMonitoringWidget;
import org.clevermore.monitor.shared.alert.Alert;
import org.clevermore.monitor.shared.alert.AlertType;
import org.clevermore.monitor.shared.alert.IAlertType;
import org.clevermore.monitor.shared.alert.RefreshAlertsRequest;
import org.clevermore.monitor.shared.alert.RefreshAlertsResponse;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Alert Widget is responsible to show the most up to date alerts coming from server. The widget takes care
 * for refreshing the list of last alerts. Only last 1000 are shown to user. <br>
 * Two features are present on the widget:<br>
 * - Filtering on client side by alert type <br>
 * - Exporting all alerts to excel file
 * 
 * @author Arman Gal
 */
public class AlertsWidget
    extends AbstractMonitoringWidget<RefreshAlertsRequest, RefreshAlertsResponse, AlertsServiceAsync>
    implements IMonitoringWidget {

    /**
     * actual html table that holds last 1000 alerts
     */
    private FlexTable alertsTable = new FlexTable();

    /**
     * listbox with available alert types for filtering
     */
    private ListBox typesListBox;

    /**
     * last alert id received by the widget. it's important to keep this one up-to-date caz it's used in
     * refresh request
     */
    private int lastAlertId = -1;

    /**
     * locally stored last 1000 alerts, we need it in order to filter the alerts locally faster
     */
    private LinkedList<Alert> alerts = new LinkedList<Alert>();

    /**
     * Constructs alert widget with default alert type available for filtering
     */
    public AlertsWidget() {
        this(new IAlertType[] {});
    }

    /**
     * Constructs alert widget with default and additionally provided alert type available for filtering.<br>
     * Usually should be used by projects that have extended the Alerts
     * 
     * @param types - additional alert types
     */
    public AlertsWidget(IAlertType[]... types) {
        super("Alerts:", 20000, (AlertsServiceAsync) GWT.create(AlertsService.class));

        Set<IAlertType> alertTypesSet = new HashSet<IAlertType>();

        // default alert types
        alertTypesSet.addAll(new HashSet<IAlertType>(Arrays.asList(AlertType.values())));

        // all the custom alert types
        for (IAlertType[] arr : types) {
            alertTypesSet.addAll(new HashSet<IAlertType>(Arrays.asList(arr)));
        }

        addStyleName("alertsWidget");
        ScrollPanel sp = new ScrollPanel();
        sp.setStyleName("alertsWidgetData");
        getDataPanel().add(sp);
        sp.add(alertsTable);

        // creating title
        HorizontalPanel title = new HorizontalPanel();
        title.setStyleName("serversHeader");
        title.add(new HTML("Alerts:&nbsp;"));
        title.add(new HTML("Filter:"));
        typesListBox = getTypesListBox(alertTypesSet);
        title.add(typesListBox);
        title.add(getExportButton());
        title.add(getRefProg());

        setTitleWidget(title);

        initAlertTable();
    }

    /**
     * creates the filter listbox from available aret types
     * 
     * @param alertTypesSet
     * @return
     */
    private ListBox getTypesListBox(Set<IAlertType> alertTypesSet) {
        ListBox listBox = new ListBox();
        listBox.addItem("All", "-1");
        for (IAlertType at : alertTypesSet) {
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

    /**
     * creates the export to excel file button
     * 
     * @return
     */
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

    /**
     * Initializes the html table what will hold last alert messages
     */
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

    /**
     * recreates the content of HTML table that holds last alert messages, here the selected filter is
     * considered and messages are filtered
     */
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
    public void clear() {
        lastAlertId = -1;
        initAlertTable();
    }

    @Override
    public RefreshAlertsRequest createRefreshRequest() {
        return new RefreshAlertsRequest(lastAlertId);
    }

    @Override
    public void refreshFailed(Throwable t) {
        Log.error("Failed to refresh alerts: " + t.getMessage(), t);
    }

    @Override
    public void refresh(RefreshAlertsResponse refershResponse) {
        getRefProg().progress();

        try {
            for (Alert a : refershResponse.getAlerts()) {
                // take only alerts with higher ID number than already received
                if (a.getId() > lastAlertId) {
                    alerts.add(a);
                }
            }

            // sort in descending order
            Collections.sort(alerts, new Comparator<Alert>() {

                @Override
                public int compare(Alert o1, Alert o2) {
                    return o2.getId() - o1.getId();
                }
            });

            // shrink the local list to 1000 elements
            while (alerts.size() > 1000) {
                Alert remove = alerts.remove();
                Log.debug("Alert widget, removing alert from memory:" + remove);
            }

            redrawTable();

        } catch (Exception e) {
            getRefProg().progress();
            Log.error(e.getMessage(), e);
        }

    }
}
