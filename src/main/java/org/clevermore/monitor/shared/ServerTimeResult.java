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
package org.clevermore.monitor.shared;

import java.io.Serializable;
import java.util.Date;

public class ServerTimeResult
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serverTime;
    private boolean alertsEnabled;
    private int timeLeftForAutoEnable;

    public ServerTimeResult() {}

    public ServerTimeResult(boolean alertsEnabled, int timeLeftForAutoEnable) {
        this.serverTime = new Date().toString();
        this.alertsEnabled = alertsEnabled;
        this.timeLeftForAutoEnable = timeLeftForAutoEnable;
    }

    public String getServerTime() {
        return serverTime;
    }

    public int getTimeLeftForAutoEnable() {
        return timeLeftForAutoEnable;
    }

    public boolean isAlertsEnabled() {
        return alertsEnabled;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerTimeResult [serverTime=")
               .append(serverTime)
               .append(", alertsEnabled=")
               .append(alertsEnabled)
               .append(", timeLeftForAutoEnable=")
               .append(timeLeftForAutoEnable)
               .append("]");
        return builder.toString();
    }

}
