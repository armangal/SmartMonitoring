package com.smexec.monitor.server.tasks.poker;

import com.smexec.monitor.server.model.poker.ServerStatausPoker;
import com.smexec.monitor.server.tasks.Refresher;
import com.smexec.monitor.server.utils.poker.JMXChannelServerStats;
import com.smexec.monitor.server.utils.poker.JMXGameServerStats;
import com.smexec.monitor.server.utils.poker.JMXLobbyServerStats;

public class RefresherPoker
    extends Refresher<ServerStatausPoker> {

    public RefresherPoker(ServerStatausPoker ss) {
        super(ss);
    }

    @Override
    public void fillExtraData(ServerStatausPoker ss) {
        super.fillExtraData(ss);

        JMXChannelServerStats.getChannelStatistics(ss);
        JMXLobbyServerStats.getLobbyStatistics(ss);
        JMXGameServerStats.getGameServerStatistics(ss);

    }
}
