package com.smexec.monitor.server.utils;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

public class JMXUtils {

    public static Integer getIntAtributeFromComposite(CompositeData cd, String name) {
        try {
            return Integer.valueOf(cd.get(name).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Long getLongAtributeFromComposite(CompositeData cd, String name) {
        try {
            return Long.valueOf(cd.get(name).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public static String getStringAtributeFromComposite(CompositeData cd, String name) {
        try {
            return cd.get(name).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Err";
        }
    }

    public static long getLong(MBeanServerConnection mbsc, ObjectName on, String name) {
        try {
            Object property = mbsc.getAttribute(on, name);
            return Long.valueOf(property.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;

        }
    }
}