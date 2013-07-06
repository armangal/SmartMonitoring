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

import java.util.Date;
import java.util.LinkedList;

import com.google.inject.Inject;
import com.smexec.monitor.client.AlertsService;
import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.AbstractServersConfig;
import com.smexec.monitor.server.services.alert.IAlertService;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.alert.RefreshAlertsRequest;
import com.smexec.monitor.shared.alert.RefreshAlertsResponse;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public abstract class AbstractAlertsServiceImpl<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    extends AbstractMonitoringService<SS, SC, DS>
    implements AlertsService {

    @Inject
    private IAlertService<SS> alertService;

    @Override
    public RefreshAlertsResponse refresh(RefreshAlertsRequest request) {
        checkAuthenticated(false);
        logger.info("GetAlerts Request: alertId:{}", request);
        LinkedList<Alert> alertsAfter = getAlertService().getAlertsAfter(request.getLastAlertId(), 1000);
        logger.info("Returning:{} alerts.", alertsAfter.size());
        return new RefreshAlertsResponse(new Date().toString(), alertsAfter);
    }

    public IAlertService<SS> getAlertService() {
        return alertService;
    }

    public Boolean stopAlerts(boolean enable) {
        checkAuthenticated(true);
        return getConfigurationService().stopAlerts(enable);
    }

}
