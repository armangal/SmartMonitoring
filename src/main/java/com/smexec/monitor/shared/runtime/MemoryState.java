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
package com.smexec.monitor.shared.runtime;

import java.io.Serializable;

public class MemoryState
    implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private long used;
    private long commited;
    private long max;
    private boolean heap;// heap or not heap part.

    public MemoryState() {}

    /**
     * Set values in KBytes
     * @param name
     * @param used
     * @param commited
     * @param max
     * @param heap
     */
    public MemoryState(String name, long used, long commited, long max, boolean heap) {
        super();
        this.name = name;
        this.used = used;
        this.commited = commited;
        this.max = max;
        this.heap = heap;
    }

    public String getName() {
        return name;
    }

    /**
     * user memory in KBytes
     * @return
     */
    public long getUsed() {
        return used;
    }

    public long getCommited() {
        return commited;
    }

    public long getMax() {
        return max;
    }

    public boolean isHeap() {
        return heap;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MS [")
               .append(name)
               .append(": us=")
               .append(used)
               .append(", co=")
               .append(commited)
               .append(", ma=")
               .append(max)
               .append(", he=")
               .append(heap)
               .append("]\n");
        return builder.toString();
    }

}
