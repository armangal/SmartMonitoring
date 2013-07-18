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
package org.clevermore.monitor.server.services.alert;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.server.model.config.AbstractServersConfig;
import org.clevermore.monitor.server.services.config.IConfigurationService;
import org.clevermore.monitor.server.services.mail.IMailService;
import org.clevermore.monitor.server.services.persistence.IPersistenceService;
import org.clevermore.monitor.shared.alert.Alert;
import org.clevermore.monitor.shared.alert.IAlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public abstract class AlertService<SS extends ServerStatus, SC extends AbstractServersConfig>
    implements IAlertService<SS> {

    private static Logger logger = LoggerFactory.getLogger("AlertService");

    private LinkedList<Alert> alertsList = new LinkedList<Alert>();

    private static AtomicInteger alertCounter = new AtomicInteger();

    @Inject
    private IConfigurationService<SC> configurationService;

    @Inject
    private IMailService<SS> mailService;

    @Inject
    private IPersistenceService persistenceService;

    /*
     * (non-Javadoc)
     * @see org.clevermore.monitor.server.services.alert.IAlertService#getAlertsAfter(int, int)
     */
    @Override
    public LinkedList<Alert> getAlertsAfter(int alertId, int max) {
        if (alertId == -1) {
            // first time, take the last max
            alertId = alertCounter.get() > max ? alertCounter.get() - max : -1;
        } else if (alertCounter.get() - alertId > max) {
            // if we have more than 1000 to return, cut it
            alertId = alertCounter.get() - max;
        }

        LinkedList<Alert> alerts = new LinkedList<Alert>();
        // starting from back
        synchronized (alertsList) {

            Iterator<Alert> it = alertsList.descendingIterator();
            for (int i = alertId; i < alertCounter.get() && it.hasNext(); i++) {

                Alert a = it.next();
                if (a != null && a.getId() > alertId) {
                    alerts.add(a);
                }
            }
            Collections.sort(alerts, new Comparator<Alert>() {

                @Override
                public int compare(Alert o1, Alert o2) {
                    return o1.getId() - o2.getId();
                }
            });
        }
        return alerts;
    }

    private void addAlert(Alert alert, SS ss) {
        try {
            alert.setId(alertCounter.getAndIncrement());
            boolean mailSent = false;
            if (alert.getAlertType().sendMail() && ss.canSendAlert(alert.getAlertType())) {
                mailSent = mailService.sendAlert(alert, ss);
            }

            logger.warn("Alert added:{}", alert);
            synchronized (alertsList) {
                alertsList.add(alert);
                while (alertsList.size() > configurationService.getMaxInMemoryAlerts()) {
                    alertsList.remove();
                }
                persistenceService.saveAlert(alert, mailSent);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.clevermore.monitor.server.services.alert.IAlertService#createAlert(java.lang.String,
     * java.lang.String, int, java.lang.String, long, org.clevermore.monitor.shared.alert.IAlertType)
     */
    @Override
    public Alert createAndAddAlert(String message, String details, int serverCode, String serverName, long alertTime, IAlertType alertType, SS serverStatus) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Alert alert = new Alert(message, details, serverCode, serverName, alertTime, DATE_FORMAT.format(new Date(alertTime)), alertType);
        addAlert(alert, serverStatus);
        return alert;

    }

    /*
     * (non-Javadoc)
     * @see org.clevermore.monitor.server.services.alert.IAlertService#getAlertsList()
     */
    @Override
    public LinkedList<Alert> getAlertsList() {
        return alertsList;
    }
}
