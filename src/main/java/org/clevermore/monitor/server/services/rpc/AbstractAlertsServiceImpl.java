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
package org.clevermore.monitor.server.services.rpc;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.clevermore.SmartExecutor;
import org.clevermore.TaskMetadata;
import org.clevermore.monitor.client.AlertsService;
import org.clevermore.monitor.server.constants.SmartPoolsMonitoring;
import org.clevermore.monitor.server.model.DatabaseServer;
import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.server.model.config.AbstractServersConfig;
import org.clevermore.monitor.server.services.alert.IAlertService;
import org.clevermore.monitor.shared.alert.Alert;
import org.clevermore.monitor.shared.alert.RefreshAlertsRequest;
import org.clevermore.monitor.shared.alert.RefreshAlertsResponse;
import org.clevermore.monitor.shared.errors.AuthenticationException;

import com.google.inject.Inject;

/**
 * The server side implementation of the monitoring RPC service.
 */
@SuppressWarnings("serial")
public abstract class AbstractAlertsServiceImpl<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    extends AbstractMonitoringService<SS, SC, DS>
    implements AlertsService {

    private static ScheduledFuture<Boolean> alertEnablerFuture;

    @Inject
    private IAlertService<SS> alertService;

    @Inject
    private SmartExecutor smartExecutor;

    @Override
    public RefreshAlertsResponse refresh(RefreshAlertsRequest request)
        throws AuthenticationException {
        checkAuthenticated(false);
        logger.info("GetAlerts Request: alertId:{}", request);
        LinkedList<Alert> alertsAfter = getAlertService().getAlertsAfter(request.getLastAlertId(), 1000);
        logger.info("Returning:{} alerts.", alertsAfter.size());
        return new RefreshAlertsResponse(new Date().toString(), alertsAfter);
    }

    public IAlertService<SS> getAlertService() {
        return alertService;
    }

    public Boolean stopAlerts(boolean enable)
        throws AuthenticationException {

        checkAuthenticated(true);
        if (getConfigurationService().getServersConfig().getAlertsConfig().isEnabled() && !enable) {
            // not enabled and we want to turn it off, schedule time for next 30 min that will re-enable the
            // alerts
            if (alertEnablerFuture != null) {
                alertEnablerFuture.cancel(true);
            }
            alertEnablerFuture = smartExecutor.schedule(new AlertEnabler(),
                                                        30,
                                                        TimeUnit.MINUTES,
                                                        TaskMetadata.newMetadata(SmartPoolsMonitoring.GENERAL, "ALRT_ENB", "ALRT_ENB"));
        }
        if (!getConfigurationService().getServersConfig().getAlertsConfig().isEnabled() && enable) {
            if (alertEnablerFuture != null) {
                alertEnablerFuture.cancel(true);
            }
        }
        return getConfigurationService().stopAlerts(enable);
    }

    class AlertEnabler
        implements Callable<Boolean> {

        @Override
        public Boolean call()
            throws Exception {
            logger.info("Re-Enabling alerts");
            getConfigurationService().stopAlerts(true);
            return true;
        }
    }

}
