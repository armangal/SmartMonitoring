package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RefreshResult
    extends AbstractRefreshResult<ConnectedServer>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    public RefreshResult() {

    }

    public RefreshResult(String title, ArrayList<ConnectedServer> servers, HashMap<String, PoolsFeed> poolFeedMap) {
        super(title, servers, poolFeedMap);
    }
}
