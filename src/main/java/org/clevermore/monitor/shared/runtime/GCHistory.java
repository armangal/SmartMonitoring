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

public class GCHistory
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String collectorName;

    private long collectionCount;

    /**
     * total collection time spent by this pool
     */
    private long collectionTime;

    private long lastColleactionTime =-1l; 

    private String time;

    public GCHistory() {}

    public GCHistory(String collectorName, long collectionCount, long collectionTime, String time) {
        super();
        this.collectorName = collectorName;
        this.collectionCount = collectionCount;
        this.collectionTime = collectionTime;
        this.time = time;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public long getCollectionCount() {
        return collectionCount;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public long getLastColleactionTime() {
        return lastColleactionTime;
    }

    public void setLastColleactionTime(long lastColleactionTime) {
        this.lastColleactionTime = lastColleactionTime;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (collectionCount ^ (collectionCount >>> 32));
        result = prime * result + (int) (collectionTime ^ (collectionTime >>> 32));
        result = prime * result + ((collectorName == null) ? 0 : collectorName.hashCode());
        result = prime * result + (int) (lastColleactionTime ^ (lastColleactionTime >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GCHistory other = (GCHistory) obj;
        if (collectionCount != other.collectionCount)
            return false;
        if (collectionTime != other.collectionTime)
            return false;
        if (collectorName == null) {
            if (other.collectorName != null)
                return false;
        } else if (!collectorName.equals(other.collectorName))
            return false;
        if (lastColleactionTime != other.lastColleactionTime)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GCHistory [cn=");
        builder.append(collectorName);
        builder.append(", cc=");
        builder.append(collectionCount);
        builder.append(", ct=");
        builder.append(collectionTime);
        builder.append(", lct=");
        builder.append(lastColleactionTime);
        builder.append(", t=").append(time);
        builder.append("]");
        return builder.toString();
    }

}
