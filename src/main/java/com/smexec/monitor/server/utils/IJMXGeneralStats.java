package com.smexec.monitor.server.utils;

import java.io.IOException;
import java.util.Date;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.shared.runtime.RuntimeInfo;

public interface IJMXGeneralStats<SS extends ServerStatus> {

    /**
     * gets the memory and cpu stats
     * 
     * @param serverStataus
     * @throws MBeanException
     * @throws AttributeNotFoundException
     * @throws InstanceNotFoundException
     * @throws ReflectionException
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    void getMemoryStats(SS serverStataus, Date executionDate)
        throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException, MalformedObjectNameException;

    RuntimeInfo getRuntimeInfo(SS serverStataus)
        throws IOException;

}
