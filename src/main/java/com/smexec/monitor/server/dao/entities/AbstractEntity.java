package com.smexec.monitor.server.dao.entities;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Id;

public abstract class AbstractEntity
    implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private Long time;
    private Integer serverCode;
    private String serverName;

    
    AbstractEntity() {
        //empty one to support serialisation FW
    }

    public AbstractEntity(ObjectId id, Long time, Integer serverCode, String serverName) {
        super();
        this.id = id;
        this.time = time;
        this.serverCode = serverCode;
        this.serverName = serverName;
    }

    public AbstractEntity(Long time, Integer serverCode, String serverName) {
        super();
        this.time = time;
        this.serverCode = serverCode;
        this.serverName = serverName;
    }
    public ObjectId getId() {
        return id;
    }

    public Long getTime() {
        return time;
    }

    public Integer getServerCode() {
        return serverCode;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AE [id=").append(id).append(", time=").append(time).append(", sc=").append(serverCode).append(", sn=").append(serverName).append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((serverCode == null) ? 0 : serverCode.hashCode());
        result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
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
        AbstractEntity other = (AbstractEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (serverCode == null) {
            if (other.serverCode != null)
                return false;
        } else if (!serverCode.equals(other.serverCode))
            return false;
        if (serverName == null) {
            if (other.serverName != null)
                return false;
        } else if (!serverName.equals(other.serverName))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        return true;
    }

}
