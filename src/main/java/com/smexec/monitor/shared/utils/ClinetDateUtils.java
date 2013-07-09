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
package com.smexec.monitor.shared.utils;

import java.util.Date;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;

public class ClinetDateUtils {

    private static DateTimeFormat formatX = DateTimeFormat.getFormat("yyyyMMddHHmmss");
    private static DateTimeFormat formatY = DateTimeFormat.getFormat("yyyy");

    /**
     * @param dateTime - 709115522 = 09/07 11:55:22
     * @return
     */
    public static Date convertFromShortDate(long dateTime) {
        try {
            int year = Integer.valueOf(formatY.format(new Date()));
            int month = (int) (dateTime % 10000000000L / 100000000);
            return formatX.parse("" + year + "" + (month > 9 ? month : "0" + month) + "" + dateTime % 100000000L);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            return new Date();
        }
    }

    public static void main(String[] args) {
        System.out.println(convertFromShortDate(709115522L));
    }
}
