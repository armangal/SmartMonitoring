package com.smexec.monitor.server.tasks;

import java.util.concurrent.locks.ReentrantLock;

public class ConnectionSynch {

    public static ReentrantLock connectionLock = new ReentrantLock();
}
