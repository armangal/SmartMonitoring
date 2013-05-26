package com.smexec.monitor.server.services.alert;

import java.util.LinkedList;

import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.alert.IAlertType;

public interface IAlertService<SS extends ServerStatus> {

    /**
     * Return the latest alerts from a given alertId.<br>
     * The result is limited to 1000 items, meaning if there're 2K alerts, then only the latest 1K will be
     * returned.
     * 
     * @param alertId
     * @return
     */
    LinkedList<Alert> getAlertsAfter(int alertId, int max);

    /**
     * Use it to add alert message to local storage. <br>
     * Message will be communicated later to client. <br>
     * Email alerts are sent as well if applicable.
     * 
     * @param alert
     */
    void addAlert(Alert alert, SS ss);

    Alert createAlert(String message, String details, int serverCode, String serverName, long alertTime, IAlertType alertType);

    LinkedList<Alert> getAlertsList();

}
