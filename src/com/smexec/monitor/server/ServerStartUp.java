package com.smexec.monitor.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.tasks.JMXConnectorThread;
import com.smexec.monitor.server.tasks.StateUpdaterThread;

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

    @Override
    public void contextInitialized(ServletContextEvent arg0) {

        logger.info("Starting JMXConnectorThread");
        executor.scheduleAtFixedRate(new JMXConnectorThread(), 5, 30, TimeUnit.SECONDS);

        logger.info("Starting StateUpdaterThread");
        executor.scheduleAtFixedRate(new StateUpdaterThread(), 20, 20, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        executor.shutdown();

    }
}
