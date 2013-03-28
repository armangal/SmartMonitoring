package com.smexec.monitor.server.services.config;

import com.smexec.monitor.server.model.config.ServersConfig;

public class ConfigurationService {

    /**
     * current most up-to-date configurations
     */
    private ServersConfig serversConfig;

    public void setServersConfig(ServersConfig sc) {
        serversConfig = sc;
    }

    public ServersConfig getServersConfig() {
        return serversConfig;
    }

    public Integer getMaxInMemoryAlerts() {
        return serversConfig.getInMemoryAlerts();
    }
}
