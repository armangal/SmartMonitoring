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

import com.smexec.monitor.server.model.ServerStataus;
import com.smexec.monitor.shared.ChartFeed;
import com.smexec.monitor.shared.PoolsFeed;

public class JMXSmartExecutorStats {

    public void getSmartThreadPoolStats(ServerStataus ss)
        throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, MalformedObjectNameException {

        Map<String, PoolsFeed> poolFeedMap = new HashMap<String, PoolsFeed>(0);

        JMXConnector jmxConnector = ss.getConnector();
        MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
        ObjectName smartExecutor = new ObjectName("org.smexec:type=SmartExecutor.Pools,name=*");

        Set<ObjectInstance> smartExecutorSet = mbsc.queryMBeans(smartExecutor, null);

        for (ObjectInstance on : smartExecutorSet) {
            ObjectName objectName = on.getObjectName();
            PoolsFeed pf = new PoolsFeed();
            pf.setPoolName(objectName.getKeyProperty("name"));

            CompositeData[] times = (CompositeData[]) mbsc.getAttribute(objectName, "ExecutionTimeStats");

            ChartFeed timesChartFeed = new ChartFeed(times.length, 4);
            for (int i = 0; i < times.length; i++) {
                CompositeData timeStat = times[i];
                timesChartFeed.getValues()[0][i] = JMXUtils.getLongAtributeFromComposite(timeStat, "max");
                timesChartFeed.getValues()[1][i] = JMXUtils.getLongAtributeFromComposite(timeStat, "avg");
                timesChartFeed.getValues()[2][i] = JMXUtils.getLongAtributeFromComposite(timeStat, "min");
                timesChartFeed.getValues()[3][i] = JMXUtils.getLongAtributeFromComposite(timeStat, "chunkTime");
            }
            pf.setTimeChartFeeds(timesChartFeed);
            // /////////////////////////////////////////

            CompositeData[] tasks = (CompositeData[]) mbsc.getAttribute(objectName, "TaskExecutionStats");
            ChartFeed tasksChartFeed = new ChartFeed(tasks.length, 6);
            for (int i = 0; i < tasks.length; i++) {
                CompositeData taskStat = tasks[i];
                tasksChartFeed.getValues()[0][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "submitted");
                tasksChartFeed.getValues()[1][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "executed");
                tasksChartFeed.getValues()[2][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "completed");
                tasksChartFeed.getValues()[3][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "failed");
                tasksChartFeed.getValues()[4][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "rejected");
                tasksChartFeed.getValues()[5][i] = JMXUtils.getLongAtributeFromComposite(taskStat, "chunkTime");
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
