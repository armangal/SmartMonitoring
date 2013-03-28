package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.Collection;

import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;

public interface IConnectedServersState<SS extends ServerStataus, CS extends ConnectedServer, RR extends AbstractRefreshResult<CS>> {

    SS getServerStataus(final Integer serevrCode);
    
    Collection<SS> getAllServers();
    
    SS removeServer(final Integer serevrCode);
    
    SS addServer(SS serverStataus);

    void mergeStats(ArrayList<CS> servers);

    RR getRefreshResult();

}
