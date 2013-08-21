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

import java.io.Serializable;

public class AlertThreshold
    implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final AlertThreshold ALWAYS = new AlertThreshold(Long.MAX_VALUE, -1);
    public static final AlertThreshold NEVER = new AlertThreshold(Long.MIN_VALUE, Integer.MAX_VALUE);

    /**
     * when we don't want to send alert immediately after it occurs but want to make sure that X alerts
     * occurred during Y period of time. So this's the Y
     */
    private long alertTriggerTime;

    /**
     * this's the X from above
     */
    private int alertTriggerCount;

    AlertThreshold() {}

    /**
     * Example: <br>
     * <p>
     * <b>new AlertThreshold(2 * 60 * 1000L, 5)</b> = send main_alert after we had 5 times alert during last 2
     * minutes
     * </p>
     * <p>
     * new AlertThreshold(Long.MIN_VALUE, Integer.MAX_VALUE) = never
     * </p>
     * <p>
     * new AlertThreshold(Long.MAX_VALUE, -1) = always
     * </p>
     * 
     * @param alertTriggerTime
     * @param alertTriggerCount
     */
    public AlertThreshold(long alertTriggerTime, int alertTriggerCount) {
        super();
        this.alertTriggerTime = alertTriggerTime;
        this.alertTriggerCount = alertTriggerCount;
    }

    public long getAlertTriggerTime() {
        return alertTriggerTime;
    }

    public int getAlertTriggerCount() {
        return alertTriggerCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AlertThreshold [alertTriggerTime=").append(alertTriggerTime).append(", alertTriggerCount=").append(alertTriggerCount).append("]");
        return builder.toString();
    }

}
