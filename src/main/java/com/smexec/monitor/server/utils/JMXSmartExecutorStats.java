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
package com.smexec.monitor.server.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import javax.management.remote.JMXConnector;

import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.shared.smartpool.PoolsFeed;
import com.smexec.monitor.shared.smartpool.SmartExecutorDataHolder;
import com.smexec.monitor.shared.smartpool.TaskExecutionChunk;
import com.smexec.monitor.shared.smartpool.TaskExecutionStats;

public class JMXSmartExecutorStats {

    public void getSmartThreadPoolStats(ServerStatus ss)
        throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, MalformedObjectNameException {

        JMXConnector jmxConnector = ss.getConnector();
        MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();

        ObjectName smartExecutors = new ObjectName("org.smexec:type=SmartExecutor,name=*");
        if (mbsc.queryMBeans(smartExecutors, null).size() == 0) {
            return;
        }
        SmartExecutorDataHolder smartExecutorDataHolder = ss.getSmartExecutorDataHolder();

        ObjectName smartExecutor = new ObjectName("org.smexec:type=SmartExecutor.Pools,name=*");

        Set<ObjectInstance> smartExecutorSet = mbsc.queryMBeans(smartExecutor, null);

        for (ObjectInstance on : smartExecutorSet) {
            ObjectName objectName = on.getObjectName();
            String poolName = JMXUtils.getString(mbsc, objectName, "Name");
            String sEName = JMXUtils.getString(mbsc, objectName, "SmartExecutorName");
            PoolsFeed pf = new PoolsFeed(poolName, sEName);

            // //////////////////////////////////////////////////////////
            pf.setActiveThreads(JMXUtils.getLong(mbsc, objectName, "ActiveCount"));
            pf.setCompleted(JMXUtils.getLong(mbsc, objectName, "Completed"));
            pf.setExecuted(JMXUtils.getLong(mbsc, objectName, "Executed"));
            pf.setFailed(JMXUtils.getLong(mbsc, objectName, "Failed"));
            pf.setLargestPoolSize(JMXUtils.getLong(mbsc, objectName, "LargestPoolSize"));
            pf.setMaxGenTime(JMXUtils.getLong(mbsc, objectName, "MaxTime"));
            pf.setMinGenTime(JMXUtils.getLong(mbsc, objectName, "MinTime"));
            pf.setPoolSize(JMXUtils.getLong(mbsc, objectName, "PoolSize"));
            pf.setRejected(JMXUtils.getLong(mbsc, objectName, "Rejected"));
            pf.setSubmitted(JMXUtils.getLong(mbsc, objectName, "Submitted"));
            pf.setTotoalGenTime(JMXUtils.getLong(mbsc, objectName, "TotalTime"));
            pf.addTaskNames((String[]) mbsc.getAttribute(objectName, "TaskNames"));

            long lastStatsTime = smartExecutorDataHolder.getLastStatsTime(poolName, sEName);
            CompositeData[] tasks;
            if (lastStatsTime == 0) {
                tasks = (CompositeData[]) mbsc.getAttribute(objectName, "TaskExecutionStats");
            } else {
                tasks = (CompositeData[]) mbsc.invoke(objectName, "getLastTaskExecutionStats", new Object[] {lastStatsTime}, new String[] {"long"});
            }

            LinkedList<TaskExecutionChunk> chunks = new LinkedList<TaskExecutionChunk>();

            if (tasks.length > 0) {

                for (int i = tasks.length - 1; i >= 0; i--) {
                    CompositeData task = tasks[i];
                    long time = JMXUtils.getLongTimeFromComposite(task, "endTime");
                    long fullTime = JMXUtils.getLongAtributeFromComposite(task, "endTime");
                    CompositeData gloabalStats = (CompositeData) task.get("globalStats");
                    HashMap<String, TaskExecutionStats> tasksMap = new HashMap<String, TaskExecutionStats>(0);
                    TabularDataSupport taskStatsMap = (TabularDataSupport) task.get("taskStatsMap");
                    if (taskStatsMap != null) {
                        for (Entry<Object, Object> v : taskStatsMap.entrySet()) {
                            CompositeDataSupport value = (CompositeDataSupport) v.getValue();
                            Iterator<?> it = value.values().iterator();
                            String next = it.next().toString();
                            CompositeDataSupport next2 = (CompositeDataSupport) it.next();
                            tasksMap.put(next, getTaskExecutionStats(next2));
                        }

                    }

                    TaskExecutionStats taskExecutionStats = getTaskExecutionStats(gloabalStats);
                    TaskExecutionChunk tes = new TaskExecutionChunk(fullTime, time, taskExecutionStats, tasksMap);

                    chunks.add(tes);
                }
            }

            smartExecutorDataHolder.addStats(pf, chunks);
        }

    }

    private TaskExecutionStats getTaskExecutionStats(CompositeData gloabalStats) {
        long submitted = JMXUtils.getLongAtributeFromComposite(gloabalStats, "submitted");
        long executed = JMXUtils.getLongAtributeFromComposite(gloabalStats, "executed");
        long completed = JMXUtils.getLongAtributeFromComposite(gloabalStats, "completed");
        long rejected = JMXUtils.getLongAtributeFromComposite(gloabalStats, "rejected");
        long failed = JMXUtils.getLongAtributeFromComposite(gloabalStats, "failed");
        long min = JMXUtils.getLongAtributeFromComposite(gloabalStats, "min");
        long max = JMXUtils.getLongAtributeFromComposite(gloabalStats, "max");
        long totalTime = JMXUtils.getLongAtributeFromComposite(gloabalStats, "totalTime");
        TaskExecutionStats taskExecutionStats = new TaskExecutionStats(submitted, executed, completed, rejected, failed, min, max, totalTime);
        return taskExecutionStats;
    }
}
