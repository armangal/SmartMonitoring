package com.smexec.monitor.server.utils.poker;

import java.util.LinkedList;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;

import com.smexec.monitor.server.model.poker.GameServerChunk;
import com.smexec.monitor.server.model.poker.GameServerStats;
import com.smexec.monitor.server.model.poker.ServerStatausPoker;
import com.smexec.monitor.server.utils.JMXUtils;
import com.smexec.monitor.shared.poker.Tournament;

public class JMXGameServerStats {

    /**
     * getting stats from game server about
     * 
     * @param ss
     */
    public void getGameServerStatistics(ServerStatausPoker ss) {
        try {
            JMXConnector jmxConnector = ss.getConnector();
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            ObjectName sessionStats = new ObjectName("com.playtech.poker.jmx:type=Stats,name=PokerGameServerStats");
            if (mbsc.isRegistered(sessionStats)) {
                CompositeData[] data;
                GameServerStats gameSeverStats = ss.getGameSeverStats();
                if (ss.isFirstTimeAccess()) {
                    // load all stats from game server
                    System.out.println("Requesting full stats from Game server:" + ss.getServerConfig().getServerCode());
                    data = (CompositeData[]) mbsc.getAttribute(sessionStats, "ServerStats");

                } else {
                    // load delta
                    long startTime = gameSeverStats.getLastChunk().getStartTime();
                    System.out.println("Requesting delta Game chunks from:" + startTime);
                    data = (CompositeData[]) mbsc.invoke(sessionStats, "getLastServerStats", new Object[] {startTime}, new String[] {"long"});
                }

                System.out.println("got :" + data.length + " chunks from Game:" + ss.getServerConfig().getServerCode());

                for (int i = data.length - 1; i >= 0; i--) {
                    CompositeData cd = data[i];

                    GameServerChunk gch = new GameServerChunk(JMXUtils.getIntAtributeFromComposite(cd, "startTime"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "endTime"));

                    // load canceled tournaments
                    CompositeData[] canceled = (CompositeData[]) cd.get("cancelledTournaments");
                    if (canceled.length > 0) {
                        LinkedList<Tournament> list = new LinkedList<Tournament>();
                        for (CompositeData tr : canceled) {
                            Tournament tournament = new Tournament(ss.getServerConfig().getServerCode(),
                                                                   JMXUtils.getLongAtributeFromComposite(tr, "code"),
                                                                   JMXUtils.getStringAtributeFromComposite(tr, "name"),
                                                                   JMXUtils.getStringAtributeFromComposite(tr, "cancellationDate"),
                                                                   JMXUtils.getIntAtributeFromComposite(tr, "reason"));
                            list.add(tournament);
                        }
                        gch.setCanceled(list);
                    }

                    // load interrupted tournaments
                    CompositeData[] interrupted = (CompositeData[]) cd.get("interruptedTournaments");
                    if (interrupted.length > 0) {
                        LinkedList<Tournament> list = new LinkedList<Tournament>();
                        for (CompositeData tr : interrupted) {
                            Tournament tournament = new Tournament(ss.getServerConfig().getServerCode(),
                                                                   JMXUtils.getLongAtributeFromComposite(tr, "code"),
                                                                   JMXUtils.getStringAtributeFromComposite(tr, "name"),
                                                                   JMXUtils.getStringAtributeFromComposite(tr, "interruptionDate"),
                                                                   JMXUtils.getIntAtributeFromComposite(tr, "reason"),
                                                                   JMXUtils.getIntAtributeFromComposite(tr, "registeredPlayers"));
                            list.add(tournament);
                        }
                        gch.setInterrupted(list);
                    }

                    System.out.println(gch);
                    gameSeverStats.addChunk(gch);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
