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
