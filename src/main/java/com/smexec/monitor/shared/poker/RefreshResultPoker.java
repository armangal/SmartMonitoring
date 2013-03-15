package com.smexec.monitor.shared.poker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.RefreshResult;

public class RefreshResultPoker
    extends RefreshResult<ConnectedServerPoker>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private ChannelSeverStats channelSeverStats;

    private LobbySeverStats lobbySeverStats;

    private GameServerClientStats gameServerStats;

    public RefreshResultPoker() {}

    public RefreshResultPoker(String title,
                              ArrayList<ConnectedServerPoker> servers,
                              HashMap<String, PoolsFeed> poolFeedMap,
                              ChannelSeverStats channelSeverStats,
                              LobbySeverStats lobbySeverStats,
                              GameServerClientStats gameServerStats) {
        super(title, servers, poolFeedMap);
        this.channelSeverStats = channelSeverStats;
        this.lobbySeverStats = lobbySeverStats;
        this.gameServerStats = gameServerStats;
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
