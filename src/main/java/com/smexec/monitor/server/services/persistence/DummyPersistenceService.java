package com.smexec.monitor.server.services.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.dao.entities.ServerStatEntity;

public class DummyPersistenceService
    implements IPersistenceService {

    private static Logger logger = LoggerFactory.getLogger("DummyPersistenceService");

    @Override
    public void saveServerStat(ServerStatEntity entity) {
        logger.debug("Skipping saveing server stat");
    }
}
