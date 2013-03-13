package com.smexec.monitor.server.model.poker;

import java.util.ArrayList;
import java.util.HashMap;

import com.smexec.monitor.server.model.AbstractConnectedServersState;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.poker.ChannelSeverStats;
import com.smexec.monitor.shared.poker.ConnectedServerPoker;
import com.smexec.monitor.shared.poker.GameServerClientStats;
import com.smexec.monitor.shared.poker.LobbySeverStats;
import com.smexec.monitor.shared.poker.RefreshResultPoker;

public class ConnectedServersStatePoker
    extends AbstractConnectedServersState<ServerStatausPoker, RefreshResultPoker, ConnectedServerPoker>
    implements IConnectedServersState<ServerStatausPoker, ConnectedServerPoker> {

    ChannelSeverStats aggregatedChannelSeverStats = new ChannelSeverStats();

    LobbySeverStats lobbySeverStats = new LobbySeverStats();

    GameServerClientStats gameServerClientStats = new GameServerClientStats();

    @Override
    public void mergeExtraData(ServerStatausPoker ss) {
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

    @Override
    public void mergeStats(ArrayList<ConnectedServerPoker> servers) {
        aggregatedChannelSeverStats = new ChannelSeverStats();

        lobbySeverStats = new LobbySeverStats();

        gameServerClientStats = new GameServerClientStats();
        super.mergeStats(servers);
    }

    @Override
    public RefreshResultPoker createNewRefreshResult(String title, ArrayList<ConnectedServerPoker> servers, HashMap<String, PoolsFeed> poolFeedMap) {
        return new RefreshResultPoker(title, servers, poolFeedMap, aggregatedChannelSeverStats, lobbySeverStats, gameServerClientStats);
    }
}
