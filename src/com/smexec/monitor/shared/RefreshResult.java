package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RefreshResult
    implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * a map of aggregated thread pools statistic, ready to be presented by client
     */
    private HashMap<String, PoolsFeed> poolFeedMap;

    /**
     * list of connected servers with internal stats
     */
    private ArrayList<ConnectedServer> servers = new ArrayList<ConnectedServer>(0);

    private String title;

    private ChannelSeverStats channelSeverStats;

    private LobbySeverStats lobbySeverStats;

    private GameServerClientStats gameServerStats;

    public RefreshResult() {}

    public RefreshResult(String title,
                         ArrayList<ConnectedServer> servers,
                         HashMap<String, PoolsFeed> poolFeedMap,
                         ChannelSeverStats channelSeverStats,
                         LobbySeverStats lobbySeverStats,
                         GameServerClientStats gameServerStats) {
        super();
        this.poolFeedMap = poolFeedMap;
        this.servers = servers;
        this.title = title;
        this.channelSeverStats = channelSeverStats;
        this.lobbySeverStats = lobbySeverStats;
        this.gameServerStats = gameServerStats;
    }

    public HashMap<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    public ArrayList<ConnectedServer> getServers() {
        return servers;
    }

    public String getTitle() {
        return title;
    }

    public ChannelSeverStats getChannelSeverStats() {
        return channelSeverStats;
    }

    public LobbySeverStats getLobbySeverStats() {
        return lobbySeverStats;
    }

    public GameServerClientStats getGameServerStats() {
        return gameServerStats;
    }
}
