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
package org.clevermore.monitor.client.smartpool;

import java.util.LinkedList;

import org.clevermore.monitor.client.widgets.ILineType;
import org.clevermore.monitor.client.widgets.MonitoringLineChart;
import org.clevermore.monitor.shared.ChartFeed;
import org.clevermore.monitor.shared.smartpool.PoolsFeed;
import org.clevermore.monitor.shared.smartpool.TaskExecutionChunk;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class PoolWidget
    extends Composite {

    private NumberFormat formatLong = NumberFormat.getDecimalFormat();

    private MonitoringLineChart<Long, Long> timeChart = new MonitoringLineChart<Long, Long>(new ILineType[] {ExecutionTimeLineType.MAX,
                                                                                                             ExecutionTimeLineType.AVG,
                                                                                                             ExecutionTimeLineType.MIN},
                                                                                            "Milis",
                                                                                            "Time",
                                                                                            "Execution Time");

    private MonitoringLineChart<Long, Long> tasksChart = new MonitoringLineChart<Long, Long>(new ILineType[] {TasksLineType.SUBMITED, TasksLineType.EXECUTED,
                                                                                                              TasksLineType.COMPLETED, TasksLineType.FAILED,
                                                                                                              TasksLineType.REJECTED}, "Tasks", "Time", "Tasks");
    private FlexTable tasksTable = new FlexTable();
    private FlexTable timesTable = new FlexTable();

    private HTML poolNameWidget = new HTML();;

    private FlowPanel fp = new FlowPanel();

    private String pn;

    public PoolWidget() {

        this.poolNameWidget.setStylePrimaryName("poolName");
        fp.setStyleName("poolWidget");

        fp.add(this.poolNameWidget);

        FlowPanel timePanel = new FlowPanel();
        timePanel.setStyleName("poolCharts");
        timePanel.add(timeChart);
        timePanel.add(timesTable);
        fp.add(timePanel);

        FlowPanel taskPanel = new FlowPanel();
        taskPanel.setStyleName("poolCharts");
        taskPanel.add(tasksChart);
        taskPanel.add(tasksTable);

        fp.add(taskPanel);

        tasksChart.setStyleName("tasksChart");
        timeChart.setStyleName("timesChart");

        initilizeTimesTable();
        initilizeTasksTable();
        initWidget(fp);
    }

    private void initilizeTasksTable() {
        tasksTable.setCellPadding(0);
        tasksTable.setCellSpacing(0);
        tasksTable.setStyleName("poolDataTable");

        tasksTable.setText(0, 0, "Tasks Executed");

        tasksTable.getCellFormatter().getElement(0, 0).setId("th");

        tasksTable.getFlexCellFormatter().setColSpan(0, 0, 2);

        tasksTable.setText(1, 0, "Submited:");
        tasksTable.setText(2, 0, "Executed:");
        tasksTable.setText(3, 0, "Completed:");
        tasksTable.setText(4, 0, "Rejected:");
        tasksTable.setText(5, 0, "Failed:");

    }

    private void initilizeTimesTable() {
        timesTable.setCellPadding(0);
        timesTable.setCellSpacing(0);
        timesTable.setStyleName("poolDataTable");

        timesTable.setText(0, 0, "Execution Duration");

        timesTable.getCellFormatter().getElement(0, 0).setId("th");

        timesTable.getFlexCellFormatter().setColSpan(0, 0, 2);

        timesTable.setText(1, 0, "Max Time:");
        timesTable.setText(2, 0, "Avg. Time:");
        timesTable.setText(3, 0, "Min Time:");
        timesTable.setText(4, 0, "Total Time:");
    }

    public void clear() {
        tasksTable.setText(1, 1, "");
        tasksTable.setText(2, 1, "");
        tasksTable.setText(3, 1, "");
        tasksTable.setText(4, 1, "");
        tasksTable.setText(5, 1, "");

        timesTable.setText(1, 1, "");
        timesTable.setText(2, 1, "");
        timesTable.setText(3, 1, "");
        timesTable.setText(4, 1, "");

        poolNameWidget.setHTML("");
        timeChart.clean();
        tasksChart.clean();
    }

    public void refresh(PoolsFeed pf) {
        this.pn = pf.getPoolName();

        Log.debug("updating SE charts:");

        TaskExecutionChunk last = pf.getChunks().getLast();

        tasksTable.setText(1, 1, formatLong.format(pf.getSubmitted()) + " (" + last.getGlobalStats().getSubmitted() + ")");
        tasksTable.setText(2, 1, formatLong.format(pf.getExecuted()) + " (" + last.getGlobalStats().getExecuted() + ")");
        tasksTable.setText(3, 1, formatLong.format(pf.getCompleted()) + " (" + last.getGlobalStats().getCompleted() + ")");
        tasksTable.setText(4, 1, formatLong.format(pf.getRejected()) + " (" + last.getGlobalStats().getRejected() + ")");
        tasksTable.setText(5, 1, formatLong.format(pf.getFailed()) + " (" + last.getGlobalStats().getFailed() + ")");

        timesTable.setText(1, 1, formatLong.format(pf.getMaxGenTime()) + " (" + last.getGlobalStats().getMax() + ")");
        timesTable.setText(2, 1, formatLong.format(pf.getAvgGenTime()) + " (" + last.getGlobalStats().getAvgTime() + ")");
        timesTable.setText(3, 1, formatLong.format(pf.getMinGenTime()) + " (" + last.getGlobalStats().getMin() + ")");
        timesTable.setText(4, 1, formatLong.format(pf.getTotoalGenTime()));

        poolNameWidget.setHTML("Pool Name: " + this.pn + " | Active:" + formatLong.format(pf.getActiveThreads()) + " | Size:" + formatLong.format(pf.getPoolSize()) + " | Max:"
                               + formatLong.format(pf.getLargestPoolSize()) + " | Hosts:" + formatLong.format(pf.getHosts()));

        LinkedList<TaskExecutionChunk> chunks = pf.getChunks();
        ChartFeed<Long, Long> timeFeed = new ChartFeed<Long, Long>(new Long[3][chunks.size()], new Long[chunks.size()]);
        for (int j = 0; j < chunks.size(); j++) {
            TaskExecutionChunk tec = chunks.get(j);
            timeFeed.getValues()[0][j] = tec.getGlobalStats().getMax();
            timeFeed.getValues()[1][j] = tec.getGlobalStats().getAvgTime();
            timeFeed.getValues()[2][j] = tec.getGlobalStats().getMin();
            timeFeed.getXLineValues()[j] = tec.getEndTime();
        }

        timeChart.updateChart(timeFeed, true);

        ChartFeed<Long, Long> taskFeed = new ChartFeed<Long, Long>(new Long[5][chunks.size()], new Long[chunks.size()]);
        for (int j = 0; j < chunks.size(); j++) {
            TaskExecutionChunk tec = chunks.get(j);
            taskFeed.getValues()[0][j] = tec.getGlobalStats().getSubmitted();
            taskFeed.getValues()[1][j] = tec.getGlobalStats().getExecuted();
            taskFeed.getValues()[2][j] = tec.getGlobalStats().getCompleted();
            taskFeed.getValues()[3][j] = tec.getGlobalStats().getFailed();
            taskFeed.getValues()[4][j] = tec.getGlobalStats().getRejected();
            taskFeed.getXLineValues()[j] = tec.getEndTime();
        }

        tasksChart.updateChart(taskFeed, true);
    }
}
