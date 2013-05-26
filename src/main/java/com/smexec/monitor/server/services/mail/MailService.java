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
package com.smexec.monitor.server.services.mail;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.model.config.AbstractServersConfig;
import com.smexec.monitor.server.model.config.AlertsConfig;
import com.smexec.monitor.server.services.config.IConfigurationService;
import com.smexec.monitor.shared.alert.Alert;

public abstract class MailService<SC extends AbstractServersConfig, SS extends ServerStatus>
    implements IMailService<SS> {

    private static Logger logger = LoggerFactory.getLogger("MailService");

    private static final String MAIL_ENCODING = "UTF-8";
    // private static final String MAIL_TYPE_TEXT = "text/plain; charset=";
    private static final String MAIL_TYPE_HTML = "text/html; charset=";

    @Inject
    private IConfigurationService<SC> configurationService;

    private String alerTemplate = "{1}, {2}";

    /**
     * local queue of pending mails
     */
    private LinkedBlockingQueue<MailItem> mailQueue = new LinkedBlockingQueue<MailService.MailItem>();

    public MailService() {
        Thread dispatcher = new Thread(new Runnable() {

            @Override
            public void run() {
                do {
                    try {
                        MailItem take = mailQueue.take();
                        sendAlertMail(take);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                } while (true);
            }
        }, "MAIL_DISPATCHER");
        dispatcher.start();

        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AlertTemplate.html");
            byte[] bytes = new byte[resourceAsStream.available()];
            resourceAsStream.read(bytes);
            String temp = new String(bytes);
            alerTemplate = temp;
            resourceAsStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.smexec.monitor.server.services.mail.IMailService#sendMail(com.smexec.monitor.server.services.mail
     * .MailService.MailItem)
     */
    @Override
    public void sendMail(MailItem mailItem) {
        try {
            String mailType = MAIL_TYPE_HTML;

            MimeMessage msg = new MimeMessage(getSession());

            msg.setContent(mailItem.getBody(), mailType + MAIL_ENCODING);

            msg.setFrom(new InternetAddress(mailItem.getFromAddress(), mailItem.getFromName(), MAIL_ENCODING));

            msg.setSubject(mailItem.getSubject(), MAIL_ENCODING);

            for (String to : mailItem.getToAddressList()) {
                msg.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(to));
            }
            logger.debug("sending mail with subject:{}.", (mailItem.getSubject()));
            Transport.send(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendAlertMail(MailItem mailItem) {
        try {
            AlertsConfig ac = configurationService.getServersConfig().getAlertsConfig();

            String mailType = MAIL_TYPE_HTML;

            MimeMessage msg = new MimeMessage(getSession());

            msg.setContent(mailItem.getBody(), mailType + MAIL_ENCODING);

            msg.setFrom(new InternetAddress(ac.getFromAddress(), ac.getFromName(), MAIL_ENCODING));

            msg.setSubject(ac.getSubjectPrefix() + mailItem.getSubject(), MAIL_ENCODING);

            for (String to : ac.getToAddressList()) {
                msg.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(to));
            }
            logger.debug("sending alert mail with subject:{}.", (ac.getSubjectPrefix() + " " + mailItem.getSubject()));
            Transport.send(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.smexec.monitor.server.services.mail.IMailService#sendAlert(com.smexec.monitor.shared.alert.Alert,
     * SS)
     */
    @Override
    public boolean sendAlert(Alert alert, SS ss) {
        try {
            if (configurationService.getServersConfig().getAlertsConfig().isEnabled()) {
                String body = createAlerMailBody(alert, ss);
                mailQueue.offer(new MailItem(" [" + configurationService.getServersConfig().getName() + "] [" + alert.getServerName() + "] "
                                             + alert.getMessage(), body));
                logger.info("Alert:{} added to mail queue", alert.getId());
                return true;
            } else {
                logger.info("Skipping mailing, disabled");
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    private String createAlerMailBody(Alert alert, SS ss) {
        try {
            StringBuilder sb = new StringBuilder();
            if (ss.isConnected() && !ss.isFirstTimeAccess()) {
                sb.append("<tr><td>Server Up Time: </td><td>").append(ClientStringFormatter.formatMilisecondsToHours(ss.getUpTime())).append(" </td></tr> ");
                sb.append("<tr><td>CPU: </td><td>").append(ss.getCpuUtilization().getLastPercent().getUsage()).append("% </td></tr> ");
                sb.append("<tr><td>System Load AVG: </td><td>").append(ss.getCpuUtilization().getLastPercent().getSystemLoadAverage()).append(" </td></tr> ");
                sb.append("<tr><td>Memory Usage: </td><td>").append(ss.getLastMemoryUsage().getPercentage()).append("% </td></tr> ");
                sb.append("<tr><td>Detailed Memory Usage: </td><td>").append(ss.getMemoryState().replace("\n", "</br>")).append(" </td></tr> ");
            }

            StringBuilder details = new StringBuilder();
            details.append("<tr><td>ID:</td><td>").append(alert.getId()).append(" </td></tr> ");
            details.append("<tr><td>Message:</td><td>").append(alert.getMessage()).append(" </td></tr> ");
            details.append("<tr><td>Details:</td><td>").append(alert.getDetails() == null ? "" : alert.getDetails()).append(" </td></tr> ");
            details.append("<tr><td>Time:</td><td>").append(new Date(alert.getAlertTime())).append(" </td></tr> ");
            details.append("<tr><td>Server:</td><td>").append(alert.getServerCode() + ", " + alert.getServerName()).append(" </td></tr> ");
            details.append("<tr><td>Type:</td><td>").append(alert.getAlertType()).append(" </td></tr> ");

            String ret = alerTemplate.replace("{1}", details.toString()).replace("{2}", sb.toString());
            return ret;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return alert.toString();
        }
    }

    class SMTPAuthenticator
        extends Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            AlertsConfig ac = configurationService.getServersConfig().getAlertsConfig();
            // sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
            return new PasswordAuthentication(ac.getUserName(), ac.getPassword());
        }
    }

    public static class MailItem {

        String subject;
        String body;
        String fromAddress;
        String fromName;
        List<String> toAddressList;

        private MailItem(String subject, String body) {
            super();
            this.subject = subject;
            this.body = body;
        }

        public MailItem(String subject, String body, String fromAddress, String fromName, List<String> toAddressList) {
            super();
            this.subject = subject;
            this.body = body;
            this.fromAddress = fromAddress;
            this.fromName = fromName;
            this.toAddressList = toAddressList;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public String getFromName() {
            return fromName;
        }

        public List<String> getToAddressList() {
            return toAddressList;
        }
    }

    private Session getSession() {
        AlertsConfig ac = configurationService.getServersConfig().getAlertsConfig();

        Properties props = new Properties();
        props.put("mail.smtp.host", ac.getMailServerAddress());
        props.put("mail.smtp.port", ac.getMainServerPort());
        props.put("mail.smtp.auth", ac.isAuthenticate() ? "true" : "false");

        if (ac.isAuthenticate()) {
            props.put("mail.smtp.user", ac.getUserName());
            props.put("mail.smtp.password", ac.getPassword());
        }
        props.put("mail.smtp.connectiontimeout", "10000");

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.submitter", "LOGIN");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.allow8bitmime", "true");
        props.put("mail.smtp.socketFactory.port", ac.getMainServerPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.SocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "true");

        return javax.mail.Session.getInstance(props, new SMTPAuthenticator());

    }

}
