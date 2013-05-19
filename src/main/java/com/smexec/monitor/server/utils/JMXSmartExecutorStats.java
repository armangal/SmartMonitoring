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
import java.util.Map;
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
import javax.management.remote.JMXConnector;

import com.smexec.monitor.server.model.ServerStatus;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.smartpool.PoolsFeed;

public class JMXSmartExecutorStats {

    public void getSmartThreadPoolStats(ServerStatus ss)
        throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, MalformedObjectNameException {

        final Map<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>(0);

        JMXConnector jmxConnector = ss.getConnector();
        MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
        ObjectName smartExecutor = new ObjectName("org.smexec:type=SmartExecutor.Pools,name=*");

        Set<ObjectInstance> smartExecutorSet = mbsc.queryMBeans(smartExecutor, null);

        for (ObjectInstance on : smartExecutorSet) {
            ObjectName objectName = on.getObjectName();
            PoolsFeed pf = new PoolsFeed();
            pf.setPoolName(objectName.getKeyProperty("name"));

            CompositeData[] times = (CompositeData[]) mbsc.getAttribute(objectName, "ExecutionTimeStats");

            ChartFeed<Long, Long> timesChartFeed = new ChartFeed<Long, Long>(new Long[3][times.length], new Long[times.length]);
            for (int i = 0; i < times.length; i++) {
                CompositeData timeStat = times[i];
                timesChartFeed.getValues()[0][i] = JMXUtils.getLongAtributeFromComposite(timeStat, "max");
                timesChartFeed.getValues()[1][i] = JMXUtils.getLongAtributeFromComposite(timeStat, "avg");
                timesChartFeed.getValues()[2][i] = JMXUtils.getLongAtributeFromComposite(timeStat, "min");
                timesChartFeed.getXLineValues()[i] = JMXUtils.getLongAtributeFromComposite(timeStat, "chunkTime");
            }
            pf.setTimeChartFeeds(timesChartFeed);
            // /////////////////////////////////////////

            CompositeData[] tasks = (CompositeData[]) mbsc.getAttribute(objectName, "TaskExecutionStats");
            ChartFeed<Long, Long> tasksChartFeed = new ChartFeed<Long, Long>(new Long[5][tasks.length], new Long[tasks.length]);
            for (int i = 0; i < tasks.length; i++) {
                CompositeData taskStat = tasks[i];
                tasksChartFeed.getValues()[0][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "submitted");
                tasksChartFeed.getValues()[1][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "executed");
                tasksChartFeed.getValues()[2][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "completed");
                tasksChartFeed.getValues()[3][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "failed");
                tasksChartFeed.getValues()[4][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "rejected");
                tasksChartFeed.getXLineValues()[i] = JMXUtils.getLongAtributeFromComposite(taskStat, "chunkTime");
            }
            pf.setTasksChartFeeds(tasksChartFeed);

            // //////////////////////////////////////////////////////////
            pf.setActiveThreads(JMXUtils.getLong(mbsc, objectName, "ActiveCount"));
            pf.setAvgGenTime(JMXUtils.getLong(mbsc, objectName, "AvgTime"));
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

            poolFeedMap.put(pf.getPoolName(), pf);
            // System.out.println(pf);
        }

        ss.setPoolFeedMap(poolFeedMap);
    }
}
