package com.smexec.monitor.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class AbstractMonitoringWidget
    extends Composite {

    private FlowPanel mainPanel = new FlowPanel();

    private ScrollPanel sp = new ScrollPanel();

    public AbstractMonitoringWidget(String name) {
        FlowPanel mp = new FlowPanel();
        mainPanel.setStyleName("monitoringWidget");
        mainPanel.addStyleName("table");
        HTML lable = new HTML(name);
        lable.addStyleName("monitoringWidgetLable");
        mp.add(lable);
        mp.add(mainPanel);
        mainPanel.add(sp);
        initWidget(mp);
        sp.setHeight("100%");
        sp.setWidth("100%");
    }

    public ScrollPanel getScrollPanel() {
        return sp;
    }

    public FlowPanel getMainPanel() {
        return mainPanel;
    }
}
