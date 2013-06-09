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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

interface Resources extends ClientBundle {
    @Source("img/settings-blue.png")
    ImageResource settings();

    @Source("img/settings-blue-small.png")
    ImageResource settingsSmall();

    @Source("img/logout.png")
    ImageResource logout();

    @Source("img/StopRefresh.png")
    ImageResource stopRefresh();

    @Source("img/ContinueRefresh.png")
    ImageResource continueRefresh();

    @Source("img/ContinueAlerts.png")
    ImageResource continueAlerts();

    @Source("img/StopAlerts.png")
    ImageResource stopAlerts();
}