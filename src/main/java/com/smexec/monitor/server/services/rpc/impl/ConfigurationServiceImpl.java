package com.smexec.monitor.server.services.rpc.impl;

import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.server.services.rpc.AbstractConfigurationServiceImpl;
import com.smexec.monitor.shared.config.ClientConfigurations;
import com.smexec.monitor.shared.config.Version;

public class ConfigurationServiceImpl
    extends AbstractConfigurationServiceImpl<ClientConfigurations, ServerStatus, ServersConfig, DatabaseServer> {

    private static final long serialVersionUID = 1L;

    @Override
    public ClientConfigurations getClientConfigurations() {
        ClientConfigurations clientConfigurations = new ClientConfigurations(Version.getEnvName(),
                                                                             Version.getVersion(),
                                                                             getConfigurationService().getServersConfig().getAlertsConfig().isEnabled());
        logger.info("returning basic configurations:{}", clientConfigurations);
        return clientConfigurations;

    }
}
