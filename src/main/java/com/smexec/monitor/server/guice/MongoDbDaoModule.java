package com.smexec.monitor.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.smexec.monitor.server.dao.IServerStatEntityDao;
import com.smexec.monitor.server.dao.impl.ServerStatEntityDaoImpl;

public class MongoDbDaoModule
    extends AbstractModule {

    @Override
    protected void configure() {
        bind(IServerStatEntityDao.class).to(ServerStatEntityDaoImpl.class).in(Singleton.class);
    }
    
}
