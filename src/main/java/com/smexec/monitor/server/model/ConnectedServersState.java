package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

public class ConnectedServersState
    extends AbstractConnectedServersState<ServerStataus, RefreshResult, ConnectedServer>
    implements IConnectedServersState<ServerStataus, ConnectedServer, RefreshResult> {

    @Override
    public void mergeExtraData(ServerStataus ss) {
        // nothing
    }

    @Override
    public RefreshResult createNewRefreshResult(ArrayList<ConnectedServer> servers, HashMap<String, PoolsFeed> poolFeedMap) {
        return new RefreshResult(servers, poolFeedMap);
    }
}
