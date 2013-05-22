/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smexec.monitor.client.widgets;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
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
    private FlowPanel header = new FlowPanel();
    private FlowPanel dataPanel = new FlowPanel();
    /**
     * default title widget
     */
    private HTML lable = new HTML();
    /**
     * the custom title widget
     */
    private Widget title;

    /**
     * widget name
     */
    private String name;
    /**
     * indicates if to send refresh command.
     */
    private boolean refresh = false;

    private RepeatingCommand refreshCommand = new RepeatingCommand() {

        @Override
        public boolean execute() {
            if (isRefresh()) {
                refresh();
            }
            Log.debug("Reschedule container refresh?:" + isRefresh());
            return true;
        }

    };

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
        this(name, 0);
    }

    public AbstractMonitoringWidget(final String name, final int refreshDelay) {
        this.name = name;
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

        if (refreshDelay > 0) {
            Scheduler.get().scheduleFixedDelay(refreshCommand, refreshDelay);
        }

    }

    public abstract void refresh();

    public FlowPanel getDataPanel() {
        return dataPanel;
    }

    public void updateTitle(String title) {
        this.lable.setText(title);
    }

    public void setTitleWidget(Widget title) {
        lable.removeFromParent();
        header.add(title);
        this.title = title;
    }

    public Widget getTitleWidget() {
        return this.title;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        Log.debug(refresh ? "Enabling " : "Disabling " + name + " widget refresh mechinism.");
        this.refresh = refresh;
    }

}
