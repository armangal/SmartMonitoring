package com.smexec.monitor.server.utils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.shared.LobbyChunkStats;
import com.smexec.monitor.shared.LobbySeverStats;

public class JMXLobbyServerStats {

    /**
     * gets stats about lobby server
     * 
     * @param ss2
     */
    public static void getLobbyStatistics(ServerStataus ss) {
        try {
            JMXConnector jmxConnector = ss.getConnector();
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
            ObjectName sessionStats = new ObjectName("com.playtech.poker.jmx:type=Stats,name=LobbyServerStats");
            if (mbsc.isRegistered(sessionStats)) {
                // Lobby server
                LobbySeverStats stats = ss.getLobbySeverStats();
                CompositeData[] data;
                if (ss.isFirstTimeAccess()) {
                    // load all stats from lobby
                    System.out.println("Requesting full stats from Lobby");
                    data = (CompositeData[]) mbsc.getAttribute(sessionStats, "ServerStats");

                } else {
                    // load delta
                    long startTime = stats.getLastChunk().getStartTime();
                    System.out.println("Requesting delta Lobby chunks from:" + startTime);
                    data = (CompositeData[]) mbsc.invoke(sessionStats, "getLastServerStats", new Object[] {startTime}, new String[] {"long"});
                }
                System.out.println("got :" + data.length + " chunks from lobby");

                for (int i = data.length - 1; i >= 0; i--) {
                    CompositeData cd = data[i];

                    LobbyChunkStats lcs = new LobbyChunkStats(JMXUtils.getIntAtributeFromComposite(cd, "startTime"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "endTime"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "funTables"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "funActiveTables"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "funCashPlayers"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "funTournamentPlayers"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "funActiveTournaments"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realSpeedRooms"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realActiveSpeedRooms"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realActiveSpeedRoomPlayers"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realTables"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realActiveTables"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realCashPlayers"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realActiveTournaments"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realTournamentPlayers"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realTournamentsInRegisterStatus"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "funTournamentsInRegisterStatus"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "realRegisteredPlayers"),
                                                              JMXUtils.getIntAtributeFromComposite(cd, "funRegisteredPlayers"));

                    stats.addChunk(lcs);
                    System.out.println(lcs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
