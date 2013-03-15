package com.smexec.monitor.client;

import com.allen_sauer.gwt.log.client.Log;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.RefreshResult;

public class Smartexecutormonitor
    extends AbstractEntryPoint<ConnectedServer, RefreshResult<ConnectedServer>, FullRefreshResult<RefreshResult<ConnectedServer>, ConnectedServer>> {

    
    public Smartexecutormonitor() {
        Log.debug("Smartexecutormonitor created");
    }
}
