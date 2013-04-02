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
package com.smexec.monitor.server.services.alert;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.server.services.mail.MailService;
import com.smexec.monitor.shared.alert.Alert;

public class AlertService {

    private static Logger logger = LoggerFactory.getLogger("AlertService");

    private LinkedList<Alert> alertsList = new LinkedList<Alert>();

    private static AtomicInteger alertCounter = new AtomicInteger();

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private MailService mailService;

    /**
     * Return the latest alerts from a given alertId.<br>
     * The result is limited to 1000 items, meaning if there're 2K alerts, then only the latest 1K will be
     * returned.
     * 
     * @param alertId
     * @return
     */
    public LinkedList<Alert> getAlertsAfter(int alertId) {
        if (alertId == -1) {
            // first time, take the last 1000
            alertId = alertCounter.get() > 1000 ? alertCounter.get() - 1000 : 0;
        } else if (alertCounter.get() - alertId > 1000) {
            // if we have more than 1000 to return, cut it
            alertId = alertCounter.get() - 1000;
        }

        LinkedList<Alert> alerts = new LinkedList<Alert>();
        // starting from back
        synchronized (alertsList) {

            Iterator<Alert> it = alertsList.descendingIterator();
            for (int i = alertId + 1; i < alertCounter.get(); i++) {
                alerts.add(it.next());
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

    /**
     * Use it to add alert message to local storage. <br>
     * Message will be communicated later to client. <br>
     * Email alerts are sent as well if applicable.
     * 
     * @param alert
     */
    public <SS extends ServerStataus> void addAlert(Alert alert, SS ss) {
        alert.setId(alertCounter.getAndIncrement());

        if (alert.getAlertType().sendMail() && ss.canSendAlert(alert.getAlertType())) {
            mailService.sendAlert(alert, ss);
        }

        logger.warn("Alert added:{}", alert);
        synchronized (alertsList) {
            alertsList.add(alert);
            if (alertsList.size() > configurationService.getMaxInMemoryAlerts()) {
                alertsList.remove();
            }
        }
    }
}
