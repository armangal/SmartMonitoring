package com.smexec.monitor.shared;

import java.io.Serializable;

public class GCHistory
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String collectorName;

    private long collectionCount;

    private long collectionTime;

    private String[] memoryPoolNames;

    public GCHistory() {}

    public GCHistory(String collectorName, long collectionCount, long collectionTime, String[] memoryPoolNames) {
        super();
        this.collectorName = collectorName;
        this.collectionCount = collectionCount;
        this.collectionTime = collectionTime;
        this.memoryPoolNames = memoryPoolNames;
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

    public String[] getMemoryPoolNames() {
        return memoryPoolNames;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (collectionCount ^ (collectionCount >>> 32));
        result = prime * result + ((collectorName == null) ? 0 : collectorName.hashCode());
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
        if (collectorName == null) {
            if (other.collectorName != null)
                return false;
        } else if (!collectorName.equals(other.collectorName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GCHistory [collectorName=");
        builder.append(collectorName);
        builder.append(", collectionCount=");
        builder.append(collectionCount);
        builder.append(", collectionTime=");
        builder.append(collectionTime);
        builder.append("]");
        return builder.toString();
    }

}
