package com.smexec.monitor.client.utils;

import com.google.gwt.i18n.client.NumberFormat;

public class ClientStringFormatter {

    public static String formatMillis(long ms) {
        return NumberFormat.getFormat("#,##0.0###").format(ms / (double) 1000);
    }

    public static String formatMillisShort(long ms) {
        return NumberFormat.getFormat("#,##0.0#").format(ms / (double) 1000);
    }

    public static String formatMilisecondsToHours(long ms) {
        int seconds = (int) (ms / 1000) % 60;
        int minutes = (int) ((ms / (1000 * 60)) % 60);
        int hours = (int) ((ms / (1000 * 60 * 60)) % 24);
        return hours + "h, " + minutes + "m, " + seconds + "sec";

    }

    public static String formatMillisShort(double ms) {
        return NumberFormat.getFormat("#,##0.0#").format(ms);
    }

    public static String formatBytes(long bytes) {
        long kb = bytes;
        if (bytes > 0) {
            kb = bytes / 1024;
        }
        return kb + "K";
    }

    public static String formatMBytes(long bytes) {
        long kb = bytes;
        if (bytes > 0) {
            kb = bytes / 1024 / 1024;
        }
        return String.valueOf(kb);
    }

}
