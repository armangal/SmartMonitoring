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
package com.smexec.monitor.server.tasks.impl;

import javax.xml.bind.JAXBException;

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.model.config.ServerConfig;
import com.smexec.monitor.server.model.config.ServerGroup;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.RefreshResult;

public class JMXConnectorThread
    extends AbstractJMXConnectorThread<ServerStataus, ConnectedServer, RefreshResult> {

    public JMXConnectorThread()
        throws JAXBException {
        super();
    }

    @Override
    public ServerStataus getServerStatus(ServerConfig sc, final ServerGroup serverGroup) {
        // keeping history stats for 24 hours
        return new ServerStataus(sc, serverGroup, (24 * 60 * 3), (24 * 60 * 3));
    }
}
