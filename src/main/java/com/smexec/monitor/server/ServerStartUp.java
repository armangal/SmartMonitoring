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
package com.smexec.monitor.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.guice.MonitoringModule;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;
import com.smexec.monitor.server.tasks.IPeriodicalUpdater;
import com.smexec.monitor.server.tasks.IStateUpdaterThread;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.config.Version;

public class ServerStartUp
    implements ServletContextListener {

    private static Logger logger = LoggerFactory.getLogger(ServerStartUp.class);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(3, new ThreadFactory() {

        int count = 0;

        @Override
        public Thread newThread(Runnable r) {

            return new Thread(r, "STARTER_" + count++);
        }
    });

    @Inject
    private IJMXConnectorThread jmxConnectorThread;

    @Inject
    private IStateUpdaterThread stateUpdaterThread;

    @Inject
    private IPeriodicalUpdater periodicalUpdater;

    /**
     * for extensions to override and initilize other module
     */
    public void initGuice(ServersConfig serversConfig) {
        GuiceUtils.init(new MonitoringModule<ServerStataus, ConnectedServer>(serversConfig));
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ServersConfig serversConfig = getServersConfig();
        ConfigurationService.setServersConfig(serversConfig);
        initGuice(serversConfig);
        GuiceUtils.getInjector().injectMembers(this);

        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("version.txt");
            byte[] bytes = new byte[resourceAsStream.available()];
            resourceAsStream.read(bytes);
            String version = new String(bytes);
            Version.setVersion(version);
            resourceAsStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Version.setVersion("Unknown");
        }

        logger.info("Version:{}", Version.getVersion());

        logger.info("Starting AbstractJMXConnectorThread");
        executor.scheduleAtFixedRate(jmxConnectorThread, 5, 30, TimeUnit.SECONDS);

        logger.info("Starting StateUpdaterThread");
        executor.scheduleAtFixedRate(stateUpdaterThread, 20, 20, TimeUnit.SECONDS);

        if (serversConfig.getMailUpdaterConfig().isEnabled()) {
            logger.info("Starting Periodicat Updater");
            executor.scheduleAtFixedRate(periodicalUpdater, serversConfig.getMailUpdaterConfig().getPeriod() / 2, serversConfig.getMailUpdaterConfig()
                                                                                                                               .getPeriod(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        executor.shutdown();
    }

    public ServersConfig getServersConfig() {
        JAXBContext context;
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

            Version.setEnvName(serversConfig.getName());
            return serversConfig;
        } catch (Throwable e) {
            logger.error("Error loading config:{}", e.getMessage());
            logger.error(e.getMessage(), e);
            Runtime.getRuntime().exit(1);
            return null;
        }
    }
}
