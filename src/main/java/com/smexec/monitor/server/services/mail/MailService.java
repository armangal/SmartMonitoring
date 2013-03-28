package com.smexec.monitor.server.services.mail;

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
import com.smexec.monitor.server.model.config.AlertsConfig;
import com.smexec.monitor.server.services.config.ConfigurationService;

public class MailService {

    private static Logger logger = LoggerFactory.getLogger("MailService");

    private static final String MAIL_ENCODING = "UTF-8";
    // private static final String MAIL_TYPE_TEXT = "text/plain; charset=";
    private static final String MAIL_TYPE_HTML = "text/html; charset=";

    @Inject
    private ConfigurationService configurationService;

    private LinkedBlockingQueue<MailItem> mailQueue = new LinkedBlockingQueue<MailService.MailItem>();

    public MailService() {
        Thread dispatcher = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    do {
                        MailItem take = mailQueue.take();
                        sendAlert(take);
                    } while (true);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }, "MAIL_DISPATCHER");
        dispatcher.start();
    }

    private void sendAlert(MailItem mailItem) {
        try {
            AlertsConfig ac = configurationService.getServersConfig().getAlertsConfig();

            String mailType = MAIL_TYPE_HTML;

            MimeMessage msg = new MimeMessage(getSession());

            msg.setContent(mailItem.getBody(), mailType + MAIL_ENCODING);

            msg.setFrom(new InternetAddress(ac.getFromAddress(), ac.getFromName(), MAIL_ENCODING));

            msg.setSubject(ac.getSubjectPrefix() + " ["+ configurationService.getServersConfig().getName() +"] " + mailItem.getSubject(), MAIL_ENCODING);

            for (String to : ac.getToAddressList()) {
                msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            }
            logger.debug("sending alert mail with subject:{}.", (ac.getSubjectPrefix() + " " + mailItem.getSubject()));
            Transport.send(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendAlert(String subject, String body) {
        if (configurationService.getServersConfig().getAlertsConfig().isEnabled()) {
            mailQueue.offer(new MailItem(subject, body));
            logger.info("Alert:{} added to queue", subject);
        } else {
            logger.info("Skipping mailing, disabled");
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

    private static class MailItem {

        String subject;
        String body;

        private MailItem(String subject, String body) {
            super();
            this.subject = subject;
            this.body = body;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
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
