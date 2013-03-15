package com.smexec.monitor.server.tasks;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.utils.JMXGeneralStats;
import com.smexec.monitor.server.utils.JMXSmartExecutorStats;

/**
 * class, used to update server stats. We use separate threads in order to make the updates as fast as
 * possible
 * 
 * @author armang
 */
public class Refresher<S extends ServerStataus>
    implements Callable<S> {

    private static Logger logger = LoggerFactory.getLogger(Refresher.class);

    @Inject
    private JMXGeneralStats jmxGeneralStats;
    @Inject
    private JMXSmartExecutorStats jmxSmartExecutorStats;

    private S ss;

    public Refresher(S ss) {
        setSs(ss);
        GuiceUtils.getInjector().injectMembers(this);
    }

    @Override
    public S call()
        throws Exception {
        try {
            if (ss.isConnected()) {
                System.out.println("Refreshing:" + ss.getServerConfig().getName());
                jmxGeneralStats.getMemoryStats(ss);
                jmxSmartExecutorStats.getSmartThreadPoolStats(ss);
                fillExtraData(ss);

                ss.setFirstTimeAccess(false);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ss;
    }

    public void fillExtraData(S ss) {

    }

    public void setSs(S ss) {
        this.ss = ss;
    }

}
