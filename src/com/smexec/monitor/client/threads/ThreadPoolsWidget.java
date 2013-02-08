package com.smexec.monitor.client.threads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.shared.PoolsFeed;

public class ThreadPoolsWidget
    extends AbstractMonitoringWidget {

    private Map<String, PoolWidget> poolsMap = new HashMap<String, PoolWidget>();

    private FlowPanel fp = new FlowPanel();

    public ThreadPoolsWidget() {
        super("Thread Pools");
        addStyleName("threadPoolsWidget");
        getScrollPanel().add(fp);
    }

    public void refresh(HashMap<String, PoolsFeed> map) {
        // TODO, clean, remove, update

        fp.clear();

        if (map.size() == 0) {
            return;
        }

        final List<PoolsFeed> values = new ArrayList<PoolsFeed>(map.values());

        Collections.sort(values, new Comparator<PoolsFeed>() {

            @Override
            public int compare(PoolsFeed o1, PoolsFeed o2) {
                return (int) (o2.getSubmitted() - o1.getSubmitted());
            }
        });

        PoolsFeed pf = values.get(0);
        String poolName = pf.getPoolName();

        PoolWidget w = poolsMap.get(poolName);
        if (w == null) {
            w = new PoolWidget(pf.getPoolName());
            poolsMap.put(pf.getPoolName(), w);
        }

        fp.add(w);
        w.refresh(pf);

        FlexTable ft = new FlexTable();
        ft.getElement().setId("infoTable");
        fp.add(ft);
        ft.setCellPadding(0);
        ft.setCellSpacing(0);

        int i = 0;
        ft.setText(i, 0, "Pool Name");
        ft.setText(i, 1, "Submited");
        ft.setText(i, 2, "Executed");
        ft.setText(i, 3, "Completed");
        ft.setText(i, 4, "Rejected");
        ft.setText(i, 5, "Failed");
        ft.getRowFormatter().getElement(i++).setId("th");

        for (PoolsFeed feed : values) {
            ft.setText(i, 0, "" + feed.getPoolName());
            ft.setText(i, 1, "" + feed.getSubmitted());
            ft.setText(i, 2, "" + feed.getExecuted());
            ft.setText(i, 3, "" + feed.getCompleted());
            ft.setText(i, 4, "" + feed.getRejected());
            ft.setText(i++, 5, "" + feed.getFailed());
        }
    }
}
