package com.smexec.monitor.client.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.RefreshResult;

public interface IMonitoringWidget<CS extends ConnectedServer, R extends RefreshResult<CS>>
    extends IsWidget {

    void update(FullRefreshResult<R, CS> result);

    void clear(FullRefreshResult<R, CS> result);
}
