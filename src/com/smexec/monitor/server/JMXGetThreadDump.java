package com.smexec.monitor.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import com.smexec.monitor.server.model.ConnectedServersState;
import com.smexec.monitor.server.model.ServerStataus;

public class JMXGetThreadDump {

    public static String getThreadDump(Integer serverCode) {
        StringBuilder response = new StringBuilder();

        ServerStataus ss = ConnectedServersState.getMap().get(serverCode);
        if (ss == null || !ss.isConnected()) {
            response = new StringBuilder("Server:" + serverCode + " not found or not connected.");
        } else {
            try {
                MBeanServerConnection mbsc = ss.getConnector().getMBeanServerConnection();
                ObjectName threading = new ObjectName("java.lang:type=Threading");
                CompositeData[] invoke = (CompositeData[]) mbsc.invoke(threading, "dumpAllThreads", new Object[] {true, true}, new String[] {"boolean",
                                                                                                                                             "boolean"});

                response.append("Dump of " + invoke.length + " thread at server:" + serverCode + ", time:"
                                + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(System.currentTimeMillis())) + "\n\n");

                for (CompositeData cd : invoke) {

                    response.append("\"" + cd.get("threadName") + "\"  tid=" + cd.get("threadId") + " " + cd.get("threadState") + "\n");

                    response.append("    native=" + cd.get("inNative") + ", suspended=" + cd.get("suspended") + ", block=" + cd.get("blockedCount") + ", wait="
                                    + cd.get("waitedCount") + ", waitedTime=" + cd.get("waitedTime") + "\n");

                    response.append("    lock=" + cd.get("lockName") + " owned by " + cd.get("lockOwnerName") + " (" + cd.get("lockOwnerId") + ") cpuTime="
                                    + getThreadCpuTime(mbsc, threading, Long.valueOf(cd.get("threadId").toString())) + ", userTime="
                                    + getThreadUserTime(mbsc, threading, Long.valueOf(cd.get("threadId").toString())) + "\n");

                    for (CompositeData st : (CompositeData[]) cd.get("stackTrace")) {
                        response.append(extractStack(st)).append("\n");
                    }
                    response.append("\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
                response = new StringBuilder(e.getMessage());
            }

        }
        return response.toString();
    }

    private static long getThreadCpuTime(MBeanServerConnection mbsc, ObjectName threading, long threadId) {
        try {
            return Long.valueOf(mbsc.invoke(threading, "getThreadCpuTime", new Object[] {threadId}, new String[] {"long"}).toString()).longValue() / 1000000L;
        } catch (Exception e) {
            return -1L;
        }
    }

    private static long getThreadUserTime(MBeanServerConnection mbsc, ObjectName threading, long threadId) {
        try {
            return Long.valueOf(mbsc.invoke(threading, "getThreadUserTime", new Object[] {threadId}, new String[] {"long"}).toString()).longValue() / 1000000L;
        } catch (Exception e) {
            return -1L;
        }
    }

    private static String extractStack(CompositeData st) {
        return st.get("className")
               + "."
               + st.get("methodName")
               + (Boolean.valueOf(st.get("nativeMethod").toString()) ? "(Native Method)"
                               : (st.get("fileName") != null && Integer.valueOf(st.get("lineNumber").toString()) >= 0 ? "(" + st.get("fileName") + ":"
                                                                                                                        + st.get("lineNumber") + ")"
                                               : (st.get("fileName") != null ? "(" + st.get("fileName") + ")" : "(Unknown Source)")));
    }
}
