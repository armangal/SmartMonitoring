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
package org.clevermore.monitor.server.services.persistence;

import org.clevermore.monitor.server.dao.IAlertEntityDao;
import org.clevermore.monitor.server.dao.IServerStatEntityDao;
import org.clevermore.monitor.server.dao.entities.AlertEntity;
import org.clevermore.monitor.server.dao.entities.ServerStatsEntity;
import org.clevermore.monitor.shared.alert.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class MongoDbPersitenceService
    implements IPersistenceService {

    private static Logger logger = LoggerFactory.getLogger("MongoDbPersitenceService");

    @Inject(optional = true)
    private IServerStatEntityDao serverStatDao;

    @Inject(optional = true)
    private IAlertEntityDao alertEntityDao;

    /**
     * Save server entry data
     * 
     * @param entity
     */
    @Override
    public void saveServerStat(ServerStatsEntity entity) {
        logger.debug("Saving:{}", entity);
        serverStatDao.save(entity);
    }

    @Override
    public void saveAlert(Alert alert, boolean mailSent) {
        AlertEntity ae = new AlertEntity(alert.getAlertTime(),
                                         alert.getServerCode(),
                                         alert.getServerName(),
                                         alert.getId(),
                                         alert.getMessage(),
                                         alert.getDetails(),
                                         alert.getAlertType().getId(),
                                         alert.getAlertType().getName(),
                                         mailSent);

        alertEntityDao.save(ae);
    }
}
