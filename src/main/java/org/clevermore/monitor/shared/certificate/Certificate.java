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
package org.clevermore.monitor.shared.certificate;

import java.io.Serializable;
import java.util.Date;

/**
 * class that represent monitored certificate
 */
public class Certificate
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type; // server or chain
    private String commonName;
    private Date validFrom;
    private Date validTo;
    private String serialNumber;
    private String signatureAlgorithm;
    private String issuer;
    private String organization;
    private String location;
    private boolean alertRaised;

    Certificate() {}

    public Certificate(String type, Date validFrom, Date validTo, String serialNumber) {
        super();
        this.type = type;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.serialNumber = serialNumber;
    }

    public String getType() {
        return type;
    }

    public boolean isAlertRaised() {
        return alertRaised;
    }

    public void setAlertRaised(boolean alertRaised) {
        this.alertRaised = alertRaised;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Certificate [type=")
               .append(type)
               .append(", commonName=")
               .append(commonName)
               .append(", validFrom=")
               .append(validFrom)
               .append(", validTo=")
               .append(validTo)
               .append(", serialNumber=")
               .append(serialNumber)
               .append(", signatureAlgorithm=")
               .append(signatureAlgorithm)
               .append(", issuer=")
               .append(issuer)
               .append(", organization=")
               .append(organization)
               .append(", location=")
               .append(location)
               .append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (alertRaised ? 1231 : 1237);
        result = prime * result + ((commonName == null) ? 0 : commonName.hashCode());
        result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((organization == null) ? 0 : organization.hashCode());
        result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
        result = prime * result + ((signatureAlgorithm == null) ? 0 : signatureAlgorithm.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
        result = prime * result + ((validTo == null) ? 0 : validTo.hashCode());
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
        Certificate other = (Certificate) obj;
        if (alertRaised != other.alertRaised)
            return false;
        if (commonName == null) {
            if (other.commonName != null)
                return false;
        } else if (!commonName.equals(other.commonName))
            return false;
        if (issuer == null) {
            if (other.issuer != null)
                return false;
        } else if (!issuer.equals(other.issuer))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (organization == null) {
            if (other.organization != null)
                return false;
        } else if (!organization.equals(other.organization))
            return false;
        if (serialNumber == null) {
            if (other.serialNumber != null)
                return false;
        } else if (!serialNumber.equals(other.serialNumber))
            return false;
        if (signatureAlgorithm == null) {
            if (other.signatureAlgorithm != null)
                return false;
        } else if (!signatureAlgorithm.equals(other.signatureAlgorithm))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (validFrom == null) {
            if (other.validFrom != null)
                return false;
        } else if (!validFrom.equals(other.validFrom))
            return false;
        if (validTo == null) {
            if (other.validTo != null)
                return false;
        } else if (!validTo.equals(other.validTo))
            return false;
        return true;
    }

}
