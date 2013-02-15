package com.smexec.monitor.client.threads;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.smexec.monitor.client.widgets.LineType;
import com.smexec.monitor.client.widgets.MonitoringLineChart;
import com.smexec.monitor.shared.PoolsFeed;

public class PoolWidget
    extends Composite {

    private NumberFormat formatLong = NumberFormat.getDecimalFormat();

    private MonitoringLineChart timeChart = new MonitoringLineChart(new LineType[] {LineType.MAX, LineType.AVG, LineType.MIN},
                                                                    "Milis",
                                                                    "Time",
                                                                    "Execution Time");

    private MonitoringLineChart tasksChart = new MonitoringLineChart(new LineType[] {LineType.SUBMITED, LineType.EXECUTED, LineType.COMPLETED, LineType.FAILED,
                                                                                     LineType.REJECTED}, "Tasks", "Milis", "Tasks");
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
        timeChart.updateChart(pf.getTimeChartFeeds());

        Log.debug("TasksChart:" + pf.getTasksChartFeeds());
        tasksChart.updateChart(pf.getTasksChartFeeds());

        tasksTable.setText(1, 1, formatLong.format(pf.getSubmitted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.SUBMITED.getIndex()) + ")");
        tasksTable.setText(2, 1, formatLong.format(pf.getExecuted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.EXECUTED.getIndex()) + ")");
        tasksTable.setText(3, 1, formatLong.format(pf.getCompleted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.COMPLETED.getIndex()) + ")");
        tasksTable.setText(4, 1, formatLong.format(pf.getRejected()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.REJECTED.getIndex()) + ")");
        tasksTable.setText(5, 1, formatLong.format(pf.getFailed()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.FAILED.getIndex()) + ")");

        timesTable.setText(1, 1, formatLong.format(pf.getMaxGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.MAX.getIndex()) + ")");
        timesTable.setText(2, 1, formatLong.format(pf.getAvgGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.AVG.getIndex()) + ")");
        timesTable.setText(3, 1, formatLong.format(pf.getMinGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.MIN.getIndex()) + ")");
        timesTable.setText(4, 1, formatLong.format(pf.getTotoalGenTime()));

        poolNameWidget.setHTML(this.pn + " | " + formatLong.format(pf.getActiveThreads()) + " | " + formatLong.format(pf.getPoolSize()) + " | "
                               + formatLong.format(pf.getLargestPoolSize()) + " | Hosts:" + formatLong.format(pf.getHosts()));

    }
}
