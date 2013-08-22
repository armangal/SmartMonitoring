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
package org.clevermore.monitor.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.clevermore.monitor.shared.certificate.Certificate;
import org.clevermore.monitor.shared.servers.ConnectedServer;
import org.clevermore.monitor.shared.smartpool.PoolsFeed;

public interface IConnectedServersState<SS extends ServerStatus, DS extends DatabaseServer> {

    ConnectedServer getConnectedServer(Integer serverCode);

    SS getServerStataus(final Integer serevrCode);

    List<SS> getAllServers();

    SS removeServer(final Integer serevrCode);

    SS addServer(SS serverStataus);

    void mergeStats(ArrayList<ConnectedServer> servers);

    ArrayList<ConnectedServer> getServers();

    HashMap<String, PoolsFeed> getPoolFeedMap();

    ArrayList<DS> getDatabases();

    DS getDatabaseServer(String name);

    void addDatabaseServer(DS ds);

    String getExtraServerDetails(Integer serverCode);

    HashMap<String, List<Certificate>> getCertificates();

    void addCertificate(String domain, List<Certificate> certs);
}
