package com.smexec.monitor.shared;


public class StringFormatter {

    public static String formatMillis(long ms) {
        return String.format("%.4fsec", ms / (double) 1000);
    }

    public static String formatMillisShort(long ms) {
        return String.format("%.2fsec", ms / (double) 1000);
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
