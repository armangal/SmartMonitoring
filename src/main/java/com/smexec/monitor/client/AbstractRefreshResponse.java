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
package com.smexec.monitor.client;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractRefreshResponse
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serverTime;

    public AbstractRefreshResponse() {
        this(new Date().toString());
    }

    public AbstractRefreshResponse(String serverTime) {
        super();
        this.serverTime = serverTime;
    }

    public String getServerTime() {
        return serverTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(", serverTime=").append(serverTime);
        return builder.toString();
    }

}
