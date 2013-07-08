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

import java.io.IOException;

import javax.servlet.http.HttpSession;

import com.smexec.monitor.client.GeneralService;
import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.AbstractServersConfig;
import com.smexec.monitor.shared.ServerTimeResult;
import com.smexec.monitor.shared.errors.AuthenticationException;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public abstract class AbstractGeneralServiceImpl<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    extends AbstractMonitoringService<SS, SC, DS>
    implements GeneralService {

    public ServerTimeResult refresh() throws AuthenticationException {
        checkAuthenticated(false);

        return new ServerTimeResult();
    }

    public Boolean authenticate(String userName, String password) {
        logger.info("Authnticating user:{}, pass:{}", userName, password);

        HttpSession session = getThreadLocalRequest().getSession();
        userName = userName.trim();
        password = password.trim();
        int authenticate = getConfigurationService().authenticate(userName, password);
        if (authenticate == 1) {
            session.setAttribute(AUTHENTICATED, "1");
            logger.info("Authnticated admin user:{}, pass:{}", userName, password);
            return true;
        } else if (authenticate == 2) {
            session.setAttribute(AUTHENTICATED, "2");
            logger.info("Authnticated guest user:{}, pass:{}", userName, password);
            return true;
        } else {
            logger.info("NOT Authnticated user:{}, pass:{}", userName, password);
            session.removeAttribute(AUTHENTICATED);
            return false;
        }
    }

    public void logout() {
        HttpSession session = getThreadLocalRequest().getSession();
        logger.info("logging out");
        session.removeAttribute(AUTHENTICATED);
    }

    public String getSettingsXML() {
        try {
            checkAuthenticated(true);
            logger.info("About to load configuration xml");
            return getConfigurationService().getServersConfigXML();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public Boolean saveSettingsXML(String xml) throws AuthenticationException {
        try {
            checkAuthenticated(true);
            logger.info("About to save xml:{}", xml);
            getConfigurationService().saveServersConfigXML(xml);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}
