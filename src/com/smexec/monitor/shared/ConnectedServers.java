package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class ConnectedServers
    implements Serializable {

    private ArrayList<ConnectedServer> servers = new ArrayList<ConnectedServer>(0);

    public ConnectedServers() {}

    public ConnectedServers(ArrayList<ConnectedServer> servers) {
        super();
        this.servers = servers;
    }

    public ArrayList<ConnectedServer> getServers() {
        return servers;
    }

    public void setServers(ArrayList<ConnectedServer> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectedServers [servers=");
        builder.append(servers);
        builder.append("]");
        return builder.toString();
    }

}
