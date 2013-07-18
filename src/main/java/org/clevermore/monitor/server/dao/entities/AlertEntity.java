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
package org.clevermore.monitor.server.dao.entities;

import java.io.Serializable;

import com.github.jmkgreen.morphia.annotations.Entity;

@Entity("alerts")
public class AlertEntity
    extends AbstractEntity
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private int alertId;
    private String message;
    private String details;
    private Integer alertType;
    private String alertName;
    private Boolean mailSent;

    AlertEntity() {}

    public AlertEntity(Long time,
                       Integer serverCode,
                       String serverName,
                       int alertId,
                       String message,
                       String details,
                       Integer alertType,
                       String alertName,
                       Boolean mailSent) {

        super(time, serverCode, serverName);
        this.alertId = alertId;
        this.message = message;
        this.details = details;
        this.alertType = alertType;
        this.alertName = alertName;
        this.mailSent = mailSent;
    }

    public int getAlertId() {
        return alertId;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public Integer getAlertType() {
        return alertType;
    }

    public String getAlertName() {
        return alertName;
    }

    public Boolean getMailSent() {
        return mailSent;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AlertEntity [")
               .append(super.toString())
               .append(", alertId=")
               .append(alertId)
               .append(", message=")
               .append(message)
               .append(", details=")
               .append(details)
               .append(", alertType=")
               .append(alertType)
               .append(", alertName=")
               .append(alertName)
               .append(", mailed=")
               .append(mailSent)

               .append("]");
        return builder.toString();
    }

}
