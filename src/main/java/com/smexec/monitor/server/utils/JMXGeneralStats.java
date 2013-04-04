/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smexec.monitor.server.utils;

import static java.lang.management.ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE;
import static java.lang.management.ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE;
import static java.lang.management.ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.RUNTIME_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.dao.entities.ServerStatEntity;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.services.alert.AlertService;
import com.smexec.monitor.server.services.persistence.IPersistenceService;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.alert.AlertType;
import com.smexec.monitor.shared.runtime.GCHistory;
import com.smexec.monitor.shared.runtime.MemoryState;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.sun.management.OperatingSystemMXBean;

/**
 * Using the java.lang.management API to monitor the memory usage and garbage collection statistics.
 */
@SuppressWarnings("restriction")
public class JMXGeneralStats {

    private static Logger logger = LoggerFactory.getLogger("JMXGeneralStats");

    @Inject
    private AlertService alertService;
    @Inject
    private IPersistenceService persistenceService;

    public JMXGeneralStats() {}

    private List<GCHistory> getGcHistory(List<GarbageCollectorMXBean> gcmbeans) {
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = DATE_FORMAT.format(new Date());
        List<GCHistory> retList = new ArrayList<GCHistory>(0);
        for (GarbageCollectorMXBean gc : gcmbeans) {
            String name = gc.getName() + "," + Arrays.toString(gc.getMemoryPoolNames());
            retList.add(new GCHistory(name, gc.getCollectionCount(), gc.getCollectionTime(), time));
        }
        return retList;
    }

    private LinkedList<MemoryState> getMemoryState(List<MemoryPoolMXBean> pools) {
        LinkedList<MemoryState> list = new LinkedList<MemoryState>();
        long div = 1024 * 1024;
        try {
            for (MemoryPoolMXBean p : pools) {
                MemoryUsage u = p.getUsage();
                list.add(new MemoryState(p.getName(), u.getUsed() / div, u.getCommitted() / div, u.getMax() / div, p.getType().equals(MemoryType.HEAP)));
            }

            return list;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            return list;
        }
    }

    /**
     * gets the memory and cpu stats
     * 
     * @param serverStataus
     * @throws MBeanException
     * @throws AttributeNotFoundException
     * @throws InstanceNotFoundException
     * @throws ReflectionException
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    public void getMemoryStats(ServerStataus serverStataus, Date executionDate)
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException, MalformedObjectNameException {

        final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.##");

        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        MBeanServerConnection mbsc = serverStataus.getConnector().getMBeanServerConnection();
        RuntimeMXBean rmbean = newPlatformMXBeanProxy(mbsc, RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
        MemoryMXBean mxBean = newPlatformMXBeanProxy(mbsc, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);

        MemoryUsage heapMemoryUsage = mxBean.getHeapMemoryUsage();

        ObjectName poolName = null;
        ObjectName gcName = null;
        List<GarbageCollectorMXBean> gcmbeans = new ArrayList<GarbageCollectorMXBean>();
        List<MemoryPoolMXBean> pools = new ArrayList<MemoryPoolMXBean>();

        try {
            poolName = new ObjectName(MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",*");
            gcName = new ObjectName(GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*");
        } catch (MalformedObjectNameException e) {
            // should not reach here
            assert (false);
        }

        Set<ObjectName> mbeans = mbsc.queryNames(poolName, null);
        if (mbeans != null) {
            Iterator<ObjectName> iterator = mbeans.iterator();
            while (iterator.hasNext()) {
                ObjectName objName = iterator.next();
                MemoryPoolMXBean p = newPlatformMXBeanProxy(mbsc, objName.getCanonicalName(), MemoryPoolMXBean.class);
                pools.add(p);
            }
        }

        mbeans = mbsc.queryNames(gcName, null);
        if (mbeans != null) {
            Iterator<ObjectName> iterator = mbeans.iterator();
            while (iterator.hasNext()) {
                ObjectName objName = iterator.next();
                GarbageCollectorMXBean gc = newPlatformMXBeanProxy(mbsc, objName.getCanonicalName(), GarbageCollectorMXBean.class);
                gcmbeans.add(gc);
            }
        }

        LinkedList<MemoryState> memoryState = getMemoryState(pools);

        serverStataus.setUptime(rmbean.getUptime());

        com.smexec.monitor.shared.runtime.MemoryUsage mu = serverStataus.updateMemoryUsage(heapMemoryUsage.getInit(),
                                                                                           heapMemoryUsage.getUsed(),
                                                                                           heapMemoryUsage.getCommitted(),
                                                                                           heapMemoryUsage.getMax(),
                                                                                           memoryState);
        // check the groups settings
        if (mu.getPercentage() > serverStataus.getServerGroup().getMemoryUsage()) {
            Alert alert = new Alert("Memory Usage Alert:" + DECIMAL_FORMAT.format(mu.getPercentage()) + "%",
                                    serverStataus.getServerConfig().getServerCode(),
                                    serverStataus.getServerConfig().getName(),
                                    DATE_FORMAT.format(new Date()),
                                    AlertType.MEMORY);
            alertService.addAlert(alert, serverStataus);
        }

        List<GCHistory> gcHistoryList = getGcHistory(gcmbeans);
        for (GCHistory gch : gcHistoryList) {
            serverStataus.updateGCHistory(gch);
        }

        OperatingSystemMXBean operatingSystemMXBean = newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

        double load = serverStataus.updateCPUutilization(operatingSystemMXBean.getProcessCpuTime(),
                                                         operatingSystemMXBean.getAvailableProcessors(),
                                                         System.nanoTime(),
                                                         operatingSystemMXBean.getSystemLoadAverage());

        // check the groups settings
        if (load > serverStataus.getServerGroup().getCpuLoad()) {
            Alert alert = new Alert("CPU Alert:" + DECIMAL_FORMAT.format(load) + "%",
                                    serverStataus.getServerConfig().getServerCode(),
                                    serverStataus.getServerConfig().getName(),
                                    DATE_FORMAT.format(new Date()),
                                    AlertType.CPU);
            alertService.addAlert(alert, serverStataus);
        }

        persistenceService.saveServerStat(new ServerStatEntity(executionDate.getTime(),
                                                               serverStataus.getUpTime(),
                                                               serverStataus.getServerConfig().getServerCode(),
                                                               serverStataus.getServerConfig().getName(),
                                                               serverStataus.getCpuUtilization().getLastPercent().getUsage(),
                                                               serverStataus.getCpuUtilization().getLastPercent().getSystemLoadAverage(),
                                                               serverStataus.getLastMemoryUsage().getCommitted(),
                                                               serverStataus.getLastMemoryUsage().getInit(),
                                                               serverStataus.getLastMemoryUsage().getMax(),
                                                               serverStataus.getLastMemoryUsage().getUsed(),
                                                               serverStataus.getMemoryState(),
                                                               serverStataus.isConnected()));
    }

    public RuntimeInfo getRuntimeInfo(ServerStataus serverStataus)
        throws IOException {
        MBeanServerConnection mbsc = serverStataus.getConnector().getMBeanServerConnection();
        RuntimeMXBean rmbean = newPlatformMXBeanProxy(mbsc, RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);

        OperatingSystemMXBean osbean = newPlatformMXBeanProxy(mbsc, OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

        HashMap<String, String> map = new HashMap<String, String>(rmbean.getSystemProperties());

        RuntimeInfo rti = new RuntimeInfo(rmbean.getBootClassPath(),
                                          rmbean.getClassPath(),
                                          rmbean.getInputArguments(),
                                          rmbean.getLibraryPath(),
                                          rmbean.getName(),
                                          map,
                                          osbean.getAvailableProcessors(),
                                          osbean.getSystemLoadAverage());
        return rti;
    }

}
