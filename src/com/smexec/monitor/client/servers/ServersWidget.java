package com.smexec.monitor.client.servers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.user.client.ui.FlexTable;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.shared.ConnectedServer;

public class ServersWidget
    extends AbstractMonitoringWidget {

    public ServersWidget() {
        super("JMX Servers");
        addStyleName("serevrsWidget");
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
                    return o1.getName().compareTo(o2.getName());
                }
                return o2.getStatus().compareTo(o1.getStatus());
            }
        });

        int i = 0;
        ft.setText(i, 0,"Server Name");
        ft.setText(i, 1, "JMX Address");
        ft.setText(i, 2, "Status");
        ft.getRowFormatter().getElement(i++).setId("th");

        for (ConnectedServer cs : list) {
            ft.setText(i, 0, cs.getName());
            ft.setText(i, 1, cs.getIp() + ":" + cs.getJmxPort());
            ft.setText(i++, 2, (cs.getStatus() ? "Online" : "Offline"));
            if (!cs.getStatus()) {
                ft.getRowFormatter().getElement(i-1).setId("offline");
            }
        }
    }
}
