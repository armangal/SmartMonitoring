package com.smexec.monitor.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.smexec.monitor.server.model.config.ServersConfig;
import com.smexec.monitor.server.mongodb.MongoSessionFactory;
import com.smexec.monitor.server.services.persistence.DummyPersistenceService;
import com.smexec.monitor.server.services.persistence.IPersistenceService;
import com.smexec.monitor.server.services.persistence.MongoDbPersitenceService;

public class MongoDbModule
    extends AbstractModule {

    private ServersConfig serversConfig;

    public MongoDbModule(ServersConfig serversConfig) {
        this.serversConfig = serversConfig;
    }

    @Override
    protected void configure() {

        if (serversConfig.getMongoConfig().getEnabled()) {
            bind(MongoSessionFactory.class).asEagerSingleton();
            bind(IPersistenceService.class).to(MongoDbPersitenceService.class).in(Singleton.class);

            install(new MongoDbDaoModule());
        } else {
            bind(IPersistenceService.class).to(DummyPersistenceService.class).in(Singleton.class);
        }

    }

}
