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

import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.alert.Alert;

public class AlertsWidget<CS extends ConnectedServer, FR extends AbstractFullRefreshResult<CS>>
    extends AbstractMonitoringWidget
    implements IMonitoringWidget<CS, FR> {

    private ScrollPanel sp = new ScrollPanel();
    private FlexTable alertsTable = new FlexTable();

    private int lastAlertId = -1;

    public AlertsWidget() {
        super("Alerts");
        addStyleName("alertsWidget");
        sp.setStyleName("alertsWidgetData");
        getDataPanel().add(sp);
        sp.add(alertsTable);

        initAlertTable();
    }

    private void initAlertTable() {
        alertsTable.removeAllRows();
        alertsTable.getElement().setId("infoTable");

        int i = 0;
        alertsTable.setText(0, i++, "Msg.Id:");
        alertsTable.setText(0, i++, "Message");
        alertsTable.setText(0, i++, "Srever");
        alertsTable.setText(0, i++, "Time");

        alertsTable.getRowFormatter().getElement(0).setId("th");

    }

    @Override
    public void update(FR fullRefreshResult) {

        try {
            LinkedList<Alert> alerts = fullRefreshResult.getAlerts();
            for (Alert a : alerts) {
                if (a.getId() > lastAlertId) {
                    int insertRow = alertsTable.insertRow(1);
                    alertsTable.setText(insertRow, 0, "" + a.getId());
                    HTML msg = new HTML(a.getMessage() + " [" + a.getServerName() + "]");
                    msg.setTitle(a.toString());
                    alertsTable.setWidget(insertRow, 1, msg);
                    alertsTable.setText(insertRow, 2, "" + a.getServerCode());
                    alertsTable.setText(insertRow, 3, a.getAlertTimeStr());
                    alertsTable.getRowFormatter().getElement(insertRow).setAttribute("id", "" + a.getId());

                    lastAlertId = a.getId();

                }
            }

            if (alertsTable.getRowCount() > 1000) {
                // clean
                for (int i = alertsTable.getRowCount() - 1; i > 1000; i--) {
                    int id = Integer.valueOf(alertsTable.getRowFormatter().getElement(i).getAttribute("id"));
                    alertsTable.removeRow(i);
                }
            }
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }

    }

    @Override
    public void clear(FR result) {
        lastAlertId = -1;
        initAlertTable();
    }

    public int getLastAlertId() {
        return lastAlertId;
    }
}
