package com.smexec.monitor.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.management.remote.JMXConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.model.config.ServerConfig;
import com.smexec.monitor.server.utils.DateUtils;
import com.smexec.monitor.shared.StringFormatter;
import com.smexec.monitor.shared.alert.IAlertType;
import com.smexec.monitor.shared.runtime.CPUUtilization;
import com.smexec.monitor.shared.runtime.GCHistory;
import com.smexec.monitor.shared.runtime.MemoryUsage;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

/**
 * Class represents a current state of connected server with some historical data about memory, GC cycles and
 * current thread pools stats<br>
 * <b>It's server side in-memory state, not for client usage.</b>
 * 
 * @author armang
 */
public class ServerStataus {

    private final static Logger logger = LoggerFactory.getLogger("ServerStataus");

    private boolean connected = false;
    private boolean firstTimeAccess = true;
    private JMXConnector connector;
    private ServerConfig serverConfig;

    /**
     * Memory usage history stats
     */
    private LinkedList<MemoryUsage> memoryUsage = new LinkedList<MemoryUsage>();

    /**
     * Map of available GCs with a map of last XXX GC cycles
     */
    private HashMap<String, LinkedHashMap<Long, GCHistory>> gcHistoryMap = new HashMap<String, LinkedHashMap<Long, GCHistory>>(0);

    /**
     * Key - thread pool name Value - statistics about the thread pool, current state and historical data
     */
    private Map<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>(0);

    private long upTime;

    /**
     * in memory history thresholds
     */
    private final int systemHistoryToKeep;
    private final int gcHistoryToKeep;

    /**
     * stores locally CPU utilisation
     */
    private CPUUtilization cpuUtilization = new CPUUtilization();

    private Map<IAlertType, Long> lastAlertSent = new HashMap<IAlertType, Long>(0);

    /**
     * @param serverConfig
     * @param gcHistoryToKeep - how much chunks to keep in memory (aggregated each 20 sec)
     * @param systemHistoryToKeep - how much chunks to keep in memory (aggregated each 20 sec) (CPU & Memory)
     */
    public ServerStataus(final ServerConfig serverConfig, final int gcHistoryToKeep, final int systemHistoryToKeep) {
        this.serverConfig = serverConfig;
        this.systemHistoryToKeep = systemHistoryToKeep;
        this.gcHistoryToKeep = gcHistoryToKeep;
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
            // adding new collector
            LinkedHashMap<Long, GCHistory> cycles = new LinkedHashMap<Long, GCHistory>() {

                @Override
                protected boolean removeEldestEntry(java.util.Map.Entry<Long, GCHistory> eldest) {
                    return size() > gcHistoryToKeep;
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

    public MemoryUsage updateMemoryUsage(long init, long used, long committed, long max, String memoryState) {
        MemoryUsage mu = new MemoryUsage(init, used, committed, max, memoryState, DateUtils.roundDate(new Date()));
        memoryUsage.add(mu);
        if (memoryUsage.size() > systemHistoryToKeep) {
            memoryUsage.remove();
        }
        return mu;
    }

    public double updateCPUutilization(final long lastMeasurementAfter,
                                       final int availableProcessors,
                                       final long lastMeasureTimeAfter,
                                       final double systemLoadAverage) {
        return cpuUtilization.evolve(lastMeasurementAfter, availableProcessors, lastMeasureTimeAfter, systemLoadAverage, systemHistoryToKeep);
    }

    /**
     * will return last measurements for each pool
     * 
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

    /**
     * get the last 20 GC stats per each pool name
     * 
     * @return
     */
    public String getGCHistory() {
        StringBuilder sb = new StringBuilder();
        for (String poolName : gcHistoryMap.keySet()) {
            sb.append("Collector Name:").append(poolName).append("\n");
            LinkedHashMap<Long, GCHistory> map = gcHistoryMap.get(poolName);
            LinkedList<Long> list = new LinkedList<Long>(map.keySet());
            Collections.sort(list);
            for (int i = Math.max(0, list.size() - 20); i < list.size(); i++) {
                GCHistory gch = map.get(list.get(i));
                sb.append("[T:").append(gch.getTime()).append(" CNT=" + gch.getCollectionCount());
                sb.append(" TT=" + StringFormatter.formatMillis(gch.getCollectionTime()));
                sb.append(" LT=" + StringFormatter.formatMillis(gch.getLastColleactionTime()));
                sb.append("]\n");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }

    public MemoryUsage getLastMemoryUsage() {
        return memoryUsage.size() > 0 ? memoryUsage.getLast() : null;
    }

    /**
     * @param chunks = number of chunks to return (default is 100)
     * @return - memory usage chunks, to be used to draw chart
     */
    public LinkedList<MemoryUsage> getMemoryUsage(int chunks) {
        LinkedList<MemoryUsage> ret = new LinkedList<MemoryUsage>();
        // get last X chunks
        for (int i = Math.max(0, memoryUsage.size() - chunks); i < memoryUsage.size(); i++) {
            ret.add(memoryUsage.get(i));
        }
        return ret;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public JMXConnector getConnector() {
        return connector;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnector(JMXConnector connector) {
        this.connector = connector;
        if (connector != null) {
            connected = true;
        }
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

    /**
     * the method is called when JMX is disconnected
     */
    public void resetOnDisconnect() {
        this.firstTimeAccess = true;
        this.connected = false;
        this.connector = null;
        this.gcHistoryMap.clear();
        this.memoryUsage.clear();
    }

    public CPUUtilization getCpuUtilization() {
        return cpuUtilization;
    }

    /**
     * returns true if alert of a give type can be sent by mail
     * 
     * @param alertType
     * @return
     */
    public boolean canSendAlert(IAlertType alertType) {
        if (!lastAlertSent.containsKey(alertType)) {
            lastAlertSent.put(alertType, System.currentTimeMillis());
            logger.debug("can send alter:{}, first time", alertType);
            return true;
        }
        Long lastTime = lastAlertSent.get(alertType);
        if ((System.currentTimeMillis() - lastTime) > alertType.getAlertFrequency()) {
            // can send
            logger.debug("Can send alert:{}, last:{}, current:{}", new Object[] {alertType, lastTime, System.currentTimeMillis()});
            lastAlertSent.put(alertType, System.currentTimeMillis());
            return true;
        }
        logger.debug("Can NOT send alert:{}, last:{}, current:{}", new Object[] {alertType, lastTime, System.currentTimeMillis()});
        return false;
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
