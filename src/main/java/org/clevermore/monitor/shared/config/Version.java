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

public class Version
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private static String version = "XXX";

    private static String envName = "XXX";

    public Version() {}

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        Version.version = version;
    }

    public static String getEnvName() {
        return envName;
    }

    public static void setEnvName(String envName) {
        Version.envName = envName;
    }

    @Override
    public String toString() {
        return version();
    }

    public static String version() {
        StringBuilder builder = new StringBuilder();
        builder.append("Version [");
        builder.append(version);
        builder.append(", EnvName: ");
        builder.append(envName);
        builder.append("]");
        return builder.toString();
    }

}
