package com.smexec.monitor.client.tournaments;

import java.util.Date;
import java.util.Random;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;

public class TournamentsWidget
    extends AbstractMonitoringWidget {

    private DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
    FlexTable tournamentStatus = new FlexTable();
    FlexTable interruptedTable = new FlexTable();
    FlowPanel rightPanel = new FlowPanel();
    FlowPanel left = new FlowPanel();

    public TournamentsWidget() {
        super("Tournaments");
        addStyleName("tournamentsWidget");

        createLeft();
        createRight();

        ScrollPanel sp = new ScrollPanel(rightPanel);
        sp.setHeight("100%");
        FlowPanel fp = new FlowPanel();
        fp.add(left);
        fp.add(sp);
        fp.setStyleName("tournamentsWidgetInternal");
        getScrollPanel().add(fp);
    }

    public void update() {

        Random r = new Random();
        int i = 1;
        tournamentStatus.setText(i, 1, ClientStringFormatter.formatNumber(r.nextInt(888)));
        tournamentStatus.setText(i++, 2, ClientStringFormatter.formatNumber(r.nextInt(5000)));

        tournamentStatus.setText(i, 1, ClientStringFormatter.formatNumber(r.nextInt(333)));
        tournamentStatus.setText(i++, 2, ClientStringFormatter.formatNumber(r.nextInt(5000)));

        tournamentStatus.setText(i, 1, ClientStringFormatter.formatNumber(r.nextInt(33)));
        tournamentStatus.setText(i++, 2, ClientStringFormatter.formatNumber(r.nextInt(5000)));

        interruptedTable.removeFromParent();
        interruptedTable = new FlexTable();
        createRight();

        for (i = 1; i < 50; i++) {
            interruptedTable.setText(i, 0, "" + r.nextInt(1223343));
            interruptedTable.setText(i, 1, "Interrupted" + i);
            interruptedTable.setText(i, 2, format.format(new Date()));
            interruptedTable.setText(i++, 3, ClientStringFormatter.formatNumber(r.nextInt(5000)));
        }
    }

    private void createLeft() {
        left.setStyleName("left");
        tournamentStatus.getElement().setId("infoTable");
        left.add(tournamentStatus);

        int i = 0, j = 0;
        tournamentStatus.setText(i, j++, "Status");
        tournamentStatus.setText(i, j++, "Amount");
        tournamentStatus.setText(i, j++, "Players");
        tournamentStatus.getRowFormatter().getElement(i++).setId("th");
        tournamentStatus.setText(i++, 0, "Registering:");
        tournamentStatus.setText(i++, 0, "Started:");
        tournamentStatus.setText(i++, 0, "Interrupted (12h):");
    }

    private void createRight() {
        rightPanel.setStyleName("right");
        interruptedTable.getElement().setId("infoTable");
        rightPanel.add(interruptedTable);

        int i = 0, j = 0;
        interruptedTable.setText(i, j++, "Tr.Code");
        interruptedTable.setText(i, j++, "Name");
        interruptedTable.setText(i, j++, "Time");
        interruptedTable.setText(i, j++, "Players");
        interruptedTable.getRowFormatter().getElement(i).setId("th");

    }
}
