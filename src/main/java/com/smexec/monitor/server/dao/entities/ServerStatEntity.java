package com.smexec.monitor.server.dao.entities;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity("serverstat")
public class ServerStatEntity {

    @Id
    private ObjectId id;
    private Long startTime;
    private Long endTime;
    private Double memoryUsage;
    private Double cpuUsage;

    public ServerStatEntity(Long startTime, Long endTime, Double memoryUsage, Double cpuUsage) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.memoryUsage = memoryUsage;
        this.cpuUsage = cpuUsage;
    }

    public ObjectId getId() {
        return id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

}
