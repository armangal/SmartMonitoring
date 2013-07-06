package com.smexec.monitor.client;

import java.io.Serializable;

public abstract class AbstractRefreshResponse
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serverTime;

    public AbstractRefreshResponse(String serverTime) {
        super();
        this.serverTime = serverTime;
    }

    public String getServerTime() {
        return serverTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(", serverTime=").append(serverTime);
        return builder.toString();
    }

}
