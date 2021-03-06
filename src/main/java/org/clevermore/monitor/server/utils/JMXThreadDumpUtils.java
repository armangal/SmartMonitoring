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
package org.clevermore.monitor.server.utils;

import static java.lang.management.ManagementFactory.THREAD_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.management.MBeanServerConnection;

import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.shared.runtime.ThreadDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMXThreadDumpUtils {

    private static Logger logger = LoggerFactory.getLogger(JMXThreadDumpUtils.class);

    public <SS extends ServerStatus> ThreadDump getThreadDump(SS ss) {
        StringBuilder response = new StringBuilder();

        LinkedList<org.clevermore.monitor.shared.runtime.ThreadInfo> threads = new LinkedList<org.clevermore.monitor.shared.runtime.ThreadInfo>();
        try {
            MBeanServerConnection mbsc = ss.getConnector().getMBeanServerConnection();
            ThreadMXBean threadMXBean = newPlatformMXBeanProxy(mbsc, THREAD_MXBEAN_NAME, ThreadMXBean.class);
            ThreadInfo[] dump = threadMXBean.dumpAllThreads(true, true);

            response.append("Dump of " + dump.length + " thread at server:" + ss.getServerConfig().getServerCode() + ", time:"
                            + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(System.currentTimeMillis())) + "\n\n");

            for (ThreadInfo ti : dump) {

                threads.add(new org.clevermore.monitor.shared.runtime.ThreadInfo(ti.getThreadName(), ti.getThreadId(), ti.getThreadState().name()));
            }

            for (ThreadInfo ti : dump) {

                response.append("\"" + ti.getThreadName() + "\"  tid=" + ti.getThreadId() + " " + ti.getThreadState() + "\n");

                response.append("    native=" + ti.isInNative() + ", suspended=" + ti.isSuspended() + ", block=" + ti.getBlockedCount() + ", wait="
                                + ti.getWaitedCount() + ", waitedTime=" + ti.getWaitedTime() + "\n");

                response.append("    lock=" + ti.getLockName() + " owned by " + ti.getLockOwnerName() + " (" + ti.getLockOwnerId() + ") cpuTime="
                                + threadMXBean.getCurrentThreadCpuTime() + ", userTime=" + threadMXBean.getCurrentThreadUserTime() + "\n");

                for (StackTraceElement ste : ti.getStackTrace()) {
                    response.append(extractStack(ste)).append("\n");
                }
                response.append("\n");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response = new StringBuilder(e.getMessage());
        }

        ThreadDump td = new ThreadDump(response.toString(), threads);
        return td;
    }

    private static String extractStack(StackTraceElement ste) {
        return ste.getClassName()
               + "."
               + ste.getMethodName()
               + (ste.isNativeMethod() ? "(Native Method)" : (ste.getFileName() != null && ste.getLineNumber() >= 0 ? "(" + ste.getFileName() + ":"
                                                                                                                      + ste.getLineNumber() + ")"
                               : (ste.getFileName() != null ? "(" + ste.getFileName() + ")" : "(Unknown Source)")));
    }
}
