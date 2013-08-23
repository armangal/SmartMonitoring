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
package org.clevermore.monitor.server.utils;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.clevermore.monitor.shared.certificate.Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class that is retrieving ssl certificate information for monitoring tool.
 * 
 * @author Denys Mostovliuk
 */
public class SslUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(SslUtils.class);

    public static void main(String[] args) {
        try {

            System.out.println(getCertificates("channel4.ipokerfr.com", 4437));
            System.out.println(getCertificates("channel.ipokerfr.com", 443));
            System.out.println(getCertificates("channel1.ipoker.com", 4407));
            System.out.println(getCertificates("66.212.242.123", 443));
            System.out.println(getCertificates("66.212.242.115", 5007));
            System.out.println(getCertificates("67.211.103.37", 7204));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Certificate> getCertificates(String host, int port) {
        LOGGER.info("Validating certificate for:>> {}:{}", host, port);
        TrustManager[] trustAll = new TrustManager[] {new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
        }};

        SSLSocket sslsocket = null;
        List<Certificate> result = new ArrayList<>(1);
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAll, null);
            SSLSocketFactory sslsocketfactory = context.getSocketFactory();
            sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);

            LOGGER.info("SSL Socket session:{}", sslsocket.getSession());
            boolean server = true;

            for (java.security.cert.Certificate cert : sslsocket.getSession().getPeerCertificates()) {
                try {
                    if (cert instanceof X509Certificate) {
                        X509Certificate cer = (X509Certificate) cert;
                        LOGGER.info("X509Certificate:{}", cer);
                        Certificate certificate = new Certificate(cer.getType(), cer.getNotBefore(), cer.getNotAfter(), cer.getSerialNumber().toString());
                        certificate.setIssuer(cer.getIssuerDN().getName());
                        String name = cer.getSubjectDN().getName();
                        for (String s : name.split(",")) {
                            if (s.startsWith("CN=")) {
                                certificate.setCommonName(s.split("=")[1]);
                            }
                            if (s.startsWith("L=")) {
                                certificate.setLocation(s.split("=")[1]);
                            }
                            if (s.startsWith("O=")) {
                                certificate.setOrganization(s.split("=")[1]);
                            }
                        }
                        certificate.setType(server ? "server" : "chain");
                        certificate.setSerialNumber(cer.getSerialNumber().toString());
                        certificate.setSignatureAlgorithm(cer.getSigAlgName());
                        certificate.setValidFrom(cer.getNotBefore());
                        certificate.setValidTo(cer.getNotAfter());

                        server = false;
                        certificate.setLocation("");

                        result.add(certificate);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

        } finally {
            try {
                if (sslsocket != null) {
                    sslsocket.close();
                }
            } catch (Exception ignore) {}
        }
        return result;
    }
}
