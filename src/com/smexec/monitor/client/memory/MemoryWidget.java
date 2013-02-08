package com.smexec.monitor.client.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.shared.ConnectedServer;

public class MemoryWidget
    extends AbstractMonitoringWidget {

    private NumberFormat formatLong = NumberFormat.getDecimalFormat();

    public MemoryWidget() {

        super("Memory");
        setStyleName("memoryWidget");
    }

    public void update(ArrayList<ConnectedServer> list) {
        getScrollPanel().clear();
        FlexTable ft = new FlexTable();
        ft.getElement().setId("infoTable");
        getScrollPanel().add(ft);
        ft.setCellPadding(0);
        ft.setCellSpacing(0);

        Collections.sort(list, new Comparator<ConnectedServer>() {

            @Override
            public int compare(ConnectedServer o1, ConnectedServer o2) {
                if (o1.getStatus() && o2.getStatus()) {
                    return (int) (o2.getMemoryUsage().getPercentage() - o1.getMemoryUsage().getPercentage());
                }
                return 0;
            }
        });

        int i = 0;
        ft.setText(i, 0, "Server Name: Code");
        ft.setText(i, 1, "Used Memory");
        ft.setText(i, 2, "Max");
        ft.setText(i, 3, "%%");
        ft.getRowFormatter().getElement(i++).setId("th");

        for (ConnectedServer cs : list) {
            if (cs.getStatus()) {
                ft.setText(i, 0, cs.getName() + ":" + cs.getServerCode());
                ft.setText(i, 1, formatLong.format(cs.getMemoryUsage().getUsed() / 1048576) + " MB");
                ft.setText(i, 2, formatLong.format(cs.getMemoryUsage().getMax() / 1048576) + " MB");
                ft.setText(i++, 3, formatLong.format(cs.getMemoryUsage().getPercentage()) + "%");

                if (cs.getMemoryUsage().getPercentage() > 90) {
                    ft.getRowFormatter().getElement(i - 1).setId("memoryVeryHigh");
                } else if (cs.getMemoryUsage().getPercentage() > 80) {
                    ft.getRowFormatter().getElement(i - 1).setId("memoryHigh");
                } else if (cs.getMemoryUsage().getPercentage() > 70) {
                    ft.getRowFormatter().getElement(i - 1).setId("memoryWarn");
                }
            }
        }

    }
}
