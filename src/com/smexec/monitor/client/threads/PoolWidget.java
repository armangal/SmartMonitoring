package com.smexec.monitor.client.threads;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.smexec.monitor.client.widgets.LineChart;
import com.smexec.monitor.client.widgets.LineType;
import com.smexec.monitor.shared.PoolsFeed;

public class PoolWidget
    extends Composite {

    private NumberFormat formatLong = NumberFormat.getDecimalFormat();

    private LineChart timeChart = new LineChart();
    private HTML timesData = new HTML();

    private LineChart tasksChart = new LineChart();
    private HTML tasksData = new HTML();
    private HTML tasksData2 = new HTML();

    private HTML poolNameWidget = new HTML();;

    private Label avgGenTime = new Label();;
    private Label maxGenTime = new Label();;
    private Label minGenTime = new Label();;
    private Label totoalGenTime = new Label();;

    private Label executed = new Label();;
    private Label submitted = new Label();;
    private Label rejected = new Label();;
    private Label completed = new Label();;
    private Label failed = new Label();;
    private Label activeThreads = new Label();;
    private Label largestPoolSize = new Label();;
    private Label hosts = new Label();;
    private Label poolSize = new Label();;

    private FlowPanel fp = new FlowPanel();

    private String pn;

    public PoolWidget() {

        this.poolNameWidget.setStylePrimaryName("poolName");
        fp.setStyleName("poolWidget");

        FlowPanel threads = new FlowPanel();
        threads.setStyleName("poolThreads");
        threads.add(this.poolNameWidget);
        // threads.add(this.activeThreads);
        // threads.add(this.poolSize);
        // threads.add(this.largestPoolSize);
        // threads.add(this.hosts);
        fp.add(threads);

        FlowPanel timePanel = new FlowPanel();
        timePanel.setStyleName("poolCharts");
        timePanel.add(timeChart);
        timePanel.add(timesData);
        timesData.setStyleName("poolTimes");
        fp.add(timePanel);

        FlowPanel taskPanel = new FlowPanel();
        taskPanel.setStyleName("poolCharts");
        taskPanel.add(tasksChart);
        taskPanel.add(tasksData);
        tasksData.setStyleName("poolTasks");
        taskPanel.add(tasksData2);
        tasksData2.setStyleName("poolTasks");
        fp.add(taskPanel);

        initWidget(fp);
    }

    public void clear() {
        timeChart.setVisible(false);
        tasksChart.setVisible(false);
        timesData.setHTML("");
        tasksData.setHTML("");
        tasksData2.setHTML("");
        poolNameWidget.setHTML("");
    }
    public void refresh(PoolsFeed pf) {
        this.pn = pf.getPoolName();

        timeChart.setVisible(false);
        tasksChart.setVisible(false);
        timeChart.updateChart(pf.getTimeChartFeeds(), new LineType[] {LineType.MAX, LineType.AVG, LineType.MIN});
        timeChart.update();

        tasksChart.updateChart(pf.getTasksChartFeeds(), new LineType[] {LineType.SUBMITED, LineType.EXECUTED, LineType.FAILED, LineType.REJECTED,
                                                                        LineType.COMPLETED});
        tasksChart.update();

        // submitted.setText("Submitted:" + formatLong.format(pf.getSubmitted()) + " (" +
        // pf.getTasksChartFeeds().getLastValues(LineType.SUBMITED.getIndex())
        // + ")");
        // executed.setText("Executed:" + formatLong.format(pf.getExecuted()) + " (" +
        // pf.getTasksChartFeeds().getLastValues(LineType.EXECUTED.getIndex()) + ")");
        // completed.setText("Completed:" + formatLong.format(pf.getCompleted()) + " (" +
        // pf.getTasksChartFeeds().getLastValues(LineType.COMPLETED.getIndex())
        // + ")");
        // rejected.setText("Rejected:" + formatLong.format(pf.getRejected()) + " (" +
        // pf.getTasksChartFeeds().getLastValues(LineType.REJECTED.getIndex()) + ")");
        // failed.setText("Failed:" + formatLong.format(pf.getFailed()) + " (" +
        // pf.getTasksChartFeeds().getLastValues(LineType.FAILED.getIndex()) + ")");

        StringBuilder sb = new StringBuilder();
        sb.append(formatLong.format(pf.getSubmitted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.SUBMITED.getIndex()) + ")");
        sb.append(" | ").append(formatLong.format(pf.getExecuted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.EXECUTED.getIndex()) + ")");
        sb.append(" | ").append(formatLong.format(pf.getCompleted()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.COMPLETED.getIndex()) + ")");

        tasksData.setHTML(sb.toString());

        sb.setLength(0);
        sb.append(formatLong.format(pf.getRejected()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.REJECTED.getIndex()) + ")");
        sb.append(" | ").append(formatLong.format(pf.getFailed()) + " (" + pf.getTasksChartFeeds().getLastValues(LineType.FAILED.getIndex()) + ")");
        tasksData2.setHTML(sb.toString());

        // maxGenTime.setText("Max Time:" + formatLong.format(pf.getMaxGenTime()) + " (" +
        // pf.getTimeChartFeeds().getLastValues(LineType.MAX.getIndex()) + ")");
        // avgGenTime.setText("Avg Time:" + formatLong.format(pf.getAvgGenTime()) + " (" +
        // pf.getTimeChartFeeds().getLastValues(LineType.AVG.getIndex()) + ")");
        // minGenTime.setText("Min Time:" + formatLong.format(pf.getMinGenTime()) + " (" +
        // pf.getTimeChartFeeds().getLastValues(LineType.MIN.getIndex()) + ")");
        // totoalGenTime.setText("Total Time:" + formatLong.format(pf.getTotoalGenTime()));

        timesData.setHTML("" + formatLong.format(pf.getMaxGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.MAX.getIndex()) + ") | "
                          + formatLong.format(pf.getAvgGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.AVG.getIndex()) + ") | "
                          + formatLong.format(pf.getMinGenTime()) + " (" + pf.getTimeChartFeeds().getLastValues(LineType.MIN.getIndex()) + ")");

        // activeThreads.setText("Active Threads:" + formatLong.format(pf.getActiveThreads()));
        // poolSize.setText("Pool Size:" + formatLong.format(pf.getPoolSize()));
        // largestPoolSize.setText("Largest Pool Size:" + formatLong.format(pf.getLargestPoolSize()));
        // hosts.setText("Hosts:" + formatLong.format(pf.getHosts()));

        poolNameWidget.setHTML(this.pn + " | " + formatLong.format(pf.getActiveThreads()) + " | " + formatLong.format(pf.getPoolSize()) + " | "
                               + formatLong.format(pf.getLargestPoolSize()) + " | Hosts:" + formatLong.format(pf.getHosts()));

        timeChart.setVisible(true);
        tasksChart.setVisible(true);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pn == null) ? 0 : pn.hashCode());
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
        if (pn == null) {
            if (other.pn != null)
                return false;
        } else if (!pn.equals(other.pn))
            return false;
        return true;
    }

}
