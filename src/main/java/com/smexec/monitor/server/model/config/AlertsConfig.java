package com.smexec.monitor.server.model.config;

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
    private String fromAddress;
    private String fromName;
    private String subjectPrefix = "Smart Monitoring, Alert:";
    private String mailServerAddress;
    private String mainServerPort;

    private Boolean authenticate = false;
    private String userName;
    private String password;

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
}
