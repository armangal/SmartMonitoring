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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.model.DatabaseServer;

public class DbRefresher<DS extends DatabaseServer>
    implements Callable<DS> {

    private static Logger logger = LoggerFactory.getLogger("DbRefresher");

    private DS ds;

    public DbRefresher(DS ds) {
        this.ds = ds;
    }

    @Override
    public DS call()
        throws Exception {
        String oldName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("DB_REF_" + ds.getDatabaseConfig().getName());
            refersh();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            Thread.currentThread().setName(oldName);
        }

        return ds;
    }

    public void refersh() {
        Statement st;
        try {
            long pingTime = System.currentTimeMillis();
            st = ds.getConnection().createStatement();
            ResultSet rs = st.executeQuery(ds.getDatabaseConfig().getPingStatement());
            rs.next();
            pingTime = (System.currentTimeMillis() - pingTime);
            logger.info("DB_Ping date:{} time:", rs.getDate(1), pingTime);
            ds.addPing(pingTime);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public DS getDatabaseServer() {
        return ds;
    }

}
