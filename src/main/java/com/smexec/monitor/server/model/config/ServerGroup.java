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
package com.smexec.monitor.server.model.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Server Group to be used once you want to define custom thresholds for sending alerts
 * 
 * @author armang
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "group")
public class ServerGroup {

    public final static ServerGroup DEFAULT_GROUP = new ServerGroup();
    
    public final static String DEFAULT_GROUP_NAME = "DEFAULT";

    /**
     * name should be unique in the list
     */
    private String name = DEFAULT_GROUP_NAME;
    /**
     * above this value alert will be sent
     */
    private Double cpuLoad = 90d;
    /**
     * above this value alert will be sent
     */
    private Double memoryUsage = 90d;

    public ServerGroup() {}

    ServerGroup(String name, Double cpuLoad, Double memoryUsage) {
        super();
        this.name = name;
        this.cpuLoad = cpuLoad;
        this.memoryUsage = memoryUsage;
    }

    public String getName() {
        return name;
    }

    public Double getCpuLoad() {
        return cpuLoad;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerGroup [name=").append(name).append(", cpuLoad=").append(cpuLoad).append(", memoryUsage=").append(memoryUsage).append("]\n");
        return builder.toString();
    }

}
