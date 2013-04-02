package com.smexec.monitor.server.services.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.shared.config.Version;

public class ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger("ConfigurationService");
    /**
     * current most up-to-date configurations
     */
    private ServersConfig serversConfig;

    private JAXBContext context;

    public ConfigurationService()
        throws JAXBException {
        try {
            context = JAXBContext.newInstance(ServersConfig.class);

            String location = System.getProperty("servers.config", "servers.xml");
            if (location == null || location.length() < 0) {
                location = "/opt/local/bex/conf/monitoring.xml";
            }

            logger.info("Loading configuraiotns:{}", location);
            File file = new File(location);
            if (!file.canRead()) {
                logger.error("Configuration file wasn't found at:{}", location);
            }
            logger.info("Configuration file:{}", file);

            InputStream configXML = new FileInputStream(file);
            ServersConfig serversConfig = (ServersConfig) context.createUnmarshaller().unmarshal(configXML);

            serversConfig.validate();

            logger.info("Initilized:{}", serversConfig);
            setServersConfig(serversConfig);

            Version.setEnvName(serversConfig.getName());
        } catch (Throwable e) {
            logger.error("Error loading config:{}", e.getMessage());
            logger.error(e.getMessage(), e);
            Runtime.getRuntime().exit(1);
        }

    }

    public void setServersConfig(ServersConfig sc) {
        serversConfig = sc;
    }

    public ServersConfig getServersConfig() {
        return serversConfig;
    }

    public Integer getMaxInMemoryAlerts() {
        return serversConfig.getAlertsConfig().getInMemoryAlerts();
    }
}
