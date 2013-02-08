package com.smexec.monitor.server.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.management.remote.JMXConnector;

import com.smexec.monitor.shared.MemoryUsage;
import com.smexec.monitor.shared.PoolsFeed;

public class ServerStataus {

    private boolean connected = false;
    private JMXConnector connector;
    private ServerConfig serverConfig;
    private LinkedList<MemoryUsage> memoryUsage = new LinkedList<MemoryUsage>();
    private Map<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>(0);

    public ServerStataus() {

    }

    public void setPoolFeedMap(Map<String, PoolsFeed> poolFeedMap) {
        this.poolFeedMap = poolFeedMap;
    }

    public Map<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    public void updateMemoryUsage(long init, long used, long committed, long max) {
        memoryUsage.add(new MemoryUsage(init, used, committed, max));
        if (memoryUsage.size() > 100) {
            memoryUsage.remove();
        }
    }

    public MemoryUsage getLastMemoryUsage() {
        return memoryUsage.size() > 0 ? memoryUsage.getLast() : null;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public JMXConnector getConnector() {
        return connector;

    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnector(JMXConnector connector) {
        this.connector = connector;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (connected ? 1231 : 1237);
        result = prime * result + ((connector == null) ? 0 : connector.hashCode());
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
        ServerStataus other = (ServerStataus) obj;
        if (connected != other.connected)
            return false;
        if (connector == null) {
            if (other.connector != null)
                return false;
        } else if (!connector.equals(other.connector))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerStataus [connected=");
        builder.append(connected);
        builder.append(", connector=");
        builder.append(connector);
        builder.append(", serverConfig=");
        builder.append(serverConfig);
        builder.append("]");
        return builder.toString();
    }

}
