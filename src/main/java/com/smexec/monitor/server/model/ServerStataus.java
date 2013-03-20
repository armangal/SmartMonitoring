package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.management.remote.JMXConnector;

import com.smexec.monitor.shared.CPUUtilization;
import com.smexec.monitor.shared.GCHistory;
import com.smexec.monitor.shared.MemoryUsage;
import com.smexec.monitor.shared.PoolsFeed;
import com.smexec.monitor.shared.StringFormatter;

/**
 * Class represents a current state of connected server with some historical data about memory, GC cycles and
 * current thread pools stats<br>
 * <b>It's server side in-memory state, not for client usage.</b>
 * 
 * @author armang
 */
public class ServerStataus {

    private boolean connected = false;
    private boolean firstTimeAccess = true;
    private JMXConnector connector;
    private ServerConfig serverConfig;

    private LinkedList<MemoryUsage> memoryUsage = new LinkedList<MemoryUsage>();

    /**
     * Map of available GCs with a map of last 100 GC cycles
     */
    private HashMap<String, LinkedHashMap<Long, GCHistory>> gcHistoryMap = new HashMap<String, LinkedHashMap<Long, GCHistory>>(0);

    /**
     * Key - thread pool name Value - statistics about the thread pool, current state and historical data
     */
    private Map<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>(0);

    private long upTime;

    /**
     * stores locally CPU utilization
     */
    private CPUUtilization cpuUtilization = new CPUUtilization();

    public ServerStataus(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void setPoolFeedMap(Map<String, PoolsFeed> poolFeedMap) {
        this.poolFeedMap = poolFeedMap;
    }

    public Map<String, PoolsFeed> getPoolFeedMap() {
        return poolFeedMap;
    }

    @SuppressWarnings("serial")
    public void updateGCHistory(GCHistory gcHistory) {
        if (!gcHistoryMap.containsKey(gcHistory.getCollectorName())) {
            LinkedHashMap<Long, GCHistory> cycles = new LinkedHashMap<Long, GCHistory>() {

                @Override
                protected boolean removeEldestEntry(java.util.Map.Entry<Long, GCHistory> eldest) {
                    return size() > 100;
                }
            };

            cycles.put(gcHistory.getCollectionCount(), gcHistory);
            gcHistoryMap.put(gcHistory.getCollectorName(), cycles);

        } else {

            LinkedHashMap<Long, GCHistory> cycles = gcHistoryMap.get(gcHistory.getCollectorName());
            if (!cycles.containsKey(gcHistory.getCollectionCount())) {
                if (cycles.size() > 0) {
                    LinkedList<Long> list = new LinkedList<Long>(cycles.keySet());
                    Collections.sort(list);
                    GCHistory gcH = cycles.get(list.getLast());
                    long lastColleactionTime = (gcHistory.getCollectionTime() - gcH.getCollectionTime())
                                               / (gcHistory.getCollectionCount() - gcH.getCollectionCount());
                    gcHistory.setLastColleactionTime(lastColleactionTime);
                }
                cycles.put(gcHistory.getCollectionCount(), gcHistory);
            }
        }

    }

    public void updateMemoryUsage(long init, long used, long committed, long max, String memoryState) {
        memoryUsage.add(new MemoryUsage(init, used, committed, max, memoryState));
        if (memoryUsage.size() > 100) {
            memoryUsage.remove();
        }
    }

    public void updateCPUutilization(final long lastMeasurementAfter, final long lastMeasureTimeAfter) {
        cpuUtilization.evolve(lastMeasurementAfter, lastMeasureTimeAfter);
    }

    /**
     * will return last measurements for each pool
     * @return
     */
    public ArrayList<GCHistory> getLastGCHistory() {
        ArrayList<GCHistory> list = new ArrayList<GCHistory>(0);
        for (LinkedHashMap<Long, GCHistory> poolGcStats : gcHistoryMap.values()) {
            int size = poolGcStats.keySet().size();
            Object[] array = poolGcStats.keySet().toArray();
            list.add(poolGcStats.get(array[size - 1]));
        }
        return list;
    }

    public String getGCHistory() {
        StringBuilder sb = new StringBuilder();
        for (String poolName : gcHistoryMap.keySet()) {
            sb.append("Collector Name:").append(poolName).append("\n");
            LinkedHashMap<Long, GCHistory> map = gcHistoryMap.get(poolName);
            for (GCHistory gch : map.values()) {
                sb.append("[Count=" + gch.getCollectionCount());
                sb.append(" GCTimeTotal=" + StringFormatter.formatMillis(gch.getCollectionTime()));
                sb.append(" LastCollTime=" + StringFormatter.formatMillis(gch.getLastColleactionTime()));
                sb.append("]\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public MemoryUsage getLastMemoryUsage() {
        return memoryUsage.size() > 0 ? memoryUsage.getLast() : null;
    }

    public LinkedList<MemoryUsage> getMemoryUsage() {
        return memoryUsage;
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

    /**
     * true if we've never got any JMX stats from this server and we might get some historical data as well.
     * 
     * @return
     */
    public boolean isFirstTimeAccess() {
        return firstTimeAccess;
    }

    public void setFirstTimeAccess(boolean firstTimeAccess) {
        this.firstTimeAccess = firstTimeAccess;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUptime(long uptime) {
        this.upTime = uptime;
    }

    public CPUUtilization getCpuUtilization() {
        return cpuUtilization;
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
