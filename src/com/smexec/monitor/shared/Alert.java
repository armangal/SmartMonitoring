package com.smexec.monitor.shared;

import java.io.Serializable;

public class Alert
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String message;
    private String details;
    private int serverCode;
    private String alertTime;

    public Alert() {}

    public Alert(String message, int serverCode, String alertTime) {
        super();
        this.message = message;
        this.serverCode = serverCode;
        this.alertTime = alertTime;
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

    public String getDetails() {
        return details;
    }

    public int getServerCode() {
        return serverCode;
    }

    public String getAlertTime() {
        return alertTime;
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
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Alert [id=")
               .append(id)
               .append(", message=")
               .append(message)
               .append(", details=")
               .append(details)
               .append(", serverCode=")
               .append(serverCode)
               .append(", alertTime=")
               .append(alertTime)
               .append("]");
        return builder.toString();
    }

}
