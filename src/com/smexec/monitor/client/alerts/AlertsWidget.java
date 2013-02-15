package com.smexec.monitor.client.alerts;

import java.util.Date;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;

public class AlertsWidget
    extends AbstractMonitoringWidget {

    private ScrollPanel sp = new ScrollPanel();
    private FlexTable alerts = new FlexTable();

    private int lastMsgId = 0;

    public AlertsWidget() {
        super("Alerts");
        addStyleName("alertsWidget");
        sp.setStyleName("alertsWidgetData");
        getDataPanel().add(sp);
        sp.add(alerts);
        alerts.getElement().setId("infoTable");

        int i = 0;
        alerts.setText(0, i++, "Msg.Id:");
        alerts.setText(0, i++, "Message");
        alerts.setText(0, i++, "Srever");
        alerts.setText(0, i++, "Time");

        alerts.getRowFormatter().getElement(0).setId("th");

    }

    public void update() {
        lastMsgId++;
        int insertRow = alerts.insertRow(1);
        alerts.setText(insertRow, 0, "" + lastMsgId);
        alerts.setText(insertRow, 1, "Message " + lastMsgId);
        alerts.setText(insertRow, 2, "Srever " + lastMsgId);
        alerts.setText(insertRow, 3, new Date().toString());
    }
}
