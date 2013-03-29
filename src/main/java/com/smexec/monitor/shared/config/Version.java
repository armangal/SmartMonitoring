package com.smexec.monitor.shared.config;

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
        StringBuilder builder = new StringBuilder();
        builder.append("Version [");
        builder.append(version);
        builder.append(", envName");
        builder.append(envName);
        builder.append("]");
        return builder.toString();
    }

}
