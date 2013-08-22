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
package org.clevermore.monitor.server.tasks;

import java.sql.ResultSet;
import java.util.concurrent.Callable;

import org.clevermore.ITaskIdentification;
import org.clevermore.IThreadNameSuffixAware;
import org.clevermore.monitor.server.model.DatabaseServer;
import org.clevermore.monitor.server.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractDbRefresher<DS extends DatabaseServer>
    implements Callable<DS>, ITaskIdentification, IThreadNameSuffixAware {

    private static Logger logger = LoggerFactory.getLogger("AbstractDbRefresher");

    private DS ds;

    public AbstractDbRefresher(DS ds) {
        this.ds = ds;
    }

    @Override
    public String getTaskId() {
        return "DBREF_" + ds.getDatabaseConfig().getName();
    }

    @Override
    public String getThreadNameSuffix() {
        return "DBREF_" + ds.getDatabaseConfig().getName();
    }

    @Override
    public DS call()
        throws Exception {
        String oldName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(oldName + "_DB_REF_" + ds.getDatabaseConfig().getName());
            refersh();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            Thread.currentThread().setName(oldName);
        }

        return ds;
    }

    public void refersh() {
        try {
            long pingTime = System.currentTimeMillis();

            ResultSet rs = ds.getPingPs().executeQuery();
            try {
                rs.next();
                pingTime = (System.currentTimeMillis() - pingTime);
                logger.info("DB_Ping date:{} time:{}", rs.getDate(1), pingTime);
            } finally {
                SQLUtils.closeResultSet(rs);
            }
            ds.addPing(pingTime);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ds.closePingPs();
        }
    }

    public DS getDatabaseServer() {
        return ds;
    }

}
