package com.smexec.monitor.client;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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
    private Label poolSize = new Label();;

    private FlowPanel ft = new FlowPanel();

    public PoolWidget(String poolName) {
        this.poolName.setText("Poll Name:" + poolName);
        this.poolName.setStylePrimaryName("poolName");
        ft.add(this.poolName);
        ft.add(timeChart);
        ft.add(tasksChart);

        ft.add(this.submitted);
        ft.add(this.executed);
        ft.add(this.completed);
        ft.add(this.rejected);
        ft.add(this.failed);

        ft.add(this.maxGenTime);
        ft.add(this.avgGenTime);
        ft.add(this.minGenTime);
        ft.add(this.totoalGenTime);

        ft.add(this.activeThreads);
        ft.add(this.poolSize);
        ft.add(this.largestPoolSize);

        initWidget(ft);
    }

    public void refresh(PoolsFeed pf) {
//        if (GWT.isScript()) {
            timeChart.updateChart(pf.getChartFeeds());
            timeChart.update();
//        }
        submitted.setText("Submitted:" + formatLong.format(pf.getSubmitted()));
        executed.setText("Executed:" + formatLong.format(pf.getExecuted()));
        completed.setText("Completed:" + formatLong.format(pf.getCompleted()));
        rejected.setText("Rejected:" + formatLong.format(pf.getRejected()));
        failed.setText("Failed:" + formatLong.format(pf.getFailed()));

        maxGenTime.setText("Max Time:" + formatLong.format(pf.getMaxGenTime()));
        avgGenTime.setText("Avg Time:" + formatLong.format(pf.getAvgGenTime()));
        minGenTime.setText("Min Time:" + formatLong.format(pf.getMinGenTime()));
        totoalGenTime.setText("Total Time:" + formatLong.format(pf.getTotoalGenTime()));

        activeThreads.setText("Active Threads:" + formatLong.format(pf.getActiveThreads()));
        poolSize.setText("Pool Size:" + formatLong.format(pf.getPoolSize()));
        largestPoolSize.setText("Largest Pool Size:" + formatLong.format(pf.getLargestPoolSize()));

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
