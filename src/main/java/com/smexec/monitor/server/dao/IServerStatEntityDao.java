package com.smexec.monitor.server.dao;

import org.bson.types.ObjectId;

import com.smexec.monitor.server.dao.entities.ServerStatEntity;


public interface IServerStatEntityDao extends IMongoDbDao<ServerStatEntity, ObjectId>{

}
