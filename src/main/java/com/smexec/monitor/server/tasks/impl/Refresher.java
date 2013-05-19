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
package com.smexec.monitor.server.tasks.impl;

import java.util.Date;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.utils.JMXGeneralStats;
import com.smexec.monitor.server.utils.JMXSmartExecutorStats;

/**
 * class, used to update server stats. We use separate threads in order to make the updates as fast as
 * possible
 * 
 * @author armang
 */
public class Refresher<S extends ServerStatus>
    implements Callable<S> {

    private static Logger logger = LoggerFactory.getLogger("Refresher");

    @Inject
    private JMXGeneralStats jmxGeneralStats;
    @Inject
    private JMXSmartExecutorStats jmxSmartExecutorStats;

    private S ss;
    private Date executionDate;
    private int excutionNumber;

    public Refresher(S ss, Date executionDate, int excutionNumber) {
        this.ss = ss;
        this.excutionNumber = excutionNumber;
        this.executionDate = executionDate;
        GuiceUtils.getInjector().injectMembers(this);
    }

    @Override
    public S call()
        throws Exception {
        String old = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("REF_" + ss.getServerConfig().getName());
            if (ss.isConnected()) {
                logger.info("Refreshing:{}", ss.getServerConfig().getName());
                jmxGeneralStats.getMemoryStats(ss, executionDate);
                jmxSmartExecutorStats.getSmartThreadPoolStats(ss);
                fillExtraData(ss);

                ss.setFirstTimeAccess(false);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            Thread.currentThread().setName(old);
        }
        return ss;
    }

    /**
     * might be overridden to collect more information from the server
     * 
     * @param ss
     */
    public void fillExtraData(S ss) {
        // Nothing
    }

    public int getExcutionNumber() {
        return excutionNumber;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public S getServerStataus() {
        return ss;
    }
}
