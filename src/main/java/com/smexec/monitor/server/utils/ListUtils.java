package com.smexec.monitor.server.utils;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class ListUtils {

    /**
     * returns blur list with maximum items as provided in the parameter
     * 
     * @param list - original (big) list
     * @param maxItems - max items to have in the returned collection
     * @return
     */
    public static <L extends List<T>, T> L blur(final L list, final int maxItems, final L resultList) {
        if (list == null || list.size() <= maxItems) {
            return list;
        }
        final double step = list.size() / (double) maxItems;
        double index = 0;
        int added = 0;
        while (added < maxItems && index < list.size()) {
            resultList.add(list.get((int) index));
            added++;
            index += step;
        }
        return resultList;
    }

    public static class UnitTest {

        @Test
        public void test() {
            for (int j = 1; j < 100; j++) {
                List<Integer> list = new LinkedList<Integer>();
                for (int i = 0; i < 17 * j; i++) {
                    list.add(i);
                }

                List<Integer> blur = blur(list, 100, new LinkedList<Integer>());
                assertEquals(blur.size(), Math.min(list.size(), 100));
                // System.out.println("Size:" + blur.size() + ", " + blur);
            }
        }
    }
}
