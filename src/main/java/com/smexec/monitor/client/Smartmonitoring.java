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
package com.smexec.monitor.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;

public class Smartmonitoring
    extends AbstractEntryPoint<ConnectedServer, AbstractFullRefreshResult<ConnectedServer>> {

    private static final MonitoringServiceAsync<ConnectedServer, AbstractFullRefreshResult<ConnectedServer>> service = GWT.create(MonitoringServiceStd.class);

    public Smartmonitoring() {
        super(service);
        Log.debug("Smartmonitoring created");
    }
}
