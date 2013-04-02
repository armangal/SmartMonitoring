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

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;

public class GcHistoryPopup
    extends DialogBox {

    private TextArea textArea = new TextArea();

    public GcHistoryPopup() {
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        setModal(true);
        setSize("600px", "600px");
        textArea.setSize("590px", "590px");
        setWidget(textArea);
    }

    public void setText(String text) {

        textArea.setText(text);
    }
}
