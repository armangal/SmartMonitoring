package com.smexec.monitor.server.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private static int STEP = 20;

    public static long roundDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int sec = calendar.get(Calendar.SECOND);
        sec = roundSec(sec);
        // sec = (sec > 0 && sec < 20) ? 00 : (sec >= 20 && sec < 40) ? 20 : (sec >= 40) ? 40 : 0;
        long time = ((calendar.get(Calendar.MONTH) + 1) * 100000000) + (calendar.get(Calendar.DAY_OF_MONTH) * 1000000)
                    + (calendar.get(Calendar.HOUR_OF_DAY) * 10000) + (calendar.get(Calendar.MINUTE) * 100) + sec;

        return time;

    }

    private static int roundSec(int sec) {
        for (int i = 0; i * STEP < 60; i++) {
            if (sec >= i && sec < STEP * (i + 1)) {
                sec = i * STEP;
                break;
            }
        }
        return sec;
    }

    
    public void test() {
        for (int i = 0; i < 60; i++) {
            System.out.println("i=" + i + ", r=" + roundSec(i));
        }
        
        System.out.println(new Date(1364300594874L +70926603L ));
    }
}
