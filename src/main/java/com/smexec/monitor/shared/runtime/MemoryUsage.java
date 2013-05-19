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
import java.util.LinkedList;

import com.smexec.monitor.shared.AbstractMergeableChunkStats;

/**
 * represents one memory measurement
 * 
 * @author armang
 */
public class MemoryUsage
    extends AbstractMergeableChunkStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long committed;
    private long init;
    private long max;
    private long used;
    private LinkedList<MemoryState> memoryState;

    public MemoryUsage() {}

    public MemoryUsage(long init, long used, long committed, long max, long time, LinkedList<MemoryState> memoryState) {
        super(time, time);
        this.committed = committed;
        this.init = init;
        this.max = max;
        this.used = used;
        this.memoryState = memoryState;
    }

    @Override
    public AbstractMergeableChunkStats copyMe() {
        return new MemoryUsage(init, used, committed, max, getFullEndTime(), memoryState);
    }

    public void aggregate(AbstractMergeableChunkStats v) {
        MemoryUsage value = (MemoryUsage) v;
        this.committed = Math.max(committed, value.committed);
        this.init = Math.max(init, value.init);
        this.max = Math.max(max, value.max);
        this.used = Math.max(used, value.used);
        this.memoryState = aggMemState(memoryState, value.memoryState);
    }

    private LinkedList<MemoryState> aggMemState(LinkedList<MemoryState> to, LinkedList<MemoryState> from) {
        LinkedList<MemoryState> ret = new LinkedList<MemoryState>();
        for (MemoryState toMs : to) {
            for (MemoryState fromMS : from) {
                if (toMs.getName().equals(fromMS.getName())) {
                    ret.add(new MemoryState(toMs.getName(),
                                            Math.max(toMs.getUsed(), fromMS.getUsed()),
                                            Math.max(toMs.getCommited(), fromMS.getCommited()),
                                            Math.max(toMs.getMax(), fromMS.getMax()),
                                            toMs.isHeap()));
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public void divide(int elements) {
        // TODO Auto-generated method stub

    }

    public long getCommitted() {
        return committed;
    }

    public long getInit() {
        return init;
    }

    public long getMax() {
        return max;
    }

    public long getUsed() {
        return used;
    }

    public double getPercentage() {
        return used * 100d / max;
    }

    public LinkedList<MemoryState> getMemoryState() {
        return memoryState;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MemoryUsage [cm=");
        builder.append((committed));
        builder.append(", in=");
        builder.append((init));
        builder.append(", max=");
        builder.append((max));
        builder.append(", us=");
        builder.append((used));
        builder.append(", pr=");
        builder.append((getPercentage()));
        builder.append("%, ").append(super.toString()).append("]");
        return builder.toString();
    }

}
