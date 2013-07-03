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

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

public class ThreadPoolsWidget
    extends AbstractMonitoringWidget
    implements IMonitoringWidget {

    private PoolWidget poolWidget = new PoolWidget();

    private FlowPanel fp = new FlowPanel();

    private FlexTable poolsTable = new FlexTable();

    private HashMap<String, PoolsFeed> lastUpdate = new HashMap<String, PoolsFeed>(0);

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

            PoolsFeed pf = lastUpdate.get(poolNameToShow);
            if (pf != null) {
                poolWidget.refresh(pf);
            }

        }
    };

    /**
     * 
     */
    public ThreadPoolsWidget() {
        super("Thread Pools", 20000);
        addStyleName("threadPoolsWidget");
        getDataPanel().add(fp);
        fp.add(poolWidget);
        fp.add(poolsTable);

        refresh();

    }

    @Override
    public void clear() {
        this.lastUpdate = null;
        fp.remove(poolsTable);
        poolWidget.clear();
    }

    @Override
    public void refresh() {
        HashMap<String, PoolsFeed> map = null;// fullResult.getPoolFeedMap();
        this.lastUpdate = map;
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
            poolsTable.setText(i, j++, "" + feed.getSubmitted() + " (" + feed.getTasksChartFeeds().getLastValues(TasksLineType.SUBMITED.getIndex()) + ")");
            poolsTable.setText(i, j++, "" + feed.getExecuted() + " (" + feed.getTasksChartFeeds().getLastValues(TasksLineType.EXECUTED.getIndex()) + ")");
            poolsTable.setText(i, j++, "" + feed.getCompleted() + " (" + feed.getTasksChartFeeds().getLastValues(TasksLineType.COMPLETED.getIndex()) + ")");
            poolsTable.setText(i, j++, "" + feed.getRejected() + " (" + feed.getTasksChartFeeds().getLastValues(TasksLineType.REJECTED.getIndex()) + ")");
            poolsTable.setText(i, j++, "" + feed.getFailed() + " (" + feed.getTasksChartFeeds().getLastValues(TasksLineType.FAILED.getIndex()) + ")");
            poolsTable.setText(i, j++, "" + feed.getMaxGenTime() + " (" + feed.getTimeChartFeeds().getLastValues(ExecutionTimeLineType.MAX.getIndex()) + ")");
            poolsTable.setText(i, j++, "" + feed.getAvgGenTime() + " (" + feed.getTimeChartFeeds().getLastValues(ExecutionTimeLineType.AVG.getIndex()) + ")");
            poolsTable.setText(i, j++, "" + feed.getMinGenTime() + " (" + feed.getTimeChartFeeds().getLastValues(ExecutionTimeLineType.MIN.getIndex()) + ")");
            poolsTable.setText(i, j++, "" + feed.getTotoalGenTime() / 1000 + "sec");
            poolsTable.setText(i, j++, "" + feed.getHosts());
            i++;
        }

    }
}
