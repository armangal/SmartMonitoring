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
package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerWidgetRefresh
    implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * list of connected servers with internal stats
     */
    private ArrayList<ConnectedServer> servers = new ArrayList<ConnectedServer>(0);

    private ArrayList<ConnectedDB> databases = new ArrayList<ConnectedDB>(0);

    public ServerWidgetRefresh() {

    }

    public ServerWidgetRefresh(ArrayList<ConnectedServer> servers, ArrayList<ConnectedDB> databases) {
        super();
        this.servers = servers;
        this.databases = databases;
    }

    public ArrayList<ConnectedDB> getDatabases() {
        return databases;
    }

    public ArrayList<ConnectedServer> getServers() {
        return servers;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerWidgetRefresh [servers=").append(servers).append(", databases=").append(databases).append("]");
        return builder.toString();
    }

}
