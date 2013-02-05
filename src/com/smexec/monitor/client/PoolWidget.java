package com.smexec.monitor.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.smexec.monitor.shared.PoolsFeed;

public class PoolWidget
    extends Composite {

    private LineChart lineChart = new LineChart();

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

    private FlexTable ft = new FlexTable();

    public PoolWidget(String poolName) {
        this.poolName.setText("Poll Name:" + poolName);
        int i = 0;
        ft.setWidget(i, 0, lineChart);
        ft.getFlexCellFormatter().setRowSpan(0, 0, 4);
        ft.setWidget(i++, 1, this.poolName);
        ft.setWidget(i++, 0, this.submitted);
        ft.setWidget(i++, 0, this.executed);
        ft.setWidget(i++, 0, this.completed);
        ft.setWidget(i++, 0, this.rejected);
        ft.setWidget(i++, 0, this.failed);

        ft.setWidget(i++, 0, this.maxGenTime);
        ft.setWidget(i++, 0, this.avgGenTime);
        ft.setWidget(i++, 0, this.minGenTime);
        ft.setWidget(i++, 0, this.totoalGenTime);

        ft.setWidget(i++, 0, this.activeThreads);
        ft.setWidget(i++, 0, this.poolSize);
        ft.setWidget(i++, 0, this.largestPoolSize);

        initWidget(ft);
        ft.setBorderWidth(1);
    }

    public void refresh(PoolsFeed pf) {
        lineChart.updateChart(pf.getChartFeeds());
        lineChart.update();
        submitted.setText("Submitted:" + pf.getSubmitted());
        executed.setText("Executed:" + pf.getExecuted());
        completed.setText("Completed:" + pf.getCompleted());
        rejected.setText("Rejected:" + pf.getRejected());
        failed.setText("Failed:" + pf.getFailed());

        maxGenTime.setText("Max Time:" + pf.getMaxGenTime());
        avgGenTime.setText("Avg Time:" + pf.getAvgGenTime());
        minGenTime.setText("Min Time:" + pf.getMinGenTime());
        totoalGenTime.setText("Total Time:" + pf.getTotoalGenTime());

        activeThreads.setText("Active Threads:" + pf.getActiveThreads());
        poolSize.setText("Pool Size:" + pf.getPoolSize());
        largestPoolSize.setText("Largest Pool Size:" + pf.getLargestPoolSize());

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
