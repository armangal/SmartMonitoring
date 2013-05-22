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
package com.smexec.monitor.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.services.alert.AlertService;
import com.smexec.monitor.shared.alert.Alert;

public class AlertsExporter
    extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = ",";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment;filename=\"alerts.csv\"");
        PrintWriter out = response.getWriter();

        AlertService alertService = GuiceUtils.getInjector().getInstance(AlertService.class);
        LinkedList<Alert> alertsList = alertService.getAlertsList();

        out.append(format("ID"));
        out.append(SEPARATOR);
        out.append(format("Message"));
        out.append(SEPARATOR);
        out.append(format("Details"));
        out.append(SEPARATOR);
        out.append(format("Server Name"));
        out.append(SEPARATOR);
        out.append(format("Time"));
        out.append(SEPARATOR);
        out.append(format("Type"));
        out.println();

        for (Alert alert : alertsList) {

            out.append(format(Integer.toString(alert.getId())));
            out.append(SEPARATOR);
            out.append(format(alert.getMessage()));
            out.append(SEPARATOR);
            out.append(format(alert.getDetails()));
            out.append(SEPARATOR);
            out.append(format(alert.getServerName()));
            out.append(SEPARATOR);
            out.append(format(alert.getAlertTimeStr()));
            out.append(SEPARATOR);
            out.append(format(alert.getAlertType().getName()));
            out.println();
        }
        out.flush();
    }

    private static String format(String value) {
        String field = String.valueOf(value).replace("\"", "\"\"");
        if (field.indexOf(SEPARATOR) > -1 || field.indexOf('"') > -1) {
            field = '"' + field + '"';
        }
        return field;

    }
}
