package com.smexec.monitor.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.smexec.monitor.client.alerts.AlertsWidget;
import com.smexec.monitor.client.memory.MemoryWidget;
import com.smexec.monitor.client.players.PlayersWidget;
import com.smexec.monitor.client.servers.ServersWidget;
import com.smexec.monitor.client.threads.ThreadPoolsWidget;
import com.smexec.monitor.client.tournaments.TournamentsWidget;
import com.smexec.monitor.shared.ConnectedServers;
import com.smexec.monitor.shared.RefreshResult;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Smartexecutormonitor
    implements EntryPoint {

    final FlowPanel mainPanel = new FlowPanel();

    final Button refresh = new Button("Start Refresh");
    boolean continueRefresh = false;

    private AlertsWidget alertsWidget = new AlertsWidget();
    private MemoryWidget memoryWidget = new MemoryWidget();
    private PlayersWidget playersWidget = new PlayersWidget();
    private ServersWidget serversWidget = new ServersWidget();
    private TournamentsWidget tournamentsWidget = new TournamentsWidget();
    private ThreadPoolsWidget poolsWidget = new ThreadPoolsWidget();

    private RepeatingCommand refreshCommand = new RepeatingCommand() {

        @Override
        public boolean execute() {
            refresh();
            return continueRefresh;
        }
    };

    public Smartexecutormonitor() {}

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final MonitoringServiceAsync service = GWT.create(MonitoringService.class);

    private void refresh() {

        service.refresh(new AsyncCallback<RefreshResult>() {

            @Override
            public void onSuccess(RefreshResult result) {
                ConnectedServers cs = result.getConnectedServers();
                if (cs != null) {
                    serversWidget.update(cs.getServers());
                    memoryWidget.update(cs.getServers());
                    if (cs.getServers() == null || cs.getServers().isEmpty()) {
                        cleanMonitors();
                    }
                    
                    poolsWidget.refresh(result.getPoolFeedMap());
                }

            }

            @Override
            public void onFailure(Throwable caught) {

            }
        });
    }

    private void cleanMonitors() {

    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        RootPanel.get().add(mainPanel);
        mainPanel.setStyleName("mainPanel");
        mainPanel.add(new HTML("<h1>Smart Executor Monitoring</h1>"));

        mainPanel.add(tournamentsWidget);
        mainPanel.add(playersWidget);
        mainPanel.add(poolsWidget);
        mainPanel.add(memoryWidget);
        mainPanel.add(serversWidget);
        mainPanel.add(alertsWidget);

        refresh.getElement().setAttribute("state", "2");

        RootPanel.get().add(refresh);

        // mainPanel.add(monitors);

        refresh.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String attribute = refresh.getElement().getAttribute("state");
                if ("1".equals(attribute)) {
                    refresh.getElement().setAttribute("state", "2");
                    continueRefresh = false;
                    refresh.setText("Start Refresh");
                } else {
                    refresh.getElement().setAttribute("state", "1");
                    continueRefresh = true;
                    refresh();
                    Scheduler.get().scheduleFixedDelay(refreshCommand, 10000);
                    refresh.setText("Stop Refresh");
                }

            }
        });

    }
}
