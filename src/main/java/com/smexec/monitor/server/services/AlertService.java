package com.smexec.monitor.server.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smexec.monitor.shared.Alert;

public class AlertService {

    private static Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    private Map<Integer, Alert> alertsMap = new HashMap<Integer, Alert>();

    private static AtomicInteger alertCounter = new AtomicInteger();

    public LinkedList<Alert> getAlertsAfter(int alertId) {
        LinkedList<Alert> alerts = new LinkedList<Alert>();
        for (int i = alertId + 1; i < alertCounter.get(); i++) {
            if (alertsMap.containsKey(i)) {
                alerts.add(alertsMap.get(i));
            }
        }
        return alerts;
    }

    public void addAlert(Alert alert) {
        alert.setId(alertCounter.getAndIncrement());
        logger.info("Alert added:{}", alert);
        alertsMap.put(alert.getId(), alert);
    }
}
