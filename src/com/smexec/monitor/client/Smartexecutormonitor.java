package com.smexec.monitor.client;

import java.util.ArrayList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
import com.smexec.monitor.client.players.PlayersWidget;
import com.smexec.monitor.client.servers.ServersWidget;
import com.smexec.monitor.client.threads.ThreadPoolsWidget;
import com.smexec.monitor.client.tournaments.TournamentsWidget;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

public class Smartexecutormonitor
    implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final MonitoringServiceAsync service = GWT.create(MonitoringService.class);

    final FlowPanel mainPanel = new FlowPanel();

    final Button refreshBtn = new Button("Stop Refresh");
    boolean refresh = true;

    private HTML title = new HTML("<h1>----------------------------</h1>");
    private LoginWidget loginWidget = new LoginWidget();
    private AlertsWidget alertsWidget = new AlertsWidget();
    private ServersWidget serversWidget = new ServersWidget();
    private PlayersWidget playersWidget = new PlayersWidget();
    private TournamentsWidget tournamentsWidget = new TournamentsWidget();
    private ThreadPoolsWidget poolsWidget = new ThreadPoolsWidget();

    private RepeatingCommand refreshCommand = new RepeatingCommand() {

        @Override
        public boolean execute() {
            refresh();
            Log.debug("Reschedule refresh?:" + refresh);
            return refresh;
        }
    };

    public Smartexecutormonitor() {}

    private void refresh() {

        Log.debug("Send refresh request.");

        service.refresh(new AsyncCallback<RefreshResult>() {

            @Override
            public void onSuccess(RefreshResult result) {

                title.setHTML("<h1>" + result.getTitle() + "</h1>");
                ArrayList<ConnectedServer> servers = result.getServers();
                if (servers != null && !servers.isEmpty()) {
                    Log.debug("Received FULL refresh response.");

                    serversWidget.update(servers);
                    poolsWidget.refresh(result.getPoolFeedMap());
                    tournamentsWidget.update();
                    playersWidget.update(result.getChannelSeverStats(), servers);
                    alertsWidget.update();
                } else {
                    Log.debug("Received EMPTY response.");

                    poolsWidget.clear();
                }

            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Received refresh response error:" + caught.getMessage());

                Window.alert("Refresh Error:" + caught.getMessage());
            }
        });
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        Log.debug("Starting monitoring client.");

        Window.setMargin("0px");
        RootPanel.get().add(loginWidget);
        loginWidget.registerCallBack(new LoggedInCallBack() {

            @Override
            public void loggedIn() {
                Log.debug("Authenticated");
                loginWidget.removeFromParent();
                RootPanel.get().add(mainPanel);
                RootPanel.get().add(refreshBtn);
                refresh();
                Scheduler.get().scheduleFixedDelay(refreshCommand, 10000);
            }
        });

        mainPanel.setStyleName("mainPanel");
        mainPanel.add(title);

        mainPanel.add(tournamentsWidget);
        mainPanel.add(playersWidget);
        mainPanel.add(poolsWidget);
        mainPanel.add(serversWidget);
        mainPanel.add(alertsWidget);

        refreshBtn.getElement().setAttribute("state", "1");

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
                    Scheduler.get().scheduleFixedDelay(refreshCommand, 10000);
                    refreshBtn.setText("Stop Refresh");
                }

            }
        });

    }
}
