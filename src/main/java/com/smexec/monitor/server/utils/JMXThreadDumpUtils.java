package com.smexec.monitor.server.utils;

import static java.lang.management.ManagementFactory.THREAD_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.MBeanServerConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.model.ServerStataus;

public class JMXThreadDumpUtils {

    private static Logger logger = LoggerFactory.getLogger(JMXThreadDumpUtils.class);

    public <SS extends ServerStataus> String getThreadDump(SS ss) {
        StringBuilder response = new StringBuilder();

        try {
            MBeanServerConnection mbsc = ss.getConnector().getMBeanServerConnection();
            ThreadMXBean threadMXBean = newPlatformMXBeanProxy(mbsc, THREAD_MXBEAN_NAME, ThreadMXBean.class);
            ThreadInfo[] dump = threadMXBean.dumpAllThreads(true, true);

            response.append("Dump of " + dump.length + " thread at server:" + ss.getServerConfig().getServerCode() + ", time:"
                            + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(System.currentTimeMillis())) + "\n\n");

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

        return response.toString();
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
