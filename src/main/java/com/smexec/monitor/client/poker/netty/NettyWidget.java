package com.smexec.monitor.client.poker.netty;

import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.client.widgets.IMonitoringWidget;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.poker.ConnectedServerPoker;
import com.smexec.monitor.shared.poker.RefreshResultPoker;

public class NettyWidget
    extends AbstractMonitoringWidget
    implements IMonitoringWidget<ConnectedServerPoker, RefreshResultPoker> {

    public NettyWidget() {
        super("Netty Bindings");
        addStyleName("nettyWidget");

    }

    @Override
    public void update(FullRefreshResult<RefreshResultPoker, ConnectedServerPoker> result) {

    }

    @Override
    public void clear(FullRefreshResult<RefreshResultPoker, ConnectedServerPoker> result) {

    }

}
