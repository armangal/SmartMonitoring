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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMXUtils {

    private static Logger logger = LoggerFactory.getLogger(JMXUtils.class);

    public static Integer getIntAtributeFromComposite(CompositeData cd, String name) {
        try {
            return Integer.valueOf(cd.get(name).toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1;
        }
    }

    public static Long getLongAtributeFromComposite(CompositeData cd, String name) {
        try {
            return Long.valueOf(cd.get(name).toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1L;
        }
    }

    /**
     * get special time from composite in a format of mmDDHHMMSS
     * 
     * @param cd
     * @param name
     * @return
     */
    public static Long getLongTimeFromComposite(CompositeData cd, String name) {
        try {
            long time = Long.valueOf(cd.get(name).toString());

            return DateUtils.roundDate(roundDate(time));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1L;
        }
    }

    private static Date roundDate(long time) {
        Calendar date = new GregorianCalendar();
        date.setTime(new Date(time));
        int deltaMin = date.get(Calendar.SECOND) / 30;

        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.MINUTE, deltaMin);
        return date.getTime();
    }

    public static String getStringAtributeFromComposite(CompositeData cd, String name) {
        try {
            return cd.get(name).toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "Err";
        }
    }

    public static long getLong(MBeanServerConnection mbsc, ObjectName on, String name) {
        try {
            Object property = mbsc.getAttribute(on, name);
            return Long.valueOf(property.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1L;

        }
    }

    public static int getInt(MBeanServerConnection mbsc, ObjectName on, String name) {
        try {
            Object property = mbsc.getAttribute(on, name);
            return Integer.valueOf(property.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1;

        }
    }

    public static String getString(MBeanServerConnection mbsc, ObjectName on, String name) {
        try {
            Object property = mbsc.getAttribute(on, name);
            return property != null ? property.toString() : "";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "none";

        }
    }

    public static void main(String[] args) {
        long currentTimeMillis = 1365773690127L;
        System.currentTimeMillis();
        System.out.println(currentTimeMillis);
        System.out.println(new Date(currentTimeMillis));
        System.out.println(roundDate(currentTimeMillis));
    }
}
