package com.smexec.monitor.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class AbstractMonitoringWidget
    extends Composite {

    private FlowPanel mainPanel = new FlowPanel();

    private ScrollPanel sp = new ScrollPanel();

    public AbstractMonitoringWidget(String name) {
        mainPanel.setStyleName("monitoringWidget");
        mainPanel.addStyleName("table");

        FlowPanel header = new FlowPanel();
        header.setStyleName("header");
        Image img = new Image();
        img.setUrl("img/header-icon.png");
        header.add(img);
        HTML lable = new HTML(name);
        lable.setStyleName("monitoringWidgetHeaderLable");
        header.add(lable);
        mainPanel.add(header);

        mainPanel.add(sp);
        initWidget(mainPanel);
        sp.setHeight("100%");
        sp.setWidth("100%");
        setStyleName("monitoringWidget");
    }

    public ScrollPanel getScrollPanel() {
        return sp;
    }

    public FlowPanel getMainPanel() {
        return mainPanel;
    }
}
