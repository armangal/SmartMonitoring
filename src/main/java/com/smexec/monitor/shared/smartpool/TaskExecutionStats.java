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
package com.smexec.monitor.shared.smartpool;

import java.io.Serializable;

public class TaskExecutionStats
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private long submitted;
    private long executed;
    private long completed;
    private long rejected;
    private long failed;

    private long min;
    private long max;
    private long totalTime;

    public TaskExecutionStats() {}

    public TaskExecutionStats(long submitted, long executed, long completed, long rejected, long failed, long min, long max, long totalTime) {
        this.submitted = submitted;
        this.executed = executed;
        this.completed = completed;
        this.rejected = rejected;
        this.failed = failed;
        this.min = min;
        this.max = max;
        this.totalTime = totalTime;
    }

    public long getSubmitted() {
        return submitted;
    }

    public long getExecuted() {
        return executed;
    }

    public long getCompleted() {
        return completed;
    }

    public long getRejected() {
        return rejected;
    }

    public long getFailed() {
        return failed;
    }

    public long getMin() {
        return min == Long.MAX_VALUE ? 0 : min;
    }

    public long getMax() {
        return max == Long.MIN_VALUE ? 0 : max;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getAvgTime() {
        long totaltasks = completed + failed;
        return totaltasks > 0 ? (totalTime / totaltasks) : (0L);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskExecutionStats [submitted=")
               .append(submitted)
               .append(", executed=")
               .append(executed)
               .append(", completed=")
               .append(completed)
               .append(", rejected=")
               .append(rejected)
               .append(", failed=")
               .append(failed)
               .append(", min=")
               .append(min)
               .append(", max=")
               .append(max)
               .append(", totalTime=")
               .append(totalTime)
               .append("]");
        return builder.toString();
    }

}
