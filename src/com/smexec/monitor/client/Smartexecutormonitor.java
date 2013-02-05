package com.smexec.monitor.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.RefreshResult;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Smartexecutormonitor
    implements EntryPoint {

    final Button connectButton = new Button("Connect");
    final TextBox addressField = new TextBox();
    final HorizontalPanel hp = new HorizontalPanel();
    final FlexTable servers = new FlexTable();
    final FlexTable monitors = new FlexTable();

    private RepeatingCommand refreshCommand = new RepeatingCommand() {

        @Override
        public boolean execute() {
            refresh();
            return true;
        }
    };
    
    
    public Smartexecutormonitor() {
        monitors.setBorderWidth(1);
    }

    private Map<String, PoolWidget> poolsMap = new HashMap<String, PoolWidget>();

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final MonitoringServiceAsync service = GWT.create(MonitoringService.class);

    private void refresh() {
        if (servers.getRowCount() > 0) {

            service.refresh(new AsyncCallback<RefreshResult>() {

                int col = 0;
                int row = 0;

                @Override
                public void onSuccess(RefreshResult result) {
                    servers.clear();
                    int i = 0;
                    for (String server : result.getServers()) {
                        servers.setWidget(i++, 0, new Label(server));
                    }
                    if (result.getServers() == null || result.getServers().isEmpty()) {
                        cleanMonitors();
                    }

                    for (final PoolsFeed pf : result.getPoolsFeeds()) {
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                            @Override
                            public void execute() {
                                PoolWidget w = poolsMap.get(pf.getPoolName());
                                if (w == null) {
                                    w = new PoolWidget(pf.getPoolName());
                                    monitors.setWidget(row, col++, w);

                                }

                                w.refresh(pf);
                                if (col % 2 == 0) {
                                    row++;
                                    col = 0;
                                }

                            }
                        });
                    }

                }

                @Override
                public void onFailure(Throwable caught) {

                }
            });
        }
    }

    private void cleanMonitors() {
        monitors.clear();
        poolsMap.clear();
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        addressField.setText("localhost:9010");
        hp.add(addressField);
        hp.add(connectButton);
        RootPanel.get().add(hp);

        RootPanel.get().add(servers);
        RootPanel.get().add(monitors);

        connectButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                RootPanel.get().clear();
                RootPanel.get().add(hp);
                RootPanel.get().add(servers);
                RootPanel.get().add(monitors);

                service.connect(addressField.getText(), new AsyncCallback<List<String>>() {

                    @Override
                    public void onSuccess(List<String> result) {
                        servers.clear();
                        int i = 0;
                        for (String server : result) {
                            servers.setWidget(i++, 0, new Label(server));
                        }

                        refresh();
                        Scheduler.get().scheduleFixedPeriod(refreshCommand, 10000);

                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        servers.clear();
                    }
                });
            }
        });

    }

}
