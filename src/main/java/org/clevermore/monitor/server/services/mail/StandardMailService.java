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
package org.clevermore.monitor.server.services.mail;

import org.clevermore.monitor.server.model.DatabaseServer;
import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.server.model.config.ServersConfig;

public class StandardMailService
    extends MailService<ServersConfig, ServerStatus, DatabaseServer>
    implements IMailService<ServerStatus> {
    
    
    public StandardMailService() {
        super("org/clevermore/monitor/resources/AlertTemplate.html");
    }

}
