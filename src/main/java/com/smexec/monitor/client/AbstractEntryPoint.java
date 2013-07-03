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
import com.smexec.monitor.shared.ServerTimeResult;
import com.smexec.monitor.shared.alert.AlertType;
import com.smexec.monitor.shared.config.ClientConfigurations;

public abstract class AbstractEntryPoint<CC extends ClientConfigurations>
    implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final MonitoringServiceAsync<CC> service;
    private final LoginWidget<CC> loginWidget;
    private Resources resources = GWT.create(Resources.class);

    private LinkedList<IMonitoringWidget> widgets = new LinkedList<IMonitoringWidget>();
    final FlowPanel mainPanel = new FlowPanel();

    private CC clientConfigurations;

    final Image refreshImg = new Image(resources.stopRefresh());
    final Image alertImg = new Image(resources.stopAlerts());

    boolean refresh = true;

    private FlowPanel mainHeader = new FlowPanel();
    private HTML mainHeaderLabel = new HTML("--------------------------");
    private ProgressLabel refProg = new ProgressLabel();
    private HTML footer = new HTML("<ul><li><img title=\"Feedback\" src=\"img/oo_icon.gif\"><span>&nbsp;&nbsp;Created by </span><a target='blank' href=\"https://twitter.com/armangal\">@armangal</a><span>, based on </span><a href='https://github.com/armangal/SmartMonitoring' target='blank'>SmartMonitoring</a><span> project.</span></span></li></ul>");

    private AlertsWidget<CC> alertsWidget;

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
            if ("2".equals(alertImg.getElement().getAttribute("state"))) {
                Style style = alertImg.getElement().getStyle();
                style.setColor(style.getColor().equals("red") ? "blue" : "red");
                return true;
            } else {
                return false;
            }
        }
    };

    private AsyncCallback<ServerTimeResult> refreshCallback = new AsyncCallback<ServerTimeResult>() {

        int errorCount = 0;

        @Override
        public void onSuccess(ServerTimeResult fullResult) {
            errorCount = 0;
            refProg.progress();

            if (fullResult == null) {
                Log.debug("Received NULL response.");
                return;
            }

            Log.debug("Server time:" + fullResult.toString());

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
        widgets.clear();
        RootPanel.get().add(loginWidget);

        // reset last IDs
        resetLastIDs();
        for (IMonitoringWidget widget : widgets) {
            widget.clear();
            widget.setRefresh(false);
            widget.asWidget().removeFromParent();
        }
        mainPanel.clear();
        mainPanel.add(mainHeader);
    }

    public void resetLastIDs() {
        alertsWidget.clear();
    }

    public AbstractEntryPoint(final MonitoringServiceAsync<CC> service) {
        this.service = service;
        this.loginWidget = new LoginWidget<CC>(service);
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

                    alertImg.getElement().setAttribute("state", cc.isAlertsEnabled() ? "2" : "1");
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
    public void addMonitoringWidget(IMonitoringWidget widget) {
        widgets.add((IMonitoringWidget) widget);
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

        refreshImg.getElement().setAttribute("state", "1");
        alertImg.getElement().setAttribute("state", "1");

        refreshImg.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String attribute = refreshImg.getElement().getAttribute("state");
                if ("1".equals(attribute)) {
                    refreshImg.getElement().setAttribute("state", "2");
                    refresh = false;
                    refreshImg.setResource(resources.continueRefresh());
                    refreshImg.setTitle("Start Refresh");
                    for (IMonitoringWidget widget : widgets) {
                        widget.setRefresh(false);
                    }

                } else {
                    refreshImg.getElement().setAttribute("state", "1");
                    refresh = true;
                    refresh();
                    refreshImg.setTitle("Stop Refresh");
                    refreshImg.setResource(resources.stopRefresh());
                }

            }
        });

        alertImg.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String attribute = alertImg.getElement().getAttribute("state");
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
        refreshImg.addStyleName("settings");
        refreshImg.setTitle("Stop Refresh");
        mainHeader.add(refreshImg);
        alertImg.addStyleName("settings");
        mainHeader.add(alertImg);

        Image settings = new Image(resources.settingsSmall());
        settings.addStyleName("settings");
        settings.setTitle("Settings");
        settings.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                SettingsPopup<CC> sp = new SettingsPopup<CC>(service);
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
        addMonitoringWidget(new ThreadPoolsWidget());
        addMonitoringWidget(new ServersWidget<CC>(service));
        alertsWidget = new AlertsWidget<CC>(service, AlertType.values());
        addMonitoringWidget(alertsWidget);
    }

    private void addMainWidgets() {
        for (IMonitoringWidget widget : widgets) {
            mainPanel.add(widget);
        }
    }

    public MonitoringServiceAsync<CC> getService() {
        return service;
    }

    public AsyncCallback<ServerTimeResult> getRefreshCallback() {
        return refreshCallback;
    }

    private void alertButtonClicked(String attribute) {
        Style style = alertImg.getElement().getStyle();
        if ("1".equals(attribute)) {
            alertImg.getElement().setAttribute("state", "2");
            style.setColor("red");
            style.setFontWeight(FontWeight.BOLDER);
            alertImg.setTitle("Continue Alerts");
            alertImg.setResource(resources.continueAlerts());
            Scheduler.get().scheduleFixedDelay(alertBlick, 1500);
        } else {
            alertImg.getElement().setAttribute("state", "1");
            style.setColor("black");
            style.setFontWeight(FontWeight.NORMAL);
            alertImg.setTitle("Stop Alerts");
            alertImg.setResource(resources.stopAlerts());
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
