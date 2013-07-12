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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SmartExecutorDataHolder {

    /**
     * mains stats map, holds stats from many SmartExecutor instances. Each instance might have many pools
     * that are mapped by name in the internal map
     */
    private HashMap<String, HashMap<String, PoolsFeed>> executorsMap = new HashMap<String, HashMap<String, PoolsFeed>>();

    /**
     * should be called only when new chunks are available
     * 
     * @param pf
     */
    public void addStats(PoolsFeed pf, List<TaskExecutionChunk> chunks) {
        if (!executorsMap.containsKey(pf.getSmartExecutorName())) {
            executorsMap.put(pf.getSmartExecutorName(), new HashMap<String, PoolsFeed>());
        }

        HashMap<String, PoolsFeed> poolsMap = executorsMap.get(pf.getSmartExecutorName());
        if (!poolsMap.containsKey(pf.getPoolName())) {
            pf.addChunk(chunks);
            poolsMap.put(pf.getPoolName(), pf);
        } else {
            // here we have to update the global values and the chunks
            PoolsFeed poolsFeed = poolsMap.get(pf.getPoolName());
            poolsFeed.updateMe(pf);
            if (chunks != null && chunks.size() > 0) {
                poolsFeed.addChunk(chunks);
            }
        }
    }

    public long getLastStatsTime(final String poolName, final String smartExecutorName) {
        if (!executorsMap.containsKey(smartExecutorName)) {
            return 0;
        }
        HashMap<String, PoolsFeed> poolsMap = executorsMap.get(smartExecutorName);
        if (!poolsMap.containsKey(poolName)) {
            return 0;
        }
        return poolsMap.get(poolName).getLastChunkTime();

    }

    public Set<String> getAvalibleExecutorNames() {
        return executorsMap.keySet();
    }
    
    public HashMap<String, PoolsFeed> getPoolStats(final String seName) {
        return executorsMap.get(seName);
    }
}
