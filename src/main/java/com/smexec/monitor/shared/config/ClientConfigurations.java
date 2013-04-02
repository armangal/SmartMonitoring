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
package com.smexec.monitor.shared.config;

import java.io.Serializable;

public class ClientConfigurations
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String version;

    public ClientConfigurations() {}

    public ClientConfigurations(String title, String version) {
        super();
        this.title = title;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClientConfigurations [title=");
        builder.append(title);
        builder.append(", version=");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }

}
