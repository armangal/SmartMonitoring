package com.smexec.monitor.client.alerts;

import java.util.LinkedList;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;

public class AlertsWidget<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends AbstractMonitoringWidget
    implements IMonitoringWidget<CS, R, FR> {

    private ScrollPanel sp = new ScrollPanel();
    private FlexTable alertsTable = new FlexTable();

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

        LinkedList<Alert> alerts = fullRefreshResult.getAlerts();
        for (Alert a : alerts) {
            int insertRow = alertsTable.insertRow(1);
            alertsTable.setText(insertRow, 0, "" + a.getId());
            HTML msg = new HTML(a.getMessage());
            msg.setTitle(a.getDetails());
            alertsTable.setWidget(insertRow, 1, msg);
            alertsTable.setText(insertRow, 2, "" + a.getServerCode());
            alertsTable.setText(insertRow, 3, a.getAlertTime().toString());
        }

    }

    @Override
    public void clear(FR result) {

    }
}
