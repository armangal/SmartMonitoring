package com.smexec.monitor.client.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.shared.ConnectedServer;

public class MemoryWidget
    extends AbstractMonitoringWidget {

    public MemoryWidget() {

        super("Memory");
        setStyleName("memoryWidget");
    }

    public void update(ArrayList<ConnectedServer> list) {
        getScrollPanel().clear();
        FlowPanel fp = new FlowPanel();
        getScrollPanel().add(fp);
        Collections.sort(list, new Comparator<ConnectedServer>() {
            @Override
            public int compare(ConnectedServer o1, ConnectedServer o2) {
                if (o1.getStatus() && o2.getStatus()) {
                    return (int) (o2.getMemoryUsage().getPercentage() -o1.getMemoryUsage().getPercentage()); 
                }
                return 0;
            }
        } );
        for (ConnectedServer cs : list) {
            if (cs.getStatus()) {
                fp.add(new HTML(cs.getIp() + ":" + cs.getJmxPort() + " >> [" + cs.getName() + "] [" + cs.getMemoryUsage() + "]"));
            }
        }
    }
}
