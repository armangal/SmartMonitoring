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
package com.smexec.monitor.shared.runtime;

import java.io.Serializable;
import java.util.LinkedList;

public class ThreadDump
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dump;
    private LinkedList<ThreadInfo> threads;

    public ThreadDump() {}

    public ThreadDump(String dump, LinkedList<ThreadInfo> threads) {
        super();
        this.dump = dump;
        this.threads = threads;
    }

    public String getDump() {
        return dump;
    }

    public void setDump(String dump) {
        this.dump = dump;
    }

    public LinkedList<ThreadInfo> getThreads() {
        return threads;
    }

    public void setThreads(LinkedList<ThreadInfo> threads) {
        this.threads = threads;
    }

}
