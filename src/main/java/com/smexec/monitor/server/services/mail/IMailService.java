package com.smexec.monitor.server.services.mail;

import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.server.services.mail.MailService.MailItem;
import com.smexec.monitor.shared.alert.Alert;

public interface IMailService<SS extends ServerStatus> {

    void sendMail(MailItem mailItem);

    /**
     * @param alert
     * @param ss
     * @return - true if the alert was sent
     */
    boolean sendAlert(Alert alert, SS ss);

}
