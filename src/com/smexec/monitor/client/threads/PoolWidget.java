package com.smexec.monitor.client.threads;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.smexec.monitor.client.widgets.LineChart;
import com.smexec.monitor.client.widgets.LineChart.LineType;
import com.smexec.monitor.shared.PoolsFeed;

public class PoolWidget
    extends Composite {

    private NumberFormat formatLong = NumberFormat.getDecimalFormat();

    private LineChart timeChart = new LineChart();

    private LineChart tasksChart = new LineChart();

    private Label poolName = new Label();;
    private Label avgGenTime = new Label();;
    private Label maxGenTime = new Label();;
    private Label minGenTime = new Label();;
    private Label executed = new Label();;
    private Label submitted = new Label();;
    private Label rejected = new Label();;
    private Label completed = new Label();;
    private Label failed = new Label();;
    private Label totoalGenTime = new Label();;
    private Label activeThreads = new Label();;
    private Label largestPoolSize = new Label();;
    private Label hosts = new Label();;
    private Label poolSize = new Label();;

    private FlowPanel ft = new FlowPanel();

    public PoolWidget(String poolName) {
        this.poolName.setText("Poll Name:" + poolName);
        this.poolName.setStylePrimaryName("poolName");
        ft.setStyleName("poolWidget");
        ft.add(this.poolName);
        FlowPanel charts = new FlowPanel();
        charts.setStyleName("poolCharts");

        charts.add(timeChart);
        charts.add(tasksChart);
        ft.add(charts);

        FlowPanel tasks = new FlowPanel();
        tasks.setStyleName("poolTasks");
        tasks.add(this.submitted);
        tasks.add(this.executed);
        tasks.add(this.completed);
        tasks.add(this.rejected);
        tasks.add(this.failed);
        ft.add(tasks);

        FlowPanel times = new FlowPanel();
        times.setStyleName("poolTimes");
        times.add(this.maxGenTime);
        times.add(this.avgGenTime);
        times.add(this.minGenTime);
        times.add(this.totoalGenTime);
        ft.add(times);

        FlowPanel threads = new FlowPanel();
        threads.setStyleName("poolThreads");
        threads.add(this.activeThreads);
        threads.add(this.poolSize);
        threads.add(this.largestPoolSize);
        threads.add(this.hosts);
        ft.add(threads);

        initWidget(ft);
    }

    public void refresh(PoolsFeed pf) {
        timeChart.setVisible(false);
        tasksChart.setVisible(false);
        timeChart.updateChart(pf.getTimeChartFeeds(), new LineType[] {LineType.MAX, LineType.AVG, LineType.MIN});
        timeChart.update();

        tasksChart.updateChart(pf.getTasksChartFeeds(), new LineType[] {LineType.SUBMITED, LineType.EXECUTED, LineType.FAILED, LineType.REJECTED,
                                                                        LineType.COMPLETED});
        tasksChart.update();

        submitted.setText("Submitted:" + formatLong.format(pf.getSubmitted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.SUBMITED.getIndex())
                          + ")");
        executed.setText("Executed:" + formatLong.format(pf.getExecuted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.EXECUTED.getIndex()) + ")");
        completed.setText("Completed:" + formatLong.format(pf.getCompleted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.COMPLETED.getIndex())
                          + ")");
        rejected.setText("Rejected:" + formatLong.format(pf.getRejected()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.REJECTED.getIndex()) + ")");
        failed.setText("Failed:" + formatLong.format(pf.getFailed()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.FAILED.getIndex()) + ")");

        maxGenTime.setText("Max Time:" + formatLong.format(pf.getMaxGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.MAX.getIndex()) + ")");
        avgGenTime.setText("Avg Time:" + formatLong.format(pf.getAvgGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.AVG.getIndex()) + ")");
        minGenTime.setText("Min Time:" + formatLong.format(pf.getMinGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.MIN.getIndex()) + ")");
        totoalGenTime.setText("Total Time:" + formatLong.format(pf.getTotoalGenTime()));

        activeThreads.setText("Active Threads:" + formatLong.format(pf.getActiveThreads()));
        poolSize.setText("Pool Size:" + formatLong.format(pf.getPoolSize()));
        largestPoolSize.setText("Largest Pool Size:" + formatLong.format(pf.getLargestPoolSize()));
        hosts.setText("Hosts:" + formatLong.format(pf.getHosts()));

        timeChart.setVisible(true);
        tasksChart.setVisible(true);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((poolName == null) ? 0 : poolName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PoolWidget other = (PoolWidget) obj;
        if (poolName == null) {
            if (other.poolName != null)
                return false;
        } else if (!poolName.equals(other.poolName))
            return false;
        return true;
    }

}
