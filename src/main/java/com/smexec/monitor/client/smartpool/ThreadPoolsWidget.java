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
package com.smexec.monitor.client.smartpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.SmartExecutorService;
import com.smexec.monitor.client.SmartExecutorServiceAsync;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.smartpool.PoolsFeed;
import com.smexec.monitor.shared.smartpool.SmartExecutorRefreshRequest;
import com.smexec.monitor.shared.smartpool.SmartExecutorRefreshResponse;
import com.smexec.monitor.shared.smartpool.TaskExecutionChunk;

public class ThreadPoolsWidget
    extends AbstractMonitoringWidget<SmartExecutorRefreshRequest, SmartExecutorRefreshResponse, SmartExecutorServiceAsync>
    implements IMonitoringWidget {

    private HorizontalPanel title = new HorizontalPanel();

    private ListBox smartExecutorsList = new ListBox(false);

    private PoolWidget poolWidget = new PoolWidget();

    private FlowPanel fp = new FlowPanel();

    private FlexTable poolsTable = new FlexTable();

    private SmartExecutorRefreshResponse lastUpdate;

    private MouseOverHandler handCursor = new MouseOverHandler() {

        @Override
        public void onMouseOver(MouseOverEvent event) {
            ((Widget) event.getSource()).getElement().getStyle().setCursor(Cursor.POINTER);
        }
    };

    private String poolNameToShow = null;
    private ClickHandler setDefaultPoolClickHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            poolNameToShow = ((HTML) event.getSource()).getElement().getAttribute("name");

            PoolsFeed pf = lastUpdate.getSmartExecutorsMap().get(smartExecutorsList.getItemText(smartExecutorsList.getSelectedIndex())).get(poolNameToShow);
            if (pf != null) {
                poolWidget.refresh(pf);
            }

        }
    };

    /**
     * 
     */
    public ThreadPoolsWidget() {
        super("Thread Pools", 20000, ((SmartExecutorServiceAsync) GWT.create(SmartExecutorService.class)));
        addStyleName("threadPoolsWidget");
        getDataPanel().add(fp);
        fp.add(poolWidget);
        fp.add(poolsTable);

        title.setStyleName("serversHeader");
        setTitleWidget(title);
        title.add(new Label("Smart Executor, availible:"));
        title.add(smartExecutorsList);
        title.add(getRefProg());

        smartExecutorsList.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                update();
            }
        });

    }

    @Override
    public void clear() {
        this.lastUpdate = null;
        fp.remove(poolsTable);
        poolWidget.clear();
    }

    @Override
    public SmartExecutorRefreshRequest createRefreshRequest() {
        return new SmartExecutorRefreshRequest();
    }

    @Override
    public void refreshFailed(Throwable t) {
        Log.error("Smart Pool widget resresh error:" + t.getMessage(), t);
    }

    @Override
    public void refresh(SmartExecutorRefreshResponse refershResponse) {

        this.lastUpdate = refershResponse;
        String selected = null;
        if (smartExecutorsList.getSelectedIndex() >= 0) {
            selected = smartExecutorsList.getItemText(smartExecutorsList.getSelectedIndex());
        }
        smartExecutorsList.clear();
        int sel = 0, toSel = 0;
        for (String exName : refershResponse.getSmartExecutorsMap().keySet()) {
            smartExecutorsList.addItem(exName);
            if (exName.equals(selected)) {
                toSel = sel;
            }
            sel++;
        }
        smartExecutorsList.setSelectedIndex(toSel);

        update();

    }

    private void update() {
        HashMap<String, PoolsFeed> map = lastUpdate.getSmartExecutorsMap().get(smartExecutorsList.getItemText(smartExecutorsList.getSelectedIndex()));
        fp.remove(poolsTable);

        if (map.size() == 0) {
            poolNameToShow = null;
            poolWidget.clear();
            return;
        }

        final List<PoolsFeed> values = new ArrayList<PoolsFeed>(map.values());
        PoolsFeed pf = null;

        if (poolNameToShow != null) {
            pf = map.get(poolNameToShow);
        }

        if (pf == null) {
            Collections.sort(values, new Comparator<PoolsFeed>() {

                @Override
                public int compare(PoolsFeed o1, PoolsFeed o2) {
                    return (int) (o2.getTotoalGenTime() - o1.getTotoalGenTime());
                }
            });
            pf = values.get(0);
            poolNameToShow = null;
        }

        poolWidget.refresh(pf);

        poolsTable = new FlexTable();
        poolsTable.getElement().setId("infoTable");
        poolsTable.setStyleName("poolsList");
        fp.add(poolsTable);
        poolsTable.setCellPadding(0);
        poolsTable.setCellSpacing(0);

        int i = 0;
        int j = 0;
        poolsTable.setText(i, j++, "Name");
        poolsTable.setText(i, j++, "Subm");
        poolsTable.setText(i, j++, "Exec");
        poolsTable.setText(i, j++, "Comp");
        poolsTable.setText(i, j++, "Rejc");
        poolsTable.setText(i, j++, "Fail");
        poolsTable.setText(i, j++, "Time.MX");
        poolsTable.setText(i, j++, "Time.AV");
        poolsTable.setText(i, j++, "Time.MI");
        poolsTable.setText(i, j++, "Time.TO");
        poolsTable.setText(i, j++, "Hosts");

        poolsTable.getRowFormatter().getElement(i++).setId("th");

        for (PoolsFeed feed : values) {
            j = 0;
            final HTML name = new HTML("<a>" + feed.getPoolName() + "</a>");
            name.addMouseOverHandler(handCursor);
            name.getElement().setAttribute("name", feed.getPoolName());
            name.addClickHandler(setDefaultPoolClickHandler);
            poolsTable.setWidget(i, j++, name);
            TaskExecutionChunk last = feed.getChunks().getLast();
            poolsTable.setText(i, j++, "" + feed.getSubmitted() + " (" + last.getGlobalStats().getSubmitted() + ")");
            poolsTable.setText(i, j++, "" + feed.getExecuted() + " (" + last.getGlobalStats().getExecuted() + ")");
            poolsTable.setText(i, j++, "" + feed.getCompleted() + " (" + last.getGlobalStats().getCompleted() + ")");
            poolsTable.setText(i, j++, "" + feed.getRejected() + " (" + last.getGlobalStats().getRejected() + ")");
            poolsTable.setText(i, j++, "" + feed.getFailed() + " (" + last.getGlobalStats().getFailed() + ")");
            poolsTable.setText(i, j++, "" + feed.getMaxGenTime() + " (" + last.getGlobalStats().getMax() + ")");
            poolsTable.setText(i, j++, "" + feed.getAvgGenTime() + " (" + last.getGlobalStats().getAvgTime() + ")");
            poolsTable.setText(i, j++, "" + feed.getMinGenTime() + " (" + last.getGlobalStats().getMin() + ")");
            poolsTable.setText(i, j++, "" + feed.getTotoalGenTime() / 1000 + "sec");
            poolsTable.setText(i, j++, "" + feed.getHosts());
            i++;
        }
    }
}
