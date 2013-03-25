package com.smexec.monitor.server.services.alert;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.services.config.ConfigurationService;
import com.smexec.monitor.shared.Alert;

public class AlertService {

    private static Logger logger = LoggerFactory.getLogger("AlertService");

    private LinkedList<Alert> alertsList = new LinkedList<Alert>();

    private static AtomicInteger alertCounter = new AtomicInteger();

    @Inject
    private ConfigurationService configurationService;

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
        return alerts;
    }

    /**
     * use it to add alert message to local storage. Message will be communicated later to client
     * 
     * @param alert
     */
    public void addAlert(Alert alert) {
        alert.setId(alertCounter.getAndIncrement());
        logger.warn("Alert added:{}", alert);
        alertsList.add(alert);
        if (alertsList.size() > configurationService.getMaxInMemoryAlerts()) {
            alertsList.remove();
        }
    }
}
