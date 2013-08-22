package org.clevermore.monitor.server.tasks;

import java.util.List;

import org.clevermore.monitor.server.model.DatabaseServer;
import org.clevermore.monitor.server.model.IConnectedServersState;
import org.clevermore.monitor.server.model.ServerStatus;
import org.clevermore.monitor.server.model.config.AbstractServersConfig;
import org.clevermore.monitor.server.model.config.ValidateCertificates;
import org.clevermore.monitor.server.services.alert.IAlertService;
import org.clevermore.monitor.server.services.config.IConfigurationService;
import org.clevermore.monitor.server.utils.SslUtils;
import org.clevermore.monitor.shared.alert.AlertType;
import org.clevermore.monitor.shared.certificate.Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public abstract class AbstractCertificatesScanner<SS extends ServerStatus, SC extends AbstractServersConfig, DS extends DatabaseServer>
    implements ICertificateScanner {

    private static Logger logger = LoggerFactory.getLogger("AbstractCertificatesScanner");

    @Inject
    private IConfigurationService<SC> configurationService;

    @Inject
    private IConnectedServersState<SS, DS> connectedServersState;

    @Inject
    private IAlertService<SS> alertService;

    @Override
    public void run() {
        ValidateCertificates validateCertificates = configurationService.getServersConfig().getValidateCertificates();
        for (String server : validateCertificates.getServers()) {
            try {
                logger.info("Validting certificate for server:{}", server);
                String arr[] = server.split(":");
                List<Certificate> certificates = SslUtils.getCertificates(arr[0], Integer.valueOf(arr[1]));
                connectedServersState.addCertificate(server, certificates);
                for (Certificate c : certificates) {
                    long upTo = c.getValidTo().getTime();
                    long left = (upTo - System.currentTimeMillis());
                    if (left > 0 && left < (30L * 24 * 60 * 60 * 1000)) {
                        // still valid
                        alertService.createAndAddAlert("Certificate is about to expire:" + c,
                                                       server,
                                                       -1,
                                                       c.getCommonName(),
                                                       System.currentTimeMillis(),
                                                       AlertType.CERTIFICATE_ABOUT_TO_EXPIRE,
                                                       null);
                        c.setAlertRaised(true);
                    } else if (left <= 0) {
                        // expired
                        alertService.createAndAddAlert("Certificate is expired:" + c,
                                                       server,
                                                       -1,
                                                       c.getCommonName(),
                                                       System.currentTimeMillis(),
                                                       AlertType.CERTIFICATE_EXPIRED,
                                                       null);
                        c.setAlertRaised(true);
                    }
                }
                logger.info("Certificates:{}", certificates);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
