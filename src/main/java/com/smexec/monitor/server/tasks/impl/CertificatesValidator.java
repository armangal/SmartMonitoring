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
package com.smexec.monitor.server.tasks.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.smexec.monitor.server.model.IConnectedServersState;
import com.smexec.monitor.server.services.alert.IAlertService;
import com.smexec.monitor.server.services.config.IConfigurationService;
import com.smexec.monitor.server.services.mail.IMailService;


public class CertificatesValidator {
    public static Logger logger = LoggerFactory.getLogger("PeriodicalUpdater");

//    @Inject
//    private IConnectedServersState<SS, CS, DS> connectedServersState;
//
//    @Inject
//    private IConfigurationService<SC> configurationService;
//
//    @Inject
//    private IAlertService<SS> alertService;
//    
//    
//    public CertificatesValidator() {
//        // TODO Auto-generated constructor stub
//    }
//    
}
