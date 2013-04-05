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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.server.model.config.ServersConfig;

public class PersistenceInterceptor
    implements MethodInterceptor {

    private static Logger logger = LoggerFactory.getLogger("PersistenceInterceptor");

    private ServersConfig serversConfig;

    public PersistenceInterceptor(ServersConfig serversConfig) {
        this.serversConfig = serversConfig;
    }

    @Override
    public Object invoke(MethodInvocation invocation)
        throws Throwable {
       
        if (serversConfig.getMongoConfig().getEnabled()) {
            invocation.proceed();
        } else {
            logger.debug("Skipping call to DB service:{}, method:{}", invocation.getThis().getClass().getSimpleName(), invocation.getMethod().getName());
        }
        return null;
    }

}
