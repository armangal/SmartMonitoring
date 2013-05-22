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
package com.smexec.monitor.server.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmkgreen.morphia.Morphia;
import com.google.inject.Inject;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.smexec.monitor.server.model.config.MongoConfig;
import com.smexec.monitor.server.model.config.MongoConfig.HostAddress;
import com.smexec.monitor.server.services.config.ConfigurationService;

public class MongoSessionFactory {

    private static Logger logger = LoggerFactory.getLogger(MongoSessionFactory.class);

    private static MongoConfig mongoConfig;
    private static Mongo mongo;
    private static Morphia morphia;

    @Inject
    public MongoSessionFactory() {
        mongoConfig = ConfigurationService.getInstance().getMongoConfig();

        if (!mongoConfig.getEnabled()) {
            logger.info("Mongo DB is disabled.Skipping initialization");
            return;
        }

        List<HostAddress> hosts = mongoConfig.getHosts();
        if (hosts == null || hosts.isEmpty()) {
            throw new RuntimeException("Mongo DB  Initialization Error: hosts not configured");
        }

        if (mongoConfig.getDatabaseName() == null || mongoConfig.getDatabaseName().isEmpty()) {
            throw new RuntimeException("Mongo DB Initialization Error : database name not specified");
        }

        List<ServerAddress> mongoServers = new ArrayList<ServerAddress>();
        for (HostAddress host : hosts) {
            try {
                mongoServers.add(new ServerAddress(host.getHostName(), host.getPort()));
            } catch (UnknownHostException e) {
                throw new RuntimeException("Mongo DB  Initialization Error ", e);
            }
        }

        mongo = new Mongo(mongoServers);
        morphia = new Morphia();
    }

    public static Mongo getMongoInstance() {
        return mongo;
    }

    public static Morphia getMorphiaInstance() {
        return morphia;
    }

    public static MongoConfig getMongoConfig() {
        return mongoConfig;
    }
}
