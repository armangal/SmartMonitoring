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
package com.smexec.monitor.server.services.rpc;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.AbstractServersConfig;
import com.smexec.monitor.server.services.config.IConfigurationService;
import com.smexec.monitor.shared.errors.AuthenticationException;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public abstract class AbstractMonitoringService<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    extends RemoteServiceServlet {

    public Logger logger;

    public static final String AUTHENTICATED = "authenticated";

    @Inject
    private IConnectedServersState<SS, DS> connectedServersState;

    @Inject
    private IConfigurationService<SC> configurationService;

    public AbstractMonitoringService() {
        logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
        GuiceUtils.getInjector().injectMembers(this);
    }

    /**
     * @param admin - check if the user is admin
     * @throws AuthenticationException 
     */
    protected void checkAuthenticated(boolean admin) throws AuthenticationException {
        HttpSession session = getThreadLocalRequest().getSession();
        String auth = session.getAttribute(AUTHENTICATED).toString();
        if (auth == null || (!auth.equals("1") && !auth.equals("2"))) {
            logger.info("Session is not authenticated");
            throw new AuthenticationException("Session not authenticated, please refresh the browser.");
        }

        if (admin && !auth.equals("1")) {
            logger.info("Session is authenticated but not admin");
            throw new AuthenticationException("Session is authenticated but not admin");
        }

        if (!admin && (!auth.equals("1") && !auth.equals("2"))) {
            logger.info("Session is authenticated but not admin");
            throw new AuthenticationException("Session is authenticated but not admin");
        }

    }

    public IConnectedServersState<SS, DS> getConnectedServersState() {
        return connectedServersState;
    }

    public IConfigurationService<SC> getConfigurationService() {
        return configurationService;
    }

}
