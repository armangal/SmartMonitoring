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

import com.smexec.monitor.shared.AbstractMergeableChunkStats;

/**
 * data holder for CPU usage stats
 * 
 * @author armang
 */
public class CpuUtilizationChunk
    extends AbstractMergeableChunkStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private double usage;
    private double systemLoadAverage;

    public CpuUtilizationChunk() {}

    public CpuUtilizationChunk(double usage, double systemLoadAverage, long time) {
        super(time, time);
        this.usage = usage;
        this.systemLoadAverage = systemLoadAverage;
    }

    @Override
    public AbstractMergeableChunkStats copyMe() {
        return new CpuUtilizationChunk(usage, systemLoadAverage, getFullEndTime());
    }

    public void aggregate(AbstractMergeableChunkStats v) {
        CpuUtilizationChunk value = (CpuUtilizationChunk) v;
        this.usage = Math.max(usage, value.usage);
        this.systemLoadAverage = Math.max(systemLoadAverage, value.systemLoadAverage);
    }

    @Override
    public void divide(int elements) {
        // nothing

    }

    public double getUsage() {
        return usage;
    }

    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CUC [u=");
        builder.append(usage).append(", la=").append(systemLoadAverage);
        builder.append(", ");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(systemLoadAverage);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(usage);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CpuUtilizationChunk other = (CpuUtilizationChunk) obj;
        if (Double.doubleToLongBits(systemLoadAverage) != Double.doubleToLongBits(other.systemLoadAverage))
            return false;
        if (Double.doubleToLongBits(usage) != Double.doubleToLongBits(other.usage))
            return false;
        return true;
    }

}
