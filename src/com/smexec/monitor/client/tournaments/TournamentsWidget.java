package com.smexec.monitor.client.tournaments;

import java.util.Date;
import java.util.Random;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;

public class TournamentsWidget
    extends AbstractMonitoringWidget {

    private FlowPanel fp = new FlowPanel();
    private DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);

    public TournamentsWidget() {
        super("Tournaments");
        setStyleName("tournamentsWidget");

        getScrollPanel().add(fp);
    }

    public void update() {
        fp.clear();
        FlowPanel left = getLeft();
        FlowPanel right = getRight();
        fp.add(left);
        fp.add(right);

    }

    private FlowPanel getLeft() {
        FlowPanel fp = new FlowPanel();
        fp.setStyleName("left");
        FlexTable ft = new FlexTable();
        ft.getElement().setId("infoTable");
        fp.add(ft);
        Random r = new Random();

        int i = 0, j = 0;
        ft.setText(i, j++, "Status");
        ft.setText(i, j++, "Amount");
        ft.setText(i, j++, "Players");
        ft.getRowFormatter().getElement(i).setId("th");

        i++;
        ft.setText(i, 0, "Registering:");
        ft.setText(i, 1, ClientStringFormatter.formatNumber(r.nextInt(888)));
        ft.setText(i++, 2, ClientStringFormatter.formatNumber(r.nextInt(5000)));

        ft.setText(i, 0, "Started:");
        ft.setText(i, 1, ClientStringFormatter.formatNumber(r.nextInt(333)));
        ft.setText(i++, 2, ClientStringFormatter.formatNumber(r.nextInt(5000)));

        ft.setText(i, 0, "Interrupted (12h):");
        ft.setText(i, 1, ClientStringFormatter.formatNumber(r.nextInt(33)));
        ft.setText(i++, 2, ClientStringFormatter.formatNumber(r.nextInt(5000)));
        return fp;
    }

    private FlowPanel getRight() {
        FlowPanel fp = new FlowPanel();
        fp.setStyleName("right");
        FlexTable ft = new FlexTable();
        ft.getElement().setId("infoTable");
        fp.add(ft);
        int i = 0, j = 0;
        ft.setText(i, j++, "Tr.Code");
        ft.setText(i, j++, "Name");
        ft.setText(i, j++, "Time");
        ft.setText(i, j++, "Players");
        ft.getRowFormatter().getElement(i).setId("th");

        Random r = new Random();
        for (i = 1; i < 50; i++) {
            ft.setText(i, 0, "" + r.nextInt(1223343));
            ft.setText(i, 1, "Interrupted" + i);
            ft.setText(i, 2, format.format(new Date()));
            ft.setText(i++, 3, ClientStringFormatter.formatNumber(r.nextInt(5000)));
        }
        return fp;
    }
}
