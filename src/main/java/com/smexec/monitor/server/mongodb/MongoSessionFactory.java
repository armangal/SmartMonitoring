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
    public MongoSessionFactory(ConfigurationService confService) {
        mongoConfig = confService.getServersConfig().getMongoConfig();

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
