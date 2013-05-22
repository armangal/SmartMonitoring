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
package com.smexec.monitor.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.smexec.monitor.server.model.config.MongoConfig;
import com.smexec.monitor.server.mongodb.MongoSessionFactory;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.server.services.persistence.IPersistenceService;
import com.smexec.monitor.server.services.persistence.MongoDbPersitenceService;

public class MongoDbModule
    extends AbstractModule {

    private MongoConfig mongoConfig;

    public MongoDbModule() {
        mongoConfig = ConfigurationService.getInstance().getMongoConfig();
    }

    @Override
    protected void configure() {

        bindInterceptor(Matchers.subclassesOf(IPersistenceService.class), Matchers.any(), new PersistenceInterceptor(mongoConfig));

        bind(IPersistenceService.class).to(MongoDbPersitenceService.class).in(Singleton.class);

        // bind session and install DAOs
        if (mongoConfig.getEnabled()) {
            bind(MongoSessionFactory.class).asEagerSingleton();

            install(new MongoDbDaoModule());
        }

    }

}
