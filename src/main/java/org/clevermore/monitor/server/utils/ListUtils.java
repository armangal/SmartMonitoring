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
package org.clevermore.monitor.server.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.clevermore.monitor.shared.AbstractMergeableChunkStats;
import org.junit.Test;


public class ListUtils {

    /**
     * returns blur list with maximum items as provided in the parameter
     * 
     * @param list - original (big) list
     * @param maxItems - max items to have in the returned collection
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractMergeableChunkStats, L extends List<T>> L blur(final L list, final int maxItems, final L resultList) {
        if (list == null || list.size() <= maxItems) {
            return list;
        }
        final double step = list.size() / (double) maxItems;
        double index = step;

        int divider = 0;
        AbstractMergeableChunkStats aggItem = null;
        for (int i = 0; i < list.size(); i++) {
            if (i == (int) index) {
                // cut
                aggItem.divide(divider);
                resultList.add((T) aggItem);
                aggItem = null;
                divider = 0;
                index += step;
            }

            // aggregate
            if (aggItem == null) {
                aggItem = list.get(i).copyMe();
            } else {
                aggItem.aggregate(list.get(i));
            }
            divider++;
        }
        if (resultList.size() < maxItems && divider > 0 && aggItem != null) {
            aggItem.divide(divider);
            resultList.add((T) aggItem);

        }
        return resultList;
    }

    private static class A
        extends AbstractMergeableChunkStats {

        private static final long serialVersionUID = 1L;
        int a;
        long b;
        int[] x;

        public A(int a, long b, int[] x) {
            super();
            this.a = a;
            this.b = b;
            this.x = x;
        }

        @Override
        public AbstractMergeableChunkStats copyMe() {
            return new A(a, b, x);
        }

        @Override
        public void divide(int elements) {

        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("\nA [a=").append(a).append(", b=").append(b).append(", x=").append(Arrays.toString(x)).append("]");
            return builder.toString();
        }

        @Override
        public void aggregate(AbstractMergeableChunkStats value) {
            A from = (A) value;
            this.a = Math.max(a, from.a);
            this.b = Math.max(b, from.b);
            this.x = copyArray(x, from.x);
        }

        private int[] copyArray(int[] local, int[] merged) {
            if (local == null || local.length == 0) {
                local = new int[merged.length];
            } else {
                local = Arrays.copyOf(local, local.length);
            }
            for (int i = 0; i < merged.length; i++) {
                local[i] = Math.max(local[i], merged[i]);
            }
            return local;
        }

    }
    public static class UnitTest {

        @Test
        public void test() {
            List<A> list = new LinkedList<A>();

            Random r = new Random();
            list.add(new A(r.nextInt(1000), r.nextInt(1000), new int[] {r.nextInt(1000), r.nextInt(1000)}));
            list.add(new A(r.nextInt(1000), r.nextInt(1000), new int[] {r.nextInt(1000), r.nextInt(1000)}));
            list.add(new A(r.nextInt(1000), r.nextInt(1000), new int[] {r.nextInt(1000), r.nextInt(1000)}));
            list.add(new A(r.nextInt(1000), r.nextInt(1000), new int[] {r.nextInt(1000), r.nextInt(1000)}));
            list.add(new A(r.nextInt(1000), r.nextInt(1000), new int[] {r.nextInt(1000), r.nextInt(1000)}));

            System.out.println(list);
            List<A> blur = blur(list, 2, new LinkedList<A>());
            System.out.println("Size:" + blur.size() + ", " + blur);
        }
    }
}
