package com.smexec.monitor.server;

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
import com.smexec.monitor.server.guice.MonitoringModule;
import com.smexec.monitor.server.tasks.AbstractStateUpdaterThread;
import com.smexec.monitor.server.tasks.JMXConnectorThread;

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
    private JMXConnectorThread jmxConnectorThread;

    @Inject
    private AbstractStateUpdaterThread stateUpdaterThread;

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        GuiceUtils.init(new MonitoringModule());

        GuiceUtils.getInjector().injectMembers(this);

        logger.info("Starting JMXConnectorThread");
        executor.scheduleAtFixedRate(jmxConnectorThread, 5, 30, TimeUnit.SECONDS);

        logger.info("Starting StateUpdaterThread");
        executor.scheduleAtFixedRate(stateUpdaterThread, 20, 20, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        executor.shutdown();
    }
}
