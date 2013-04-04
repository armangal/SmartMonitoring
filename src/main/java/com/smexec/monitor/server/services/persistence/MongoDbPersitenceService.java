package com.smexec.monitor.server.services.persistence;

import com.google.inject.Inject;
import com.smexec.monitor.server.dao.IServerStatEntityDao;
import com.smexec.monitor.server.dao.entities.ServerStatEntity;
import com.smexec.monitor.server.model.config.ServersConfig;

public class MongoDbPersitenceService implements IPersistenceService {

    @Inject
    private IServerStatEntityDao serverStatDao;
    @Inject
    private ServersConfig config;

    /**
     * Save server entry data
     * @param entity
     */
    public void saveServerStat(ServerStatEntity entity) {
        if (!config.getMongoConfig().getEnabled()) {
            return;
        }
        serverStatDao.save(entity);
    }
}
