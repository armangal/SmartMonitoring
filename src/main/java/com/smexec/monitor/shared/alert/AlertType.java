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
package com.smexec.monitor.shared.alert;

import java.util.HashMap;
import java.util.Map;

public enum AlertType implements IAlertType {

    MEMORY("Memory", 1, AlertGroup.SYSTEM, true, new AlertThreshold(2 * 60 * 1000L, 5)), //
    CPU("Cpu", 2, AlertGroup.SYSTEM, true, new AlertThreshold(2 * 60 * 1000L, 5)), //
    SERVER_COMM_FAILED("Connection Failed", 3, AlertGroup.SYSTEM, true, new AlertThreshold(Long.MAX_VALUE, -1)), //
    SERVER_DISCONNECTED("Disconnection", 4, AlertGroup.SYSTEM, true, new AlertThreshold(Long.MAX_VALUE, -1)), //
    SERVER_CONNECTED("Re-Connectoin", 5, AlertGroup.SYSTEM, true, new AlertThreshold(Long.MAX_VALUE, -1)), //
    GC("GC", 6, AlertGroup.SYSTEM, true, AlertThreshold.ALWAYS),//
    MEMORY_SPACE("Memory Space", 7, AlertGroup.SYSTEM, true, new AlertThreshold(2 * 60 * 1000L, 5))//
    ;

    private String name;
    private Integer id;
    private AlertGroup alertGroup;
    private boolean sendMail;
    private AlertThreshold alertThreshold;

    private static Map<Integer, AlertType> map = new HashMap<Integer, AlertType>(values().length);

    static {
        for (AlertType atp : values()) {
            AlertType put = map.put(atp.getId(), atp);
            if (put != null) {
                throw new RuntimeException("Duplicate alert ID:" + put.getId());
            }
        }
    }

    private AlertType(String name, Integer id, AlertGroup alertGroup, boolean sendMail, AlertThreshold alertThreshold) {
        this.name = name;
        this.id = id;
        this.alertGroup = alertGroup;
        this.sendMail = sendMail;
        this.alertThreshold = alertThreshold;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public AlertGroup getAlertGroup() {
        return alertGroup;
    }

    @Override
    public boolean sendMail() {
        return sendMail;
    }

    @Override
    public AlertThreshold getAlertThreshold() {
        return alertThreshold;
    }
}
