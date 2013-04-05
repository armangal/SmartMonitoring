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
import com.smexec.monitor.server.dao.IAlertEntityDao;
import com.smexec.monitor.server.dao.IServerStatEntityDao;
import com.smexec.monitor.server.dao.impl.AlertEntityDaoImpl;
import com.smexec.monitor.server.dao.impl.ServerStatEntityDaoImpl;

public class MongoDbDaoModule
    extends AbstractModule {

    @Override
    protected void configure() {
        bind(IServerStatEntityDao.class).to(ServerStatEntityDaoImpl.class).in(Singleton.class);
        bind(IAlertEntityDao.class).to(AlertEntityDaoImpl.class).in(Singleton.class);
    }

}
