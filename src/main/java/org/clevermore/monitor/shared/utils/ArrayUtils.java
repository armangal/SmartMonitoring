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
package org.clevermore.monitor.shared.utils;

public class ArrayUtils {

    /**
     * copy int array values and keeps only max in the new one
     * 
     * @param local
     * @param merged
     * @return
     */
    public static int[] copyArrayMax(int[] local, int[] merged) {
        if (local == null || local.length == 0) {
            local = new int[merged.length];
        } else {
            //create new instance in order not to break the original
            int[] dest = new int[local.length];
            System.arraycopy(local, 0, dest, 0, local.length);
            local = dest;
        }
        for (int i = 0; i < merged.length; i++) {
            local[i] = Math.max(local[i], merged[i]);
        }
        return local;
    }

}
