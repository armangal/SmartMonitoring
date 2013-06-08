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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.smexec.monitor.client.alerts.AlertsWidget;
import com.smexec.monitor.client.login.LoginWidget;
import com.smexec.monitor.client.login.LoginWidget.LoggedInCallBack;
import com.smexec.monitor.client.servers.ServersWidget;
import com.smexec.monitor.client.settings.SettingsPopup;
import com.smexec.monitor.client.smartpool.ThreadPoolsWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.client.widgets.ProgressLabel;
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.alert.AlertType;
import com.smexec.monitor.shared.config.ClientConfigurations;

public abstract class AbstractEntryPoint<CS extends ConnectedServer, FR extends AbstractFullRefreshResult<CS>, CC extends ClientConfigurations>
    implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final MonitoringServiceAsync<CS, FR, CC> service;
    private final LoginWidget<CS, FR, CC> loginWidget;

    private LinkedList<IMonitoringWidget<CS, FR>> widgets = new LinkedList<IMonitoringWidget<CS, FR>>();
    final FlowPanel mainPanel = new FlowPanel();

    private CC clientConfigurations;

    final Button refreshBtn = new Button("Stop Refresh");
    final Button alertBtn = new Button("Stop Alerts");
    boolean refresh = true;

    private FlowPanel mainHeader = new FlowPanel();
    private HTML mainHeaderLabel = new HTML("--------------------------");
    private ProgressLabel refProg = new ProgressLabel();
    private HTML footer = new HTML("<ul><li><img title=\"Feedback\" src=\"img/oo_icon.gif\"><span>&nbsp;&nbsp;Created by </span><a target='blank' href=\"https://twitter.com/armangal\">@armangal</a><span>, based on </span><a href='https://github.com/armangal/SmartMonitoring' target='blank'>SmartMonitoring</a><span> project.</span></span></li></ul>");

    private AlertsWidget<CS, FR, CC> alertsWidget;

    private RepeatingCommand refreshCommand = new RepeatingCommand() {

        @Override
        public boolean execute() {
            if (refresh) {
                refresh();
            } else {
                Log.debug("skipping refresh");
            }
            return true;
        }
    };

    private RepeatingCommand alertBlick = new RepeatingCommand() {

        @Override
        public boolean execute() {
            if ("2".equals(alertBtn.getElement().getAttribute("state"))) {
                Style style = alertBtn.getElement().getStyle();
                style.setColor(style.getColor().equals("red") ? "blue" : "red");
                return true;
            } else {
                return false;
            }
        }
    };

    private AsyncCallback<FR> refreshCallback = new AsyncCallback<FR>() {

        int errorCount = 0;

        @Override
        public void onSuccess(FR fullResult) {
            errorCount = 0;
            refProg.progress();

            if (fullResult == null) {
                Log.debug("Received NULL response.");
                return;
            }

            Log.debug("Full Result:" + fullResult.toString());

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
            // mainHeader.add(mainHeaderLabel);

        }

        @Override
        public void onFailure(Throwable caught) {
            Log.error("Received refresh response error:" + caught.getMessage() + ",\n errorCount:" + errorCount);
            errorCount++;
            refProg.progress();
            if (errorCount > 3) {
                closeClient();
            }
        }
    };

    private void closeClient() {
        refresh = false;
        RootPanel.get().clear();
        RootPanel.get().add(loginWidget);

        // reset last IDs
        resetLastIDs();
        for (IMonitoringWidget<CS, FR> widget : widgets) {
            widget.clear(null);
            widget.setRefresh(false);
        }
    }

    public void resetLastIDs() {
        alertsWidget.clear(null);
    }

    public AbstractEntryPoint(final MonitoringServiceAsync<CS, FR, CC> service) {
        this.service = service;
        this.loginWidget = new LoginWidget<CS, FR, CC>(service);
        this.loginWidget.registerCallBack(new LoggedInCallBack<CC>() {

            @Override
            public void loggedIn(CC cc) {
                try {
                    clientConfigurations = cc;

                    registerWidgets();

                    Log.debug("Authenticated");
                    RootPanel.get().clear();
                    addMainWidgets();
                    RootPanel.get().add(mainPanel);
                    footer.setStyleName("footer");
                    RootPanel.get().add(footer);

                    refresh = true;
                    refresh();
                    Scheduler.get().scheduleFixedDelay(refreshCommand, GWT.isProdMode() ? 60000 : 20000);

                    alertBtn.getElement().setAttribute("state", cc.isAlertsEnabled() ? "2" : "1");
                    alertButtonClicked(cc.isAlertsEnabled() ? "2" : "1");

                    mainHeaderLabel.setHTML("<h1>" + clientConfigurations.getTitle() + ", v:" + clientConfigurations.getVersion() + "</h1>");

                } catch (Exception e) {
                    Log.error(e.getMessage(), e);
                    Log.error(clientConfigurations.toString());
                }
            }
        });

        mainPanel.setStyleName("mainPanel");

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
        refProg.progress();

        service.refresh(refreshCallback);
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
        alertBtn.getElement().setAttribute("state", "1");

        refreshBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String attribute = refreshBtn.getElement().getAttribute("state");
                if ("1".equals(attribute)) {
                    refreshBtn.getElement().setAttribute("state", "2");
                    refresh = false;
                    refreshBtn.setText("Start Refresh");
                    for (IMonitoringWidget<CS, FR> widget : widgets) {
                        widget.setRefresh(false);
                    }

                } else {
                    refreshBtn.getElement().setAttribute("state", "1");
                    refresh = true;
                    refresh();
                    refreshBtn.setText("Stop Refresh");
                }

            }
        });

        alertBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String attribute = alertBtn.getElement().getAttribute("state");
                alertButtonClicked(attribute);
                service.stopAlerts("2".equals(attribute), new AsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {

                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        mainHeader.setStyleName("mainHeader");
        Style style = refProg.getElement().getStyle();
        style.setWidth(20, Unit.PX);
        style.setFloat(Float.LEFT);
        mainHeader.add(refProg);
        mainHeader.add(refreshBtn);
        mainHeader.add(alertBtn);
        Resources resources = GWT.create(Resources.class);
        Image settings = new Image(resources.settingsSmall());
        settings.addStyleName("settings");
        settings.setTitle("Settings");
        settings.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                SettingsPopup<CS, FR, CC> sp = new SettingsPopup<CS, FR, CC>(service);
                sp.center();
            }
        });
        mainHeader.add(settings);
        Image logout = new Image(resources.logout());
        logout.addStyleName("settings");
        logout.setTitle("Logout");
        logout.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                service.logout(new AsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        closeClient();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        closeClient();
                    }
                });
            }
        });
        mainHeader.add(logout);

        mainHeader.add(mainHeaderLabel);
        mainPanel.add(mainHeader);
    }

    /**
     * should be overridden by extension project, the order is matters
     */
    public void registerWidgets() {
        addMonitoringWidget(new ThreadPoolsWidget<CS, FR>());
        addMonitoringWidget(new ServersWidget<CS, FR, CC>(service));
        alertsWidget = new AlertsWidget<CS, FR, CC>(service, AlertType.values());
        addMonitoringWidget(alertsWidget);
    }

    private void addMainWidgets() {
        for (IMonitoringWidget<CS, FR> widget : widgets) {
            mainPanel.add(widget);
        }
    }

    public MonitoringServiceAsync<CS, FR, CC> getService() {
        return service;
    }

    public AsyncCallback<FR> getRefreshCallback() {
        return refreshCallback;
    }

    private void alertButtonClicked(String attribute) {
        Style style = alertBtn.getElement().getStyle();
        if ("1".equals(attribute)) {
            alertBtn.getElement().setAttribute("state", "2");
            style.setColor("red");
            style.setFontWeight(FontWeight.BOLDER);
            alertBtn.setText("Continue Alerts");
            Scheduler.get().scheduleFixedDelay(alertBlick, 1500);
        } else {
            alertBtn.getElement().setAttribute("state", "1");
            style.setColor("black");
            style.setFontWeight(FontWeight.NORMAL);
            alertBtn.setText("Stop Alerts");

        }
    }

    public CC getClientConfigurations() {
        return clientConfigurations;
    }

    public void hideHeaderAndFooter() {
        mainHeader.setVisible(false);
        footer.setVisible(false);
    }
}
