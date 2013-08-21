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
    private String SANs;
    private Date validFrom;
    private Date validTo;
    private String serialNumber;
    private String signatureAlgorithm;
    private String issuer;
    private String organization;
    private String location;

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

    public void setType(String type) {
        this.type = type;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getSANs() {
        return SANs;
    }

    public void setSANs(String sANs) {
        SANs = sANs;
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
               .append(", SANs=")
               .append(SANs)
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

}
