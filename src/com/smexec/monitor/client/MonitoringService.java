package com.smexec.monitor.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.smexec.monitor.shared.RefreshResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface MonitoringService
    extends RemoteService {

    List<String> connect(String address);

    RefreshResult refresh();
}
