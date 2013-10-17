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
package org.clevermore.monitor.server;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.clevermore.SmartExecutor;
import org.clevermore.TaskMetadata;
import org.clevermore.monitor.server.constants.SmartPoolsMonitoring;
import org.clevermore.monitor.server.guice.GuiceUtils;
import org.clevermore.monitor.server.guice.MonitoringModule;
import org.clevermore.monitor.server.model.config.MailUpdaterConfig;
import org.clevermore.monitor.server.model.config.ValidateCertificates;
import org.clevermore.monitor.server.services.config.ConfigurationService;
import org.clevermore.monitor.server.tasks.ICertificateScanner;
import org.clevermore.monitor.server.tasks.IJMXConnectorThread;
import org.clevermore.monitor.server.tasks.IPeriodicalUpdater;
import org.clevermore.monitor.server.tasks.IStateUpdaterThread;
import org.clevermore.monitor.shared.config.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ServerStartUp
    implements ServletContextListener {

    public static Logger logger = LoggerFactory.getLogger(ServerStartUp.class);

    @Inject
    private SmartExecutor smartExecutor;

    @Inject
    private IJMXConnectorThread jmxConnectorThread;

    @Inject
    private IStateUpdaterThread stateUpdaterThread;

    @Inject
    private IPeriodicalUpdater periodicalUpdater;

    @Inject
    private ICertificateScanner certificateScanner;

    /**
     * for extensions to override and initialize other module
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
        smartExecutor.scheduleWithFixedDelay(jmxConnectorThread,
                                             5,
                                             60,
                                             TimeUnit.SECONDS,
                                             TaskMetadata.newMetadata(SmartPoolsMonitoring.GENERAL, "CONNECTOR", "jmxConnector"));

        logger.info("Starting StateUpdaterThread");
        smartExecutor.scheduleWithFixedDelay(stateUpdaterThread,
                                             15,
                                             20,
                                             TimeUnit.SECONDS,
                                             TaskMetadata.newMetadata(SmartPoolsMonitoring.GENERAL, "FULL_REFRESH", "stateUpdater"));

        if (getMailUpdaterConfig().isEnabled()) {
            logger.info("Starting Periodicat Updater");
            smartExecutor.scheduleAtFixedRate(periodicalUpdater,
                                              getMailUpdaterConfig().getPeriod() / 2,
                                              getMailUpdaterConfig().getPeriod(),
                                              TimeUnit.SECONDS,
                                              TaskMetadata.newMetadata(SmartPoolsMonitoring.GENERAL, "MAIL_UPD", "periodicalUpdater"));
        }

        final ValidateCertificates validateCertificates = ConfigurationService.getInstance().getValidateCertificates();
        if (validateCertificates.isValide()) {
            logger.info("Starting Certificates Validation Thread");
            smartExecutor.scheduleWithFixedDelay(certificateScanner,
                                                 0,
                                                 24,
                                                 TimeUnit.HOURS,
                                                 TaskMetadata.newMetadata(SmartPoolsMonitoring.GENERAL, "CERT", "certificateChecker"));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        logger.info("Context destroued, stopping smart executor");
        smartExecutor.shutdown();
    }

    public MailUpdaterConfig getMailUpdaterConfig() {
        return ConfigurationService.getInstance().getServersConfig().getMailUpdaterConfig();
    }

}
