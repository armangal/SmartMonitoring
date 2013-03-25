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
