package com.smexec.monitor.server;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.guice.MonitoringModulePoker;
import com.smexec.monitor.server.tasks.IJMXConnectorThread;
import com.smexec.monitor.server.tasks.IStateUpdaterThread;
import com.smexec.monitor.shared.Version;

public class ServerStartUp
    implements ServletContextListener {

    private static Logger logger = LoggerFactory.getLogger(ServerStartUp.class);

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(5, new ThreadFactory() {

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

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        GuiceUtils.init(new MonitoringModulePoker());

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
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        executor.shutdown();
    }
}
