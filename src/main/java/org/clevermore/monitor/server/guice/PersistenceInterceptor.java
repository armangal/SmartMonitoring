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
package org.clevermore.monitor.server.guice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.clevermore.monitor.server.model.config.MongoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersistenceInterceptor
    implements MethodInterceptor {

    private static Logger logger = LoggerFactory.getLogger("PersistenceInterceptor");

    private MongoConfig mongoConfig;

    public PersistenceInterceptor(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }

    @Override
    public Object invoke(MethodInvocation invocation)
        throws Throwable {

        if (mongoConfig.getEnabled()) {
            invocation.proceed();
        } else {
            logger.debug("Skipping call to DB service:{}, method:{}", invocation.getThis().getClass().getSimpleName(), invocation.getMethod().getName());
        }
        return null;
    }

}
