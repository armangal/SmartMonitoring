package com.smexec.monitor.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

interface Resources extends ClientBundle {
    @Source("img/Settings.png")
    ImageResource settings();

    @Source("img/Settings_small.png")
    ImageResource settingsSmall();
}