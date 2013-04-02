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
package com.smexec.monitor.client.servers;

import com.googlecode.gwt.charts.client.ColumnType;
import com.smexec.monitor.client.widgets.ILineType;

public enum ServersLineType implements ILineType {
    CPU(0, "CPU", "green", ColumnType.NUMBER), //
    SYS_LOAD(0, "SysLoad", "green", ColumnType.NUMBER), //
    MEMORY(0, "Memory", "blue", ColumnType.NUMBER);

    int index;
    String name;
    String lineColor;
    ColumnType columnType;

    private ServersLineType(int index, String name, String lineColor, ColumnType columnType) {
        this.index = index;
        this.name = name;
        this.lineColor = lineColor;
        this.columnType = columnType;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLineColor() {
        return lineColor;
    }

    @Override
    public ColumnType getColumnType() {
        return columnType;
    }
}
