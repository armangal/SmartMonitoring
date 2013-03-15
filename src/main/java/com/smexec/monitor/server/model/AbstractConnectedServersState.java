package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.RefreshResult;

public abstract class AbstractConnectedServersState<S extends ServerStataus, R extends RefreshResult<C>, C extends ConnectedServer> {

    private ConcurrentHashMap<Integer, S> connectedServersMap = new ConcurrentHashMap<Integer, S>();

    /**
     * Result ready to be used by clients
     */
    private R result;

    private Map<Integer, Alert> alertsMap = new HashMap<Integer, Alert>();

    private AtomicInteger alertCounter = new AtomicInteger();

    public abstract R createNewRefreshResult(String title, ArrayList<C> servers, HashMap<String, PoolsFeed> poolFeedMap);

    public abstract void mergeExtraData(S ss);

    /**
     * current most up-to-date configurations
     */
    private ServersConfig serversConfig;

    public ConcurrentHashMap<Integer, S> getMap() {
        return connectedServersMap;
    }

    public synchronized R getRefreshResult() {
        return result;
    }

    public void mergeStats(ArrayList<C> servers) {
        HashMap<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>();

        for (S ss : connectedServersMap.values()) {
            if (ss.isConnected()) {
                for (PoolsFeed pf : ss.getPoolFeedMap().values()) {
                    // go over all pools in each server
                    if (poolFeedMap.containsKey(pf.getPoolName())) {
                        poolFeedMap.get(pf.getPoolName()).merge(pf);
                    } else {
                        poolFeedMap.put(pf.getPoolName(), pf);
                    }
                }

                mergeExtraData(ss);
            }
        }

        result = createNewRefreshResult(serversConfig.getName(), servers, poolFeedMap);
    }

    public void setServersConfig(ServersConfig sc) {
        serversConfig = sc;
    }

    public ServersConfig getServersConfig() {
        return serversConfig;
    }

    public Map<Integer, Alert> getAlertsMap() {
        return alertsMap;
    }

    public LinkedList<Alert> getAlertsAfter(int alertId) {
        LinkedList<Alert> alerts = new LinkedList<Alert>();
        for (int i = alertId + 1; i < Integer.MAX_VALUE; i++) {
            if (alertsMap.containsKey(i)) {
                alerts.add(alertsMap.get(i));
            } else {
                break;
            }
        }
        return alerts;
    }

    public void addAlert(Alert alert) {
        alert.setId(alertCounter.getAndIncrement());
        alertsMap.put(alert.getId(), alert);
    }

}
