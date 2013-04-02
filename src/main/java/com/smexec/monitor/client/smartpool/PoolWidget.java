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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.smexec.monitor.client.widgets.ILineType;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

public class PoolWidget
    extends Composite {

    private NumberFormat formatLong = NumberFormat.getDecimalFormat();

    private MonitoringLineChart<Long, Long> timeChart = new MonitoringLineChart<Long, Long>(new ILineType[] {ExecutionTimeLineType.MAX, ExecutionTimeLineType.AVG,
                                                                                     ExecutionTimeLineType.MIN}, "Milis", "Time", "Execution Time");

    private MonitoringLineChart<Long, Long> tasksChart = new MonitoringLineChart<Long, Long>(new ILineType[] {TasksLineType.SUBMITED, TasksLineType.EXECUTED, TasksLineType.COMPLETED,
                                                                                      TasksLineType.FAILED, TasksLineType.REJECTED}, "Tasks", "Time", "Tasks");
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

        Log.debug("TimeChart:" + pf.getTimeChartFeeds());
        timeChart.updateChart(pf.getTimeChartFeeds(), true);

        Log.debug("TasksChart:" + pf.getTasksChartFeeds());
        tasksChart.updateChart(pf.getTasksChartFeeds(), true);

        tasksTable.setText(1, 1, formatLong.format(pf.getSubmitted()) + " (" + pf.getTasksChartFeeds().getLastValues(TasksLineType.SUBMITED.getIndex()) + ")");
        tasksTable.setText(2, 1, formatLong.format(pf.getExecuted()) + " (" + pf.getTasksChartFeeds().getLastValues(TasksLineType.EXECUTED.getIndex()) + ")");
        tasksTable.setText(3, 1, formatLong.format(pf.getCompleted()) + " (" + pf.getTasksChartFeeds().getLastValues(TasksLineType.COMPLETED.getIndex()) + ")");
        tasksTable.setText(4, 1, formatLong.format(pf.getRejected()) + " (" + pf.getTasksChartFeeds().getLastValues(TasksLineType.REJECTED.getIndex()) + ")");
        tasksTable.setText(5, 1, formatLong.format(pf.getFailed()) + " (" + pf.getTasksChartFeeds().getLastValues(TasksLineType.FAILED.getIndex()) + ")");

        timesTable.setText(1, 1, formatLong.format(pf.getMaxGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(ExecutionTimeLineType.MAX.getIndex())
                                 + ")");
        timesTable.setText(2, 1, formatLong.format(pf.getAvgGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(ExecutionTimeLineType.AVG.getIndex())
                                 + ")");
        timesTable.setText(3, 1, formatLong.format(pf.getMinGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(ExecutionTimeLineType.MIN.getIndex())
                                 + ")");
        timesTable.setText(4, 1, formatLong.format(pf.getTotoalGenTime()));

        poolNameWidget.setHTML(this.pn + " | " + formatLong.format(pf.getActiveThreads()) + " | " + formatLong.format(pf.getPoolSize()) + " | "
                               + formatLong.format(pf.getLargestPoolSize()) + " | Hosts:" + formatLong.format(pf.getHosts()));

    }
}
