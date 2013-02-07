package com.smexec.monitor.client.servers;

import java.util.ArrayList;

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
        for (ConnectedServer cs : list) {
            fp.add(new HTML(cs.getIp() + ":" + cs.getJmxPort() + (cs.getStatus() ? " Connected" : " <b>Offline</b>") + " >> [" + cs.getName() + "]"));
        }
    }
}
