package com.smexec.monitor.client.servers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
        FlowPanel fp = new FlowPanel();
        getScrollPanel().add(fp);
        Collections.sort(list, new Comparator<ConnectedServer>() {

            @Override
            public int compare(ConnectedServer o1, ConnectedServer o2) {
                if (o1.getStatus() && o2.getStatus()) {
                    return o1.getName().compareTo(o2.getName());
                }
                return o2.getStatus().compareTo(o1.getStatus());
            }
        });
        for (ConnectedServer cs : list) {
            fp.add(new HTML(cs.getIp() + ":" + cs.getJmxPort() + (cs.getStatus() ? " Connected" : " <b>Offline</b>") + " >> [" + cs.getName() + "]"));
        }
    }
}
