package com.smexec.monitor.client.widgets;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractMonitoringWidget
    extends Composite {

    private FlowPanel mainPanel = new FlowPanel();

    private ScrollPanel sp = new ScrollPanel();

    private ClickHandler zoom = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            String styles = mainPanel.getStyleName();
            if (styles.contains("fullSize")) {
                mainPanel.removeStyleName("fullSize");
            } else {
                mainPanel.addStyleName("fullSize");
            }
        }
    };

    private MouseOverHandler handCursor = new MouseOverHandler() {

        @Override
        public void onMouseOver(MouseOverEvent event) {
            ((Widget) event.getSource()).getElement().getStyle().setCursor(Cursor.POINTER);
        }
    };

    public AbstractMonitoringWidget(String name) {
        mainPanel.setStyleName("monitoringWidget");
        mainPanel.addStyleName("table");

        FlowPanel header = new FlowPanel();
        header.setStyleName("header");
        Image img = new Image();
        img.setUrl("img/header-icon.png");
        img.addClickHandler(zoom);
        img.addMouseOverHandler(handCursor);

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
