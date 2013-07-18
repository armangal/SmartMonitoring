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
package org.clevermore.monitor.shared.runtime;

import java.io.Serializable;

/**
 * represents one memory measurement for server representation only
 * 
 * @author armang
 */
public class MemoryUsageLight
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long committed;
    private long init;
    private long max;
    private long used;

    public MemoryUsageLight() {}

    public MemoryUsageLight(long init, long used, long committed, long max) {
        this.committed = committed;
        this.init = init;
        this.max = max;
        this.used = used;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MemoryUsageLight [cm=");
        builder.append((committed));
        builder.append(", in=");
        builder.append((init));
        builder.append(", max=");
        builder.append((max));
        builder.append(", us=");
        builder.append((used));
        builder.append(", pr=");
        builder.append((getPercentage()));
        builder.append("%]");
        return builder.toString();
    }

}
