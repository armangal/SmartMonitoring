package com.smexec.monitor.shared;

import java.io.Serializable;

public class LobbyChunkStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private int startTime;
    private int endTime;

    private int funTables;
    private int funActiveTables;
    private int funCashPlayers;

    private int funTournamentPlayers;
    private int funActiveTournaments;
    
    private int realSpeedRooms;
    private int realActiveSpeedRooms;
    private int realActiveSpeedRoomPlayers;

    private int realTables;
    private int realActiveTables;
    private int realCashPLayers;

    private int realActiveTournaments;
    private int realTournamentPlayers;

    public LobbyChunkStats() {}


    public LobbyChunkStats(int startTime,
                           int endTime,
                           int funTables,
                           int funActiveTables,
                           int funCashPlayers,
                           int funTournamentPlayers,
                           int funActiveTournaments,
                           int realSpeedRooms,
                           int realActiveSpeedRooms,
                           int realActiveSpeedRoomPlayers,
                           int realTables,
                           int realActiveTables,
                           int realCashPLayers,
                           int realActiveTournaments,
                           int realTournamentPlayers) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.funTables = funTables;
        this.funActiveTables = funActiveTables;
        this.funCashPlayers = funCashPlayers;
        this.funTournamentPlayers = funTournamentPlayers;
        this.funActiveTournaments = funActiveTournaments;
        this.realSpeedRooms = realSpeedRooms;
        this.realActiveSpeedRooms = realActiveSpeedRooms;
        this.realActiveSpeedRoomPlayers = realActiveSpeedRoomPlayers;
        this.realTables = realTables;
        this.realActiveTables = realActiveTables;
        this.realCashPLayers = realCashPLayers;
        this.realActiveTournaments = realActiveTournaments;
        this.realTournamentPlayers = realTournamentPlayers;
    }


    public int getStartTime() {
        return startTime;
    }

    public int getStartTimeForChart() {
        return startTime % 10000;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getFunActiveTables() {
        return funActiveTables;
    }

    public int getFunActiveTournaments() {
        return funActiveTournaments;
    }

    public int getFunCashPlayers() {
        return funCashPlayers;
    }

    public int getFunTables() {
        return funTables;
    }

    public int getFunTournamentPlayers() {
        return funTournamentPlayers;
    }

    public int getRealActiveSpeedRoomPlayers() {
        return realActiveSpeedRoomPlayers;
    }

    public int getRealActiveSpeedRooms() {
        return realActiveSpeedRooms;
    }

    public int getRealActiveTables() {
        return realActiveTables;
    }

    public int getRealActiveTournaments() {
        return realActiveTournaments;
    }

    public int getRealCashPLayers() {
        return realCashPLayers;
    }

    public int getRealSpeedRooms() {
        return realSpeedRooms;
    }

    public int getRealTables() {
        return realTables;
    }

    public int getRealTournamentPlayers() {
        return realTournamentPlayers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + startTime;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LobbyChunkStats other = (LobbyChunkStats) obj;
        if (startTime != other.startTime)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LobbyChunkStats [startTime=" + startTime + ", endTime=" + endTime + ", funActiveTables=" + funActiveTables + ", funActiveTournaments="
               + funActiveTournaments + ", funCashPlayers=" + funCashPlayers + ", funTables=" + funTables + ", funTournamentPlayers=" + funTournamentPlayers
               + ", realActiveSpeedRoomPlayers=" + realActiveSpeedRoomPlayers + ", realActiveSpeedRooms=" + realActiveSpeedRooms + ", realActiveTables="
               + realActiveTables + ", realActiveTournaments=" + realActiveTournaments + ", realCashPLayers=" + realCashPLayers + ", realSpeedRooms="
               + realSpeedRooms + ", realTables=" + realTables + ", realTournamentPlayers=" + realTournamentPlayers + "]";
    }

}
