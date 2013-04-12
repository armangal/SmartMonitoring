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
package com.smexec.monitor.server.services.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.shared.config.Version;

public class ConfigurationService {

    private static final String UTF_8 = "UTF-8";

    private static final Logger logger = LoggerFactory.getLogger("ConfigurationService");

    static JAXBContext context;
    static String location;

    /**
     * current most up-to-date configurations
     */
    private static ServersConfig serversConfig;

    public ConfigurationService() {

    }

    public static void setServersConfig(ServersConfig sc) {
        logger.debug("setting configurations:{}", sc);
        serversConfig = sc;
    }

    public static ServersConfig getServersConfig() {
        return serversConfig;
    }

    public String getServersConfigXML()
        throws FileNotFoundException, IOException {

        String xml = IOUtils.toString(getFileInputStream(), UTF_8);
        return xml;
    }

    public void saveServersConfigXML(final String xml)
        throws IOException {
        File file = new File(location);
        FileUtils.writeStringToFile(file, xml, UTF_8);
        loadServersConfig(false);
        // TODO update some features that are already defendant on previous configurations.
    }

    public Integer getMaxInMemoryAlerts() {
        return serversConfig.getAlertsConfig().getInMemoryAlerts();
    }

    public static void loadServersConfig(boolean toStopServer) {
        try {
            context = JAXBContext.newInstance(ServersConfig.class);
            FileInputStream input = getFileInputStream();
            ServersConfig serversConfig = (ServersConfig) context.createUnmarshaller().unmarshal(input);

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
}
