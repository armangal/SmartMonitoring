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

import java.util.LinkedList;

import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.shared.alert.Alert;
import org.clevermore.monitor.shared.alert.IAlertType;


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
     */
    Alert createAndAddAlert(String message, String details, int serverCode, String serverName, long alertTime, IAlertType alertType, SS serverStatus);

    LinkedList<Alert> getAlertsList();

}
