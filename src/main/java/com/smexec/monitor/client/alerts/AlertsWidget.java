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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.alert.Alert;

public class AlertsWidget<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends AbstractMonitoringWidget
    implements IMonitoringWidget<CS, R, FR> {

    private ScrollPanel sp = new ScrollPanel();
    private FlexTable alertsTable = new FlexTable();

    private Map<Integer, Alert> map = new HashMap<Integer, Alert>();

    public AlertsWidget() {
        super("Alerts");
        addStyleName("alertsWidget");
        sp.setStyleName("alertsWidgetData");
        getDataPanel().add(sp);
        sp.add(alertsTable);
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
                if (!map.containsKey(a.getId())) {
                    int insertRow = alertsTable.insertRow(1);
                    alertsTable.setText(insertRow, 0, "" + a.getId());
                    HTML msg = new HTML(a.getMessage() + " [" + a.getServerName() + "]");
                    msg.setTitle(a.toString());
                    alertsTable.setWidget(insertRow, 1, msg);
                    alertsTable.setText(insertRow, 2, "" + a.getServerCode());
                    alertsTable.setText(insertRow, 3, ClientStringFormatter.formatLongToDate(a.getAlertTime()));
                    alertsTable.getRowFormatter().getElement(insertRow).setAttribute("id", "" + a.getId());

                    map.put(a.getId(), a);
                }
            }

            if (alertsTable.getRowCount() > 1000) {
                // clean
                for (int i = alertsTable.getRowCount() - 1; i > 1000; i--) {
                    int id = Integer.valueOf(alertsTable.getRowFormatter().getElement(i).getAttribute("id"));
                    alertsTable.removeRow(i);
                    map.remove(id);
                }
            }
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }

    }

    @Override
    public void clear(FR result) {

    }
}
