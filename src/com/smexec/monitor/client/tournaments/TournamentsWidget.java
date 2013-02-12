package com.smexec.monitor.client.tournaments;

import com.google.gwt.user.client.ui.FlowPanel;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;

public class TournamentsWidget
    extends AbstractMonitoringWidget {

    private FlowPanel fp = new FlowPanel();

    public TournamentsWidget() {
        super("Tournaments");
        setStyleName("tournamentsWidget");

        getScrollPanel().add(fp);
    }

    public void update() {
        fp.clear();
        FlowPanel left = getLeft();
        FlowPanel right = getRight();
        fp.add(left);
        fp.add(right);

    }

    private FlowPanel getLeft() {
        FlowPanel left = new FlowPanel();
        return left;
    }

    private FlowPanel getRight() {
        FlowPanel left = new FlowPanel();
        return left;
    }
}
