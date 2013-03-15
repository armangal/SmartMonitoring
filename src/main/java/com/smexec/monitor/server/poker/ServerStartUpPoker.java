package com.smexec.monitor.server.poker;

import com.smexec.monitor.server.ServerStartUp;
import com.smexec.monitor.server.guice.GuiceUtils;
import com.smexec.monitor.server.guice.MonitoringModulePoker;

public class ServerStartUpPoker
    extends ServerStartUp {

    @Override
    public void initGuice() {
        GuiceUtils.init(new MonitoringModulePoker());

    }
}
