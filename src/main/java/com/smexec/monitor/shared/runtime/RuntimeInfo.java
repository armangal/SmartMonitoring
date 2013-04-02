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
import java.util.HashMap;
import java.util.List;

public class RuntimeInfo
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bootClassPath;
    private String classPath;
    private List<String> inputArguments;
    private String libraryPath;
    private String name;
    private HashMap<String, String> systemProperties;
    private int availableProcessors;
    private double systemLoadAverage;

    public RuntimeInfo() {}

    public RuntimeInfo(String bootClassPath,
                       String classPath,
                       List<String> inputArguments,
                       String libraryPath,
                       String name,
                       HashMap<String, String> systemProperties,
                       int availableProcessors,
                       double systemLoadAverage) {
        super();
        this.bootClassPath = bootClassPath;
        this.classPath = classPath;
        this.inputArguments = inputArguments;
        this.libraryPath = libraryPath;
        this.name = name;
        this.systemProperties = systemProperties;
        this.availableProcessors = availableProcessors;
        this.systemLoadAverage = systemLoadAverage;
    }

    public String getBootClassPath() {
        return bootClassPath;
    }

    public String getClassPath() {
        return classPath;
    }

    public List<String> getInputArguments() {
        return inputArguments;
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getSystemProperties() {
        return systemProperties;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RuntimeInfo [bootClassPath=");
        builder.append(bootClassPath);
        builder.append(", classPath=");
        builder.append(classPath);
        builder.append(", inputArguments=");
        builder.append(inputArguments);
        builder.append(", libraryPath=");
        builder.append(libraryPath);
        builder.append(", name=");
        builder.append(name);
        builder.append(", systemProperties=");
        builder.append(systemProperties);
        builder.append(", availableProcessors=");
        builder.append(availableProcessors);
        builder.append(", systemLoadAverage=");
        builder.append(systemLoadAverage);
        builder.append("]");
        return builder.toString();
    }

}
