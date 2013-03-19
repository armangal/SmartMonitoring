package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.RefreshResult;

public class ConnectedServersState
    extends AbstractConnectedServersState<ServerStataus, RefreshResult, ConnectedServer>
    implements IConnectedServersState<ServerStataus, ConnectedServer, RefreshResult> {

    @Override
    public void mergeExtraData(ServerStataus ss) {
        // nothing
    }

    @Override
    public RefreshResult createNewRefreshResult(String title, ArrayList<ConnectedServer> servers, HashMap<String, PoolsFeed> poolFeedMap) {
        return new RefreshResult(title, servers, poolFeedMap);
    }
}
