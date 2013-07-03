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
package com.smexec.monitor.server.tasks.impl;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.server.model.DatabaseServer;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.AbstractServersConfig;
import com.smexec.monitor.server.model.config.MailUpdaterConfig;
import com.smexec.monitor.server.services.alert.IAlertService;
import com.smexec.monitor.server.services.config.IConfigurationService;
import com.smexec.monitor.server.services.mail.IMailService;
import com.smexec.monitor.server.services.mail.MailService.MailItem;
import com.smexec.monitor.shared.alert.Alert;
import com.smexec.monitor.shared.config.Version;

public abstract class AbstractPeriodicalUpdater<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    implements Runnable {

    public static Logger logger = LoggerFactory.getLogger("PeriodicalUpdater");

    @Inject
    private IConnectedServersState<SS, DS> connectedServersState;

    @Inject
    private IConfigurationService<SC> configurationService;

    @Inject
    private IMailService<SS> mailService;

    @Inject
    private IAlertService<SS> alertService;

    private int lastAlertMailed = -1;

    private String updateTemplate;

    public AbstractPeriodicalUpdater() {
        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("PeriodicalUpdateTemplate.html");
            byte[] bytes = new byte[resourceAsStream.available()];
            resourceAsStream.read(bytes);
            String temp = new String(bytes);
            updateTemplate = temp;
            logger.info("Loaded PeriodicalUpdateTemplate:{}", updateTemplate);
            resourceAsStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {

            logger.info("Starting periodical update");
            StringBuilder sb = new StringBuilder();
            List<SS> allServers = connectedServersState.getAllServers();
            String body = updateTemplate;

            Collections.sort(allServers, new Comparator<SS>() {

                @Override
                public int compare(SS o1, SS o2) {
                    return o1.getServerConfig().getName().compareTo(o2.getServerConfig().getName());
                }
            });

            DecimalFormat dnf = new DecimalFormat("###.##");

            for (SS ss : allServers) {
                if (ss.isConnected() && !ss.isFirstTimeAccess()) {
                    sb.append("<tr>");
                    sb.append("<td>").append(ss.getServerConfig().getServerCode() + ", " + ss.getServerConfig().getName()).append("</td>");
                    sb.append("<td>").append(ClientStringFormatter.formatMilisecondsToHours(ss.getUpTime())).append("</td>");
                    sb.append("<td>")
                      .append(ClientStringFormatter.formatMBytes(ss.getLastMemoryUsage().getUsed()) + " of "
                              + ClientStringFormatter.formatMBytes(ss.getLastMemoryUsage().getMax()) + " MB")
                      .append("</td>");

                    sb.append("<td>").append(dnf.format(ss.getLastMemoryUsage().getPercentage()) + "%").append("</td>");
                    sb.append("<td>").append(dnf.format(ss.getCpuUtilization().getLastPercent().getUsage()) + "%").append("</td>");
                    double sla = ss.getCpuUtilization().getLastPercent().getSystemLoadAverage();
                    sb.append("<td>").append(sla == -1 ? "N/A" : dnf.format(sla)).append("</td>");
                    sb.append("<td>").append(connectedServersState.getExtraServerDetails(ss.getServerConfig().getServerCode())).append("</td>");

                    sb.append("</tr>");

                } else {
                    sb.append("<tr>");
                    sb.append("<td>").append(ss.getServerConfig().getServerCode() + ", " + ss.getServerConfig().getName()).append("</td>");
                    sb.append("<td>").append("Offline").append("</td>");
                    sb.append("<td>").append("Offline").append("</td>");
                    sb.append("<td>").append("Offline").append("</td>");
                    sb.append("<td>").append("Offline").append("</td>");
                    sb.append("<td>").append("Offline").append("</td>");
                    sb.append("<td>").append("Offline").append("</td>");
                    sb.append("</tr>");
                }
            }

            logger.info("Periodical update, servers:{}", sb.toString());
            String extraInfo = getExtraInfo();
            logger.info("Periodical update, extraInfo:{}", extraInfo);
            body = body.replace("{2}", sb.toString()).replace("{extra}", extraInfo).replace("{3}", getAlerts()).replace("{version}", Version.version());

            MailUpdaterConfig mailUpdaterConfig = configurationService.getServersConfig().getMailUpdaterConfig();

            MailItem mailItem = new MailItem("Network Update: " + configurationService.getServersConfig().getName(),
                                             body,
                                             mailUpdaterConfig.getFromAddress(),
                                             mailUpdaterConfig.getFromName(),
                                             mailUpdaterConfig.getTo());
            mailService.sendMail(mailItem);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public abstract String getExtraInfo();

    public IConnectedServersState<SS, DS> getConnectedServersState() {
        return connectedServersState;
    }

    public IMailService<SS> getMailService() {
        return mailService;
    }

    private String getAlerts() {
        try {
            LinkedList<Alert> alertsAfter = alertService.getAlertsAfter(lastAlertMailed, 500);
            StringBuilder sb = new StringBuilder();
            for (Alert a : alertsAfter) {
                sb.append("<tr>");

                sb.append("<td>");
                sb.append(a.getId());
                sb.append("</td>");

                sb.append("<td>");
                sb.append(a.getMessage() + " [" + a.getServerName() + "]");
                sb.append("\n");
                sb.append(a.getDetails() == null ? "No Details" : a.getDetails());
                sb.append("</td>");

                sb.append("<td>");
                sb.append(a.getServerCode());
                sb.append("</td>");

                sb.append("<td>");
                sb.append(a.getAlertTimeStr());
                sb.append("</td>");

                sb.append("</tr>");

                if (a.getId() > lastAlertMailed) {
                    lastAlertMailed = a.getId();
                }
            }

            return sb.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "<tr><td colspan=4>" + e.getMessage() + "</td></tr>";
        }
    }
}
