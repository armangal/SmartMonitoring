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
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;

public abstract class AbstractEntryPoint<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final MonitoringServiceAsync<CS, R, FR> service;
    private final LoginWidget<CS, R, FR> loginWidget;

    private LinkedList<IMonitoringWidget<CS, R, FR>> widgets = new LinkedList<IMonitoringWidget<CS, R, FR>>();
    final FlowPanel mainPanel = new FlowPanel();

    final Button refreshBtn = new Button("Stop Refresh");
    boolean refresh = true;

    private HTML title = new HTML("<h1>----------------------------</h1>");

    private int lastAlertId = 0;

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

    public AbstractEntryPoint(MonitoringServiceAsync<CS, R, FR> service) {
        this.service = service;
        this.loginWidget = new LoginWidget<CS, R, FR>(service);
        this.loginWidget.registerCallBack(new LoggedInCallBack() {

            @Override
            public void loggedIn() {
                Log.debug("Authenticated");
                RootPanel.get().clear();
                addMainWidgets();
                RootPanel.get().add(mainPanel);
                RootPanel.get().add(refreshBtn);
                refresh = true;
                refresh();
                Scheduler.get().scheduleFixedDelay(refreshCommand, 20000);
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
        mainPanel.add(title);
        
        registerWidgets();
        
        Log.debug("AbstractEntryPoint created.");
    }

    /**
     * Add widget to the screen, the order matters.
     * 
     * @param widget
     */
    @SuppressWarnings("unchecked")
    public void addMonitoringWidget(IMonitoringWidget<?, ?, FR> widget) {
        widgets.add((IMonitoringWidget<CS, R, FR>) widget);
    }

    private void refresh() {

        Log.debug("Send refresh request.");

        service.refresh(lastAlertId, new AsyncCallback<FR>() {

            @Override
            public void onSuccess(FR fullResult) {
                if (fullResult == null) {
                    Log.debug("Received NULL response.");
                    return;
                }
                R result = fullResult.getRefreshResult();
                if (result == null) {
                    Log.debug("Received NULL response.");
                    return;
                }

                title.setHTML("<h1>" + result.getTitle() + ", v:" + fullResult.getVersion() + "</h1>");
                Window.setTitle(result.getTitle() + ", v:" + fullResult.getVersion());
                ArrayList<CS> servers = result.getServers();
                if (servers != null && !servers.isEmpty()) {
                    Log.debug("Received FULL refresh response.");

                    for (IMonitoringWidget<CS, R, FR> widget : widgets) {
                        widget.update(fullResult);
                    }

                } else {
                    Log.debug("Received EMPTY response.");
                    for (IMonitoringWidget<CS, R, FR> widget : widgets) {
                        widget.clear(fullResult);
                    }

                }

                lastAlertId = fullResult.getLastAlertId();

            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Received refresh response error:" + caught.getMessage());
                Window.setTitle("Error:" + caught.getMessage());
                refresh = false;
                RootPanel.get().clear();
                RootPanel.get().add(loginWidget);
            }
        });
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
        addMonitoringWidget(new ThreadPoolsWidget<CS, R, FR>());
        addMonitoringWidget(new ServersWidget<CS, R, FR>(service));
        addMonitoringWidget(new AlertsWidget<CS, R, FR>());
    }

    private void addMainWidgets() {
        for (IMonitoringWidget<CS, R, FR> widget : widgets) {
            mainPanel.add(widget);
        }
    }

    public MonitoringServiceAsync<CS, R, FR> getService() {
        return service;
    }
}