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
package com.smexec.monitor.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.model.config.DatabaseConfig;
import com.smexec.monitor.server.utils.DateUtils;
import com.smexec.monitor.server.utils.SQLUtils;
import com.smexec.monitor.shared.servers.DbPingChunk;

public class DatabaseServer {

    private static Logger logger = LoggerFactory.getLogger("DatabaseServer");

    private DatabaseConfig databaseConfig;
    private Connection connection;
    private PreparedStatement pingPs;

    private LinkedList<DbPingChunk> pings = new LinkedList<DbPingChunk>();

    public DatabaseServer(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        if (this.databaseConfig == null || !this.databaseConfig.equals(databaseConfig)) {
            logger.info("Changing DatabaseConfig from:{}, to:{}", this.databaseConfig, databaseConfig);
            this.databaseConfig = databaseConfig;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isConnected() {
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public LinkedList<DbPingChunk> getPings() {
        return pings;
    }

    public long getLastPingTime() {
        return pings.size() > 0 ? pings.getLast().getPingTime() : -1L;
    }

    public void addPing(long pingTime) {
        pings.add(new DbPingChunk(pingTime, DateUtils.roundDate(new Date())));

        while (pings.size() > 3 * 60 * 24) {
            pings.remove();
        }
    }

    public PreparedStatement getPingPs() {
        try {
            if (pingPs == null) {
                pingPs = getConnection().prepareStatement(getDatabaseConfig().getPingStatement());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return pingPs;
    }

    public void closePingPs() {
        SQLUtils.closeStatement(pingPs);
        pingPs = null;
    }
}
