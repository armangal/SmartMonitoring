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

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smexec.SmartExecutor;
import org.smexec.TaskMetadata;

import com.google.inject.Inject;
import com.smexec.monitor.server.constants.SmartPoolsMonitoring;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.guice.MonitoringModule;
import com.smexec.monitor.server.model.config.MailUpdaterConfig;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;
import com.smexec.monitor.server.tasks.IPeriodicalUpdater;
import com.smexec.monitor.server.tasks.IStateUpdaterThread;
import com.smexec.monitor.shared.config.Version;

public class ServerStartUp
    implements ServletContextListener {

    private static Logger logger = LoggerFactory.getLogger(ServerStartUp.class);

    @Inject
    private SmartExecutor smartExecutor;

    @Inject
    private IJMXConnectorThread jmxConnectorThread;

    @Inject
    private IStateUpdaterThread stateUpdaterThread;

    @Inject
    private IPeriodicalUpdater periodicalUpdater;

    /**
     * for extensions to override and initilize other module
     */
    public void initGuice() {
        GuiceUtils.init(new MonitoringModule());
    }

    public void loadServerConfig() {
        ConfigurationService.getInstance().loadServersConfig(true);
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        loadServerConfig();
        initGuice();
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
        smartExecutor.scheduleAtFixedRate(jmxConnectorThread, 5, 60, TimeUnit.SECONDS, TaskMetadata.newMetadata(SmartPoolsMonitoring.CONNECTOR, "CONNECTOR"));

        logger.info("Starting StateUpdaterThread");
        smartExecutor.scheduleAtFixedRate(stateUpdaterThread,
                                          20,
                                          20,
                                          TimeUnit.SECONDS,
                                          TaskMetadata.newMetadata(SmartPoolsMonitoring.CONNECTOR, "FULL_REFRESH"));

        if (getMailUpdaterConfig().isEnabled()) {
            logger.info("Starting Periodicat Updater");
            smartExecutor.scheduleAtFixedRate(periodicalUpdater,
                                              getMailUpdaterConfig().getPeriod() / 2,
                                              getMailUpdaterConfig().getPeriod(),
                                              TimeUnit.SECONDS,
                                              TaskMetadata.newMetadata(SmartPoolsMonitoring.CONNECTOR, "MAIL_UPD"));
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        smartExecutor.shutdown();
    }

    public MailUpdaterConfig getMailUpdaterConfig() {
        return ConfigurationService.getInstance().getServersConfig().getMailUpdaterConfig();
    }

}
