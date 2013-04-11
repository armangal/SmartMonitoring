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
package com.smexec.monitor.client;

import java.util.ArrayList;
import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.smexec.monitor.client.alerts.AlertsWidget;
import com.smexec.monitor.client.login.LoginWidget;
import com.smexec.monitor.client.login.LoginWidget.LoggedInCallBack;
import com.smexec.monitor.client.servers.ServersWidget;
import com.smexec.monitor.client.smartpool.ThreadPoolsWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.config.ClientConfigurations;

public abstract class AbstractEntryPoint<CS extends ConnectedServer, FR extends AbstractFullRefreshResult<CS>>
    implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final MonitoringServiceAsync<CS, FR> service;
    private final LoginWidget<CS, FR> loginWidget;

    private LinkedList<IMonitoringWidget<CS, FR>> widgets = new LinkedList<IMonitoringWidget<CS, FR>>();
    final FlowPanel mainPanel = new FlowPanel();

    private ClientConfigurations clientConfigurations;

    final Button refreshBtn = new Button("Stop Refresh");
    boolean refresh = true;

    private FlowPanel mainHeader = new FlowPanel();
    private HTML mainHeaderLabel = new HTML("--------------------------");
    private HTML footer = new HTML("<ul><li><img title=\"Feedback\" src=\"//1.www.s81c.com/i/v17/opinionlab/oo_icon.gif\"><span>&nbsp;&nbsp;Created by </span><a target='blank' href=\"https://twitter.com/armangal\">@armangal</a><span>, based on </span><a href='https://github.com/armangal/SmartMonitoring' target='blank'>SmartMonitoring</a><span> project.</span></span></li></ul>");

    private AlertsWidget<CS, FR> alertsWidget;

    private RepeatingCommand refreshCommand = new RepeatingCommand() {

        @Override
        public boolean execute() {
            if (refresh) {
                refresh();
            }
            Log.debug("Reschedule refresh?:" + refresh);
            return refresh;
        }
    };

    private AsyncCallback<FR> refreshCallback = new AsyncCallback<FR>() {

        @Override
        public void onSuccess(FR fullResult) {
            if (fullResult == null) {
                Log.debug("Received NULL response.");
                return;
            }

            ArrayList<CS> servers = fullResult.getServers();
            if (servers != null && !servers.isEmpty()) {
                Log.debug("Received FULL refresh response.");

                for (IMonitoringWidget<CS, FR> widget : widgets) {
                    widget.update(fullResult);
                }

            } else {
                Log.debug("Received EMPTY response.");
                for (IMonitoringWidget<CS, FR> widget : widgets) {
                    widget.clear(fullResult);
                }

            }

            mainHeaderLabel.setHTML("<h1>" + clientConfigurations.getTitle() + ", v:" + clientConfigurations.getVersion() + " (" + fullResult.getServerTime()
                                    + ")</h1>");
            mainHeader.add(mainHeaderLabel);

        }

        @Override
        public void onFailure(Throwable caught) {
            Log.error("Received refresh response error:" + caught.getMessage());
            Window.setTitle("Error:" + caught.getMessage());
            refresh = false;
            RootPanel.get().clear();
            RootPanel.get().add(loginWidget);
        }
    };

    public AbstractEntryPoint(MonitoringServiceAsync<CS, FR> service) {
        this.service = service;
        this.loginWidget = new LoginWidget<CS, FR>(service);
        this.loginWidget.registerCallBack(new LoggedInCallBack() {

            @Override
            public void loggedIn(ClientConfigurations cc) {
                clientConfigurations = cc;
                Log.debug("Authenticated");
                RootPanel.get().clear();
                addMainWidgets();
                RootPanel.get().add(mainPanel);
                footer.setStyleName("footer");
                RootPanel.get().add(footer);

                refresh = true;
                refresh();
                Scheduler.get().scheduleFixedDelay(refreshCommand, 20000);
                mainHeaderLabel.setHTML("<h1>" + clientConfigurations.getTitle() + ", v:" + clientConfigurations.getVersion() + "</h1>");

            }
        });

        refreshBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String attribute = refreshBtn.getElement().getAttribute("state");
                if ("1".equals(attribute)) {
                    refreshBtn.getElement().setAttribute("state", "2");
                    refresh = false;
                    refreshBtn.setText("Start Refresh");
                } else {
                    refreshBtn.getElement().setAttribute("state", "1");
                    refresh = true;
                    refresh();
                    Scheduler.get().scheduleFixedDelay(refreshCommand, 20000);
                    refreshBtn.setText("Stop Refresh");
                }

            }
        });

        mainPanel.setStyleName("mainPanel");
        mainHeader.setStyleName("mainHeader");
        mainHeader.add(refreshBtn);
        mainPanel.add(mainHeader);

        registerWidgets();

        Log.debug("AbstractEntryPoint created.");
    }

    /**
     * Add widget to the screen, the order matters.
     * 
     * @param widget
     */
    @SuppressWarnings("unchecked")
    public void addMonitoringWidget(IMonitoringWidget<?, FR> widget) {
        widgets.add((IMonitoringWidget<CS, FR>) widget);
    }

    /**
     * might be overridden to call other refresh function
     */
    public void refresh() {

        Log.debug("Send refresh request.");

        service.refresh(alertsWidget.getLastAlertId(), refreshCallback);
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        Log.debug("Starting monitoring client.");

        Window.setMargin("0px");
        RootPanel.get().clear();
        RootPanel.get().add(loginWidget);

        refreshBtn.getElement().setAttribute("state", "1");
    }

    /**
     * should be overridden by extension project, the order is matters
     */
    public void registerWidgets() {
        addMonitoringWidget(new ThreadPoolsWidget<CS, FR>());
        addMonitoringWidget(new ServersWidget<CS, FR>(service));
        alertsWidget = new AlertsWidget<CS, FR>();
        addMonitoringWidget(alertsWidget);
    }

    private void addMainWidgets() {
        for (IMonitoringWidget<CS, FR> widget : widgets) {
            mainPanel.add(widget);
        }
    }

    public MonitoringServiceAsync<CS, FR> getService() {
        return service;
    }

    public AsyncCallback<FR> getRefreshCallback() {
        return refreshCallback;
    }
}
