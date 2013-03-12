package com.smexec.monitor.server.model.poker;

import java.io.Serializable;

import com.smexec.monitor.server.model.ServerConfig;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.shared.poker.ChannelSeverStats;
import com.smexec.monitor.shared.poker.LobbySeverStats;

public class ServerStatausPoker
    extends ServerStataus
    implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * if it's channle server, then here we will have the appropriate stats
     */
    private ChannelSeverStats channelSeverStats;

    /**
     * if it's lobby server, then here we will have the appropriate stats
     */
    private LobbySeverStats lobbySeverStats;

    private GameServerStats gameServerStats;

    public ServerStatausPoker(ServerConfig serverConfig) {
        super(serverConfig);
    }

    /**
     * returns channel server stats, will lazily initiate if needed
     * 
     * @return
     */
    public ChannelSeverStats getChannelSeverStats() {
        if (channelSeverStats == null) {
            this.channelSeverStats = new ChannelSeverStats();
        }
        return channelSeverStats;
    }

    public LobbySeverStats getLobbySeverStats() {
        if (lobbySeverStats == null) {
            this.lobbySeverStats = new LobbySeverStats();
        }
        return lobbySeverStats;
    }

    public boolean hasChannelSeverStats() {
        return channelSeverStats != null;
    }

    public boolean hasLobbySeverStats() {
        return lobbySeverStats != null;
    }

    public boolean hasGameSeverStats() {
        return gameServerStats != null;
    }

    public GameServerStats getGameSeverStats() {
        if (gameServerStats == null) {
            this.gameServerStats = new GameServerStats();
        }
        return gameServerStats;
    }

}
