package com.smexec.monitor.server.dao.impl;

import org.bson.types.ObjectId;

import com.smexec.monitor.server.dao.IServerStatEntityDao;
import com.smexec.monitor.server.dao.entities.ServerStatEntity;

public class ServerStatEntityDaoImpl
    extends GenericMongoDao<ServerStatEntity, ObjectId>
    implements IServerStatEntityDao {

}
