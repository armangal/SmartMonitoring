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
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(time));
            int hours = calendar.get(Calendar.HOUR_OF_DAY);

            int minutes = calendar.get(Calendar.MINUTE);

            int month = calendar.get(Calendar.MONTH) + 1;

            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            return Long.valueOf((month * 1000000) + (dayOfMonth * 10000) + (hours * 100) + minutes);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1L;
        }
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
}
