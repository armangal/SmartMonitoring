package com.smexec.monitor.server.services.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.smexec.monitor.server.model.config.ServersConfig;

public interface IConfigurationService<SC extends ServersConfig> {

    void setServersConfig(SC sc);

    SC getServersConfig();

    String getServersConfigXML()
        throws FileNotFoundException, IOException;

    void saveServersConfigXML(final String xml)
        throws IOException;

    Integer getMaxInMemoryAlerts();

    /**
     * @param toStopServer - meaning if failed to load configs, we will crush the server.
     */
    void loadServersConfig(boolean toStopServer);

    Boolean stopAlerts(boolean enable);

}
