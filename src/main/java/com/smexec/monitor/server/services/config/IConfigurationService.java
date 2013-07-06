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
package com.smexec.monitor.server.services.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.smexec.monitor.server.model.config.AbstractServersConfig;

public interface IConfigurationService<SC extends AbstractServersConfig> {

    void setServersConfig(SC sc);

    SC getServersConfig();

    String getServersConfigXML()
        throws FileNotFoundException, IOException;

    void saveServersConfigXML(final String xml)
        throws IOException;

    Integer getMaxInMemoryAlerts();

    /**
     * @param toStopServer - meaning if failed to load configs, we will crush the server.
     */
    void loadServersConfig(boolean toStopServer);

    Boolean stopAlerts(boolean enable);
    
    int authenticate(final String userName, final String password);

}
