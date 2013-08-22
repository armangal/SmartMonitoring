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
package org.clevermore.monitor.server.services.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.clevermore.monitor.server.model.config.AbstractServersConfig;
import org.clevermore.monitor.server.model.config.MongoConfig;
import org.clevermore.monitor.server.model.config.ValidateCertificates;
import org.clevermore.monitor.shared.config.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigurationService<SC extends AbstractServersConfig>
    implements IConfigurationService<SC> {

    private static final String UTF_8 = "UTF-8";

    private static final Logger logger = LoggerFactory.getLogger("ConfigurationService");

    public static JAXBContext context;
    static String location;

    /**
     * reference to current most up-to-date configurations
     */
    private static AbstractServersConfig serversConfig;

    /**
     * when last time alert was disables
     */
    private Date stopAlertsStartDate;

    public AbstractConfigurationService() {

    }

    /*
     * (non-Javadoc)
     * @see org.clevermore.monitor.server.services.config.IConfigurationService#setServersConfig(SC)
     */
    @Override
    public void setServersConfig(SC sc) {
        logger.debug("setting configurations:{}", sc);
        serversConfig = sc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SC getServersConfig() {
        return (SC) serversConfig;
    }

    public MongoConfig getMongoConfig() {
        return serversConfig.getMongoConfig();
    }

    public ValidateCertificates getValidateCertificates() {
        return serversConfig.getValidateCertificates();
    }

    @Override
    public String getServersConfigXML()
        throws FileNotFoundException, IOException {

        String xml = IOUtils.toString(getFileInputStream(), UTF_8);
        return xml;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.clevermore.monitor.server.services.config.IConfigurationService#saveServersConfigXML(java.lang.
     * String)
     */
    @Override
    public void saveServersConfigXML(final String xml)
        throws IOException {
        File file = new File(location);
        FileUtils.writeStringToFile(file, xml, UTF_8);
        loadServersConfig(false);
        // TODO update some features that are already defendant on previous configurations.
    }

    /*
     * (non-Javadoc)
     * @see org.clevermore.monitor.server.services.config.IConfigurationService#getMaxInMemoryAlerts()
     */
    @Override
    public Integer getMaxInMemoryAlerts() {
        return serversConfig.getAlertsConfig().getInMemoryAlerts();
    }

    /*
     * (non-Javadoc)
     * @see org.clevermore.monitor.server.services.config.IConfigurationService#loadServersConfig(boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadServersConfig(boolean toStopServer) {
        try {

            context = getContext();
            FileInputStream input = getFileInputStream();
            SC serversConfig = (SC) context.createUnmarshaller().unmarshal(input);

            serversConfig.validate();

            logger.info("Initilized:{}", serversConfig);

            Version.setEnvName(serversConfig.getName());
            setServersConfig(serversConfig);
        } catch (Throwable e) {
            logger.error("Error loading config:{}", e.getMessage());
            logger.error(e.getMessage(), e);
            if (toStopServer) {
                Runtime.getRuntime().exit(1);
            }
        }
    }

    public abstract JAXBContext getContext()
        throws JAXBException;

    private static FileInputStream getFileInputStream()
        throws FileNotFoundException {
        location = System.getProperty("servers.config", "servers.xml");
        if (location == null || location.length() < 0) {
            location = "/opt/local/bex/conf/monitoring.xml";
        }

        logger.info("Loading configuraiotns:{}", location);
        File file = new File(location);
        if (!file.canRead()) {
            logger.error("Configuration file wasn't found at:{}", location);
        }
        logger.info("Configuration file:{}", file);

        FileInputStream input = new FileInputStream(file);
        return input;

    }

    /*
     * (non-Javadoc)
     * @see org.clevermore.monitor.server.services.config.IConfigurationService#stopAlerts(boolean)
     */
    @Override
    public Boolean stopAlerts(boolean enable) {
        serversConfig.getAlertsConfig().setEnabled(enable);
        if (!enable) {
            stopAlertsStartDate = new Date();
        }
        return serversConfig.getAlertsConfig().isEnabled();
    }

    public Date getStopAlertsStartDate() {
        return stopAlertsStartDate;
    }

    @Override
    public int authenticate(String userName, String password) {
        SC sc = getServersConfig();
        if ("admin".equalsIgnoreCase(userName) && sc.getPassword().equals(password)) {
            return 1;
        } else if ("guest".equalsIgnoreCase(userName) && sc.getGuestPassword().equals(password)) {
            return 2;
        }
        return 0;
    }
}
