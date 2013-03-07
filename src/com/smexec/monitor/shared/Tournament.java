package com.smexec.monitor.shared;

import java.io.Serializable;

public class Tournament
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long code;

    private String name;

    private String date;

    private int reason;

    private int registeredPlayers;

    public Tournament() {}

    public Tournament(long code, String name, String date, int reason, int registeredPlayers) {
        super();
        this.code = code;
        this.name = name;
        this.date = date;
        this.reason = reason;
        this.registeredPlayers = registeredPlayers;
    }

    public Tournament(long code, String name, String date, int reason) {
        super();
        this.code = code;
        this.name = name;
        this.date = date;
        this.reason = reason;

    }

    public long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getReason() {
        return reason;
    }

    public int getRegisteredPlayers() {
        return registeredPlayers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (code ^ (code >>> 32));
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + reason;
        result = prime * result + registeredPlayers;
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
        Tournament other = (Tournament) obj;
        if (code != other.code)
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (reason != other.reason)
            return false;
        if (registeredPlayers != other.registeredPlayers)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tournament [code=");
        builder.append(code);
        builder.append(", name=");
        builder.append(name);
        builder.append(", date=");
        builder.append(date);
        builder.append(", reason=");
        builder.append(reason);
        builder.append(", registeredPlayers=");
        builder.append(registeredPlayers);
        builder.append("]");
        return builder.toString();
    }

}
