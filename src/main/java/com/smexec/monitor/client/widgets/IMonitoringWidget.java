package com.smexec.monitor.client.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;

public interface IMonitoringWidget<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends IsWidget {

    void update(FR result);

    void clear(FR result);
}
