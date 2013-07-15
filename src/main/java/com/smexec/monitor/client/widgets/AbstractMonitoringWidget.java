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
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.AbstractRefreshRequest;
import com.smexec.monitor.client.AbstractRefreshResponse;
import com.smexec.monitor.client.BasicMonitoringRefreshServiceAsync;

public abstract class AbstractMonitoringWidget<RQ extends AbstractRefreshRequest, RS extends AbstractRefreshResponse, SR extends BasicMonitoringRefreshServiceAsync<RQ, RS>>
    extends Composite {

    private SR service;

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
     * refresh progress indicator, it's up to developer to decide if to use it.
     */
    private ProgressLabel refProg = new ProgressLabel();

    /**
     * widget name
     */
    private String name;
    /**
     * indicates if to send refresh command.
     */
    private boolean refresh = false;

    public void forceRefresh() {
        try {
            if (isRefresh()) {
                refProg.progress();
                service.refresh(createRefreshRequest(), new AsyncCallback<RS>() {

                    public void onSuccess(RS result) {
                        refresh(result);
                    };

                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error("Refresh server call failed:" + caught.getMessage(), caught);
                    }
                });
            }
        } catch (Exception e) {
            Log.error("Refresh failed:" + e.getMessage(), e);
        }
        Log.debug("Reschedule widget refresh" + this.getClass().getName() + " :" + isRefresh());

    }

    private RepeatingCommand refreshCommand = new RepeatingCommand() {

        @Override
        public boolean execute() {
            forceRefresh();
            return true;
        }

    };

    public abstract RQ createRefreshRequest();

    public abstract void refresh(RS refershResponse);

    public abstract void refreshFailed(Throwable t);

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

    public SR getService() {
        return service;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        Style style = getElement().getStyle();
        if (getAbsoluteLeft() < 200) {
            style.setFloat(Float.LEFT);
        } else {
            style.setWidth(50, Unit.PCT);
            style.setFloat(Float.RIGHT);
        }
        
    }
    /**
     * @param name - the name of the widget
     * @param refreshDelay - referesh rate in ms.
     */
    public AbstractMonitoringWidget(final String name, final int refreshDelay, SR service) {
        this.name = name;
        this.service = service;
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

            // first time do the request faster then the original one
            Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

                @Override
                public boolean execute() {
                    forceRefresh();
                    Scheduler.get().scheduleFixedDelay(refreshCommand, refreshDelay);
                    return false;
                }
            }, 1);
        }

        setRefresh(true);

    }

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

    public ProgressLabel getRefProg() {
        return refProg;
    }
}
