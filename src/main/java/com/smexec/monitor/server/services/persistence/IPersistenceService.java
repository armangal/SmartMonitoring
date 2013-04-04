package com.smexec.monitor.server.services.persistence;

import com.smexec.monitor.server.dao.entities.ServerStatEntity;

public interface IPersistenceService {

    void saveServerStat(ServerStatEntity entity);
}
