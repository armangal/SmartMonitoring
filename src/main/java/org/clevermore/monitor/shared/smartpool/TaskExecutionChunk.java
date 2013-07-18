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
package org.clevermore.monitor.shared.smartpool;

import java.util.HashMap;

import org.clevermore.monitor.shared.AbstractChunkStats;


public class TaskExecutionChunk
    extends AbstractChunkStats {

    private static final long serialVersionUID = 1L;

    /**
     * global stats about current chunk
     */
    private TaskExecutionStats globalStats;

    /**
     * detailed stats per task name about current chunk
     */
    private HashMap<String, TaskExecutionStats> taskStatsMap;

    public TaskExecutionChunk() {}

    public TaskExecutionChunk(long fullEndTime, long endTime, TaskExecutionStats globalStats, HashMap<String, TaskExecutionStats> taskStatsMap) {
        super(fullEndTime, endTime);
        this.globalStats = globalStats;
        this.taskStatsMap = taskStatsMap;
    }

    public TaskExecutionStats getGlobalStats() {
        return globalStats;
    }

    public HashMap<String, TaskExecutionStats> getTaskStatsMap() {
        return taskStatsMap;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskExecutionChunk [globalStats=")
               .append(globalStats)
               .append(", taskStatsMap=")
               .append(taskStatsMap)
               .append(", toString()=")
               .append(super.toString())
               .append("]");
        return builder.toString();
    }

}
