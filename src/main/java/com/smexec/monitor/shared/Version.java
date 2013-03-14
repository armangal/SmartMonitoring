package com.smexec.monitor.shared;

import java.io.Serializable;

public class Version
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private static String version = "XXX";

    public Version() {}

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        Version.version = version;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Version [");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }

}
