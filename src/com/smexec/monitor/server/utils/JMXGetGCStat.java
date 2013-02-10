package com.smexec.monitor.server.utils;

/*
 * %W% %E%
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Oracle or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * %W% %E%
 */

import static java.lang.management.ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE;
import static java.lang.management.ManagementFactory.MEMORY_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE;
import static java.lang.management.ManagementFactory.RUNTIME_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.smexec.monitor.shared.GCHistory;
import com.smexec.monitor.shared.StringFormatter;

/**
 * Example of using the java.lang.management API to monitor the memory usage and garbage collection
 * statistics.
 * 
 * @author Mandy Chung
 * @version %% %G%
 */
public class JMXGetGCStat {

    private RuntimeMXBean rmbean;
    private MemoryMXBean mmbean;
    private List<MemoryPoolMXBean> pools;
    private List<GarbageCollectorMXBean> gcmbeans;

    /**
     * Constructs a PrintGCStat object to monitor a remote JVM.
     */
    public JMXGetGCStat(MBeanServerConnection server)
        throws IOException {
        // Create the platform mxbean proxies
        this.rmbean = newPlatformMXBeanProxy(server, RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
        this.mmbean = newPlatformMXBeanProxy(server, MEMORY_MXBEAN_NAME, MemoryMXBean.class);
        ObjectName poolName = null;
        ObjectName gcName = null;
        try {
            poolName = new ObjectName(MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",*");
            gcName = new ObjectName(GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*");
        } catch (MalformedObjectNameException e) {
            // should not reach here
            assert (false);
        }

        Set mbeans = server.queryNames(poolName, null);
        if (mbeans != null) {
            pools = new ArrayList<MemoryPoolMXBean>();
            Iterator iterator = mbeans.iterator();
            while (iterator.hasNext()) {
                ObjectName objName = (ObjectName) iterator.next();
                MemoryPoolMXBean p = newPlatformMXBeanProxy(server, objName.getCanonicalName(), MemoryPoolMXBean.class);
                pools.add(p);
            }
        }

        mbeans = server.queryNames(gcName, null);
        if (mbeans != null) {
            gcmbeans = new ArrayList<GarbageCollectorMXBean>();
            Iterator iterator = mbeans.iterator();
            while (iterator.hasNext()) {
                ObjectName objName = (ObjectName) iterator.next();
                GarbageCollectorMXBean gc = newPlatformMXBeanProxy(server, objName.getCanonicalName(), GarbageCollectorMXBean.class);
                gcmbeans.add(gc);
            }
        }
    }

    public List<GCHistory> getGcHistory() {
        List<GCHistory> retList = new ArrayList<GCHistory>(0);
        for (GarbageCollectorMXBean gc : gcmbeans) {
            retList.add(new GCHistory(gc.getName(), gc.getCollectionCount(), gc.getCollectionTime(), gc.getMemoryPoolNames()));
        }
        return retList;
    }

    public long getUpTime() {
        return rmbean.getUptime();
    }

    public String getMemoryState() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Uptime: " + StringFormatter.formatMillis(rmbean.getUptime()));

            sb.append("\n");
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
     * Prints the verbose GC log to System.out to list the memory usage of all memory pools as well as the GC
     * statistics.
     */
    @Deprecated
    public String[] getVerboseGc() {
        String gcs = "";

        StringBuilder sb = new StringBuilder();
        sb.append("Uptime: " + StringFormatter.formatMillis(rmbean.getUptime()));

        for (GarbageCollectorMXBean gc : gcmbeans) {
            sb.append(" [" + gc.getName() + ": ");
            sb.append("Count=" + gc.getCollectionCount());
            sb.append(" GCTime=" + StringFormatter.formatMillis(gc.getCollectionTime()));
            sb.append("]");
            gcs += StringFormatter.formatMillisShort(gc.getCollectionTime()) + ":(" + gc.getCollectionCount() + ");";
        }
        sb.append("\n");
        for (MemoryPoolMXBean p : pools) {
            sb.append("  [" + p.getName() + ":");
            MemoryUsage u = p.getUsage();
            sb.append(" Used=" + StringFormatter.formatBytes(u.getUsed()));
            sb.append(" Committed=" + StringFormatter.formatBytes(u.getCommitted()));
            sb.append("]\n");
        }

        return new String[] {sb.toString(), gcs};
    }

}
