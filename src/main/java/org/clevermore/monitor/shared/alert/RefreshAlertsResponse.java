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
package org.clevermore.monitor.shared.alert;

import java.util.Date;
import java.util.LinkedList;

import org.clevermore.monitor.client.AbstractRefreshResponse;


public class RefreshAlertsResponse
    extends AbstractRefreshResponse {

    private static final long serialVersionUID = 1L;

    private LinkedList<Alert> alerts;

    public RefreshAlertsResponse() {
        super(new Date().toString());
    }

    public RefreshAlertsResponse(String serverTime, LinkedList<Alert> alerts) {
        super(serverTime);
        this.alerts = alerts;
    }

    public LinkedList<Alert> getAlerts() {
        return alerts;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RefreshAlertsResponse [alerts=").append(alerts).append(super.toString()).append("]");
        return builder.toString();
    }

}
