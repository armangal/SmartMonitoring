package com.smexec.monitor.server.tasks.impl;

import java.util.Date;

import com.smexec.monitor.server.model.ServerStatus;

public class StandardRefresher
    extends AbstractRefresher<ServerStatus> {

    public StandardRefresher(ServerStatus ss, Date executionDate, int excutionNumber) {
        super(ss, executionDate, excutionNumber);
    }

}
