package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.smexec.monitor.shared.ChannelSeverStats;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.GameServerClientStats;
import com.smexec.monitor.shared.GameServerStats;
import com.smexec.monitor.shared.LobbySeverStats;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.RefreshResult;

public final class ConnectedServersState {

    private static ConcurrentHashMap<Integer, ServerStataus> map = new ConcurrentHashMap<Integer, ServerStataus>();

    /**
     * Result ready to be used by clients
     */
    private static RefreshResult result = new RefreshResult();

    /**
     * current most up-to-date configurations
     */
    private static ServersConfig serversConfig;

    public static ConcurrentHashMap<Integer, ServerStataus> getMap() {
        return map;
    }

    public static synchronized RefreshResult getRefreshResult() {
        return result;
    }

    public static synchronized void mergeStats(ArrayList<ConnectedServer> servers) {
        HashMap<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>();

        ChannelSeverStats aggregatedChannelSeverStats = new ChannelSeverStats();

        LobbySeverStats lobbySeverStats = new LobbySeverStats();

        GameServerClientStats gameServerClientStats = new GameServerClientStats();

        for (ServerStataus ss : map.values()) {
            if (ss.isConnected()) {
                for (PoolsFeed pf : ss.getPoolFeedMap().values()) {
                    // go over all pools in each server
                    if (poolFeedMap.containsKey(pf.getPoolName())) {
                        poolFeedMap.get(pf.getPoolName()).merge(pf);
                    } else {
                        poolFeedMap.put(pf.getPoolName(), pf);
                    }
                }

                // merge channel stats
                if (ss.hasChannelSeverStats()) {
                    aggregatedChannelSeverStats.merge(ss.getChannelSeverStats());
                }

                // merge lobby stats
                if (ss.hasLobbySeverStats()) {
                    if (ss.getLobbySeverStats().getLastChunk().getEndTime() > lobbySeverStats.getLastChunk().getEndTime()) {
                        // replacing with more up-to-date stats
                        lobbySeverStats = ss.getLobbySeverStats();
                    }
                }

                if (ss.hasGameSeverStats()) {
                    GameServerStats gameSeverStats = ss.getGameSeverStats();

                    gameServerClientStats.getInterrupted().addAll(gameSeverStats.getInterrupted());
                }
            }
        }

        result = new RefreshResult(serversConfig.getName(), servers, poolFeedMap, aggregatedChannelSeverStats, lobbySeverStats, gameServerClientStats);
    }

    public static void setServersConfig(ServersConfig sc) {
        serversConfig = sc;
    }

    public static ServersConfig getServersConfig() {
        return serversConfig;
    }
}
