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
package org.clevermore.monitor.server.model.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "alerts")
public class AlertsConfig {

    private Boolean enabled = false;
    private String fromAddress = "";
    private String fromName = "";
    private String subjectPrefix = "Smart Monitoring, Alert:";
    private String mailServerAddress = "";
    private String mainServerPort = "";

    private Boolean authenticate = false;
    private String userName = "";
    private String password = "";

    private Integer inMemoryAlerts = 10000;

    @XmlElement(name = "to")
    @XmlElementWrapper
    private List<String> toAddressList;

    public AlertsConfig() {}

    public AlertsConfig(Boolean enabled,
                        String fromAddress,
                        String fromName,
                        String subjectPrefix,
                        String mailServerAddress,
                        String mainServerPort,
                        Integer inMemoryAlerts,
                        List<String> toAddressList) {
        super();
        this.enabled = enabled;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
        this.subjectPrefix = subjectPrefix;
        this.mailServerAddress = mailServerAddress;
        this.mainServerPort = mainServerPort;
        this.inMemoryAlerts = inMemoryAlerts;
        this.toAddressList = toAddressList;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public String getSubjectPrefix() {
        return subjectPrefix;
    }

    public String getMailServerAddress() {
        return mailServerAddress;
    }

    public String getMainServerPort() {
        return mainServerPort;
    }

    public List<String> getToAddressList() {
        return toAddressList;
    }

    public Integer getInMemoryAlerts() {
        return inMemoryAlerts;
    }

    public Boolean isAuthenticate() {
        return authenticate;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AlertsConfig [enabled=");
        builder.append(enabled);
        builder.append(", fromAddress=");
        builder.append(fromAddress);
        builder.append(", fromName=");
        builder.append(fromName);
        builder.append(", subjectPrefix=");
        builder.append(subjectPrefix);
        builder.append(", mailServerAddress=");
        builder.append(mailServerAddress);
        builder.append(", mainServerPort=");
        builder.append(mainServerPort);
        builder.append(", authenticate=");
        builder.append(authenticate);
        builder.append(", userName=");
        builder.append(userName);
        builder.append(", password=");
        builder.append(password);
        builder.append(", inMemoryAlerts=");
        builder.append(inMemoryAlerts);
        builder.append(", toAddressList=");
        builder.append(toAddressList);
        builder.append("]");
        return builder.toString();
    }

}
