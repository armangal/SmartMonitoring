package com.smexec.monitor.client.threads;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
        for (final PoolsFeed pf : map.values()) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    String poolName = pf.getPoolName();
                    if (!poolName.equals("Custom1(CM1)") && !GWT.isScript()) {
                        return;
                    }

                    PoolWidget w = poolsMap.get(poolName);
                    if (w == null) {
                        w = new PoolWidget(pf.getPoolName());
                        poolsMap.put(pf.getPoolName(), w);
                        fp.add(w);
                    }

                    w.refresh(pf);

                }
            });
        }

    }
}
