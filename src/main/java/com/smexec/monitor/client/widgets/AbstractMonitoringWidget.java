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
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractMonitoringWidget
    extends Composite {

    private FlowPanel mainPanel = new FlowPanel();

    private FlowPanel dataPanel = new FlowPanel();

    private HTML lable = new HTML();

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

        FlowPanel header = new FlowPanel();
        header.setStyleName("header");
        Image img = new Image();
        img.setUrl("img/header-icon.png");
        img.addClickHandler(zoom);
        img.addMouseOverHandler(handCursor);

        header.add(img);
        lable.setText(name);
        lable.setStyleName("lable");
        header.add(lable);
        mainPanel.add(header);

        mainPanel.add(dataPanel);
        dataPanel.setStyleName("data");

        initWidget(mainPanel);
        setStyleName("monitoringWidget");
    }

    public FlowPanel getDataPanel() {
        return dataPanel;
    }

    public void updateTitle(String title) {
        this.lable.setText(title);
    }
}
