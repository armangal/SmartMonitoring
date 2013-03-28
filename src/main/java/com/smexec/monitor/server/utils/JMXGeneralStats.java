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
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.google.inject.Inject;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.services.alert.AlertService;
import com.smexec.monitor.shared.Alert;
import com.smexec.monitor.shared.StringFormatter;
import com.smexec.monitor.shared.alert.AlertType;
import com.smexec.monitor.shared.runtime.GCHistory;
import com.smexec.monitor.shared.runtime.RuntimeInfo;
import com.sun.management.OperatingSystemMXBean;

/**
 * Using the java.lang.management API to monitor the memory usage and garbage collection statistics.
 */
@SuppressWarnings("restriction")
public class JMXGeneralStats {

    @Inject
    private AlertService alertService;

    public JMXGeneralStats() {}

    private List<GCHistory> getGcHistory(List<GarbageCollectorMXBean> gcmbeans) {
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = DATE_FORMAT.format(new Date());
        List<GCHistory> retList = new ArrayList<GCHistory>(0);
        for (GarbageCollectorMXBean gc : gcmbeans) {
            retList.add(new GCHistory(gc.getName(), gc.getCollectionCount(), gc.getCollectionTime(), gc.getMemoryPoolNames(), time));
        }
        return retList;
    }

    private String getMemoryState(List<MemoryPoolMXBean> pools) {
        try {
            StringBuilder sb = new StringBuilder();
            for (MemoryPoolMXBean p : pools) {
                sb.append("  [" + p.getName() + ":");
                MemoryUsage u = p.getUsage();
                sb.append(" Used=" + StringFormatter.formatBytes(u.getUsed()));
                sb.append(" Committed=" + StringFormatter.formatBytes(u.getCommitted()));
                sb.append("]\n");
            }

            return sb.toString();
        } catch (Throwable e) {
            return e.getMessage();
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
    public void getMemoryStats(ServerStataus serverStataus)
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

        String memoryState = getMemoryState(pools);

        serverStataus.setUptime(rmbean.getUptime());

        com.smexec.monitor.shared.runtime.MemoryUsage mu = serverStataus.updateMemoryUsage(heapMemoryUsage.getInit(),
                                                                                   heapMemoryUsage.getUsed(),
                                                                                   heapMemoryUsage.getCommitted(),
                                                                                   heapMemoryUsage.getMax(),
                                                                                   memoryState);

        if (mu.getPercentage() > 90d) {
            Alert alert = new Alert("Memory Usage Alert, load is:" + DECIMAL_FORMAT.format(mu.getPercentage()) + "%",
                                    serverStataus.getServerConfig().getServerCode(),
                                    DATE_FORMAT.format(new Date()),
                                    AlertType.MEMORY);
            alertService.addAlert(alert);
        }

        List<GCHistory> gcHistoryList = getGcHistory(gcmbeans);
        for (GCHistory gch : gcHistoryList) {
            serverStataus.updateGCHistory(gch);
        }

        OperatingSystemMXBean operatingSystemMXBean = newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

        double load = serverStataus.updateCPUutilization(operatingSystemMXBean.getProcessCpuTime(),
                                                         operatingSystemMXBean.getAvailableProcessors(),
                                                         System.nanoTime(), operatingSystemMXBean.getSystemLoadAverage());

        if (load > 90d) {
            Alert alert = new Alert("CPU Alert, load is:" + DECIMAL_FORMAT.format(load),
                                    serverStataus.getServerConfig().getServerCode(),
                                    DATE_FORMAT.format(new Date()),
                                    AlertType.CPU);
            alertService.addAlert(alert);
        }

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
