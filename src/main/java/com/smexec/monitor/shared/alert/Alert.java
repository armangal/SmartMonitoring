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

import java.io.Serializable;

public class Alert
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String message;
    private String details;
    private int serverCode;
    private String serverName;
    private String alertTime;
    private IAlertType alertType;

    public Alert() {}

    public Alert(String message, int serverCode, String serverName, String alertTime, IAlertType alertType) {
        super();
        this.message = message;
        this.serverCode = serverCode;
        this.alertTime = alertTime;
        this.alertType = alertType;
        this.serverName = serverName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public int getServerCode() {
        return serverCode;
    }

    public String getAlertTime() {
        return alertTime;
    }

    public IAlertType getAlertType() {
        return alertType;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alertTime == null) ? 0 : alertTime.hashCode());
        result = prime * result + ((details == null) ? 0 : details.hashCode());
        result = prime * result + id;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + serverCode;
        result = prime * result + ((alertType == null) ? 0 : alertType.hashCode());
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
        Alert other = (Alert) obj;
        if (alertTime == null) {
            if (other.alertTime != null)
                return false;
        } else if (!alertTime.equals(other.alertTime))
            return false;
        if (details == null) {
            if (other.details != null)
                return false;
        } else if (!details.equals(other.details))
            return false;
        if (id != other.id)
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (serverCode != other.serverCode)
            return false;
        if (alertType == null) {
            if (other.alertType != null)
                return false;
        } else if (!alertType.equals(other.alertType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Alert [id=");
        builder.append(id);
        builder.append(", ms=");
        builder.append(message);
        builder.append(", sc=");
        builder.append(serverCode);
        builder.append(", sn=");
        builder.append(serverName);
        builder.append(", at=");
        builder.append(alertTime);
        builder.append(", t=");
        builder.append(alertType);
        builder.append(",\nde=");
        builder.append(details);
        builder.append("]");
        return builder.toString();
    }

}
