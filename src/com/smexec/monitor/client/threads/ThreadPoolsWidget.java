package com.smexec.monitor.client.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ft.setStyleName("poolsList");
        fp.add(ft);
        ft.setCellPadding(0);
        ft.setCellSpacing(0);

        int i = 0;
        int j = 0;
        ft.setText(i, j++, "Name");
        ft.setText(i, j++, "Subm");
        ft.setText(i, j++, "Exec");
        ft.setText(i, j++, "Comp");
        ft.setText(i, j++, "Rejc");
        ft.setText(i, j++, "Fail");
        ft.setText(i, j++, "Time.MX");
        ft.setText(i, j++, "Time.AV");
        ft.setText(i, j++, "Time.MI");
        ft.setText(i, j++, "Time.TO");
        ft.setText(i, j++, "Hosts");

        ft.getRowFormatter().getElement(i++).setId("th");

        for (PoolsFeed feed : values) {
            j = 0;
            ft.setText(i, j++, "" + feed.getPoolName());
            ft.setText(i, j++, "" + feed.getSubmitted());
            ft.setText(i, j++, "" + feed.getExecuted());
            ft.setText(i, j++, "" + feed.getCompleted());
            ft.setText(i, j++, "" + feed.getRejected());
            ft.setText(i, j++, "" + feed.getFailed());
            ft.setText(i, j++, "" + feed.getMaxGenTime());
            ft.setText(i, j++, "" + feed.getAvgGenTime());
            ft.setText(i, j++, "" + feed.getMinGenTime());
            ft.setText(i, j++, "" + feed.getTotoalGenTime() / 1000 + "sec");
            ft.setText(i, j++, "" + feed.getHosts());
            i++;
        }
    }
}
