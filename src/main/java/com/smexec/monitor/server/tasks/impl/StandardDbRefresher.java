package com.smexec.monitor.server.tasks.impl;

import com.smexec.monitor.server.model.DatabaseServer;

public class StandardDbRefresher
    extends AbstractDbRefresher<DatabaseServer> {

    public StandardDbRefresher(DatabaseServer ds) {
        super(ds);
    }

}
