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
package com.smexec.monitor.client.settings;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.smexec.monitor.client.GeneralServiceAsync;

public class SettingsPopup
    extends DialogBox {

    private FlowPanel fp = new FlowPanel();
    private TextArea ta = new TextArea();

    public SettingsPopup(final GeneralServiceAsync service) {
        setWidget(fp);
        fp.add(ta);
        setAutoHideEnabled(false);
        setAnimationEnabled(true);
        setModal(true);
        setSize("1000px", "500px");
        setGlassEnabled(true);

        service.getSettingsXML(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ta.setText(caught.getMessage());
            }

            @Override
            public void onSuccess(String result) {
                ta.setText(result);
                int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
                setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), 26);
                ta.setSize("1000px", "500px");

            }
        });

        Button save = new Button("Save");
        save.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                service.saveSettingsXML(ta.getText(), new AsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            Window.alert("Configurations saved");
                        } else {
                            Window.alert("Error saving: not clear");
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error saving:" + caught.getMessage());
                    }
                });
            }
        });

        fp.add(save);
        Button close = new Button("Close");
        close.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        fp.add(close);
    }
}
