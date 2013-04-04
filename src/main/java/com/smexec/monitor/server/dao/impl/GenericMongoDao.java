package com.smexec.monitor.server.dao.impl;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.smexec.monitor.server.mongodb.MongoSessionFactory;

public abstract class GenericMongoDao<T, K>
    extends BasicDAO<T, K> {

    public GenericMongoDao() {
        super(MongoSessionFactory.getMongoInstance(), MongoSessionFactory.getMorphiaInstance(), MongoSessionFactory.getMongoConfig().getDatabaseName());
    }

}
