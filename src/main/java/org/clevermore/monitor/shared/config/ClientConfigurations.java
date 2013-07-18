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
package org.clevermore.monitor.shared.config;

import java.io.Serializable;

public class ClientConfigurations
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String version;

    private Boolean alertsEnabled;

    private Boolean smartPoolMonEnabled;

    public ClientConfigurations() {}

    public ClientConfigurations(final String title, final String version, final Boolean alertsEnabled, final Boolean smartPoolMonEnabled) {
        super();
        this.title = title;
        this.version = version;
        this.alertsEnabled = alertsEnabled;
        this.smartPoolMonEnabled = smartPoolMonEnabled;
    }

    public String getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public Boolean isAlertsEnabled() {
        return alertsEnabled;
    }

    public Boolean isSmartPoolMonEnabled() {
        return smartPoolMonEnabled;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClientConfigurations [title=")
               .append(title)
               .append(", version=")
               .append(version)
               .append(", alertsEnabled=")
               .append(alertsEnabled)
               .append(", smartPoolMonEnabled=")
               .append(smartPoolMonEnabled)
               .append("]");
        return builder.toString();
    }

}
