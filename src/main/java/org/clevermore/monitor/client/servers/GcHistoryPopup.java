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
package org.clevermore.monitor.client.servers;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;

public class GcHistoryPopup
    extends DialogBox {

    private FlowPanel flowPanel = new FlowPanel();
    private TextArea textArea = new TextArea();

    public GcHistoryPopup() {
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        setModal(true);
        setSize("600px", "600px");
        textArea.setSize("590px", "590px");
        flowPanel.add(new HTML("<a style=\"font-size: 24px;line-height: 24px;font-weight: bold;\" href='http://javaeesupportpatterns.blogspot.co.il/2011/10/verbosegc-output-tutorial-java-7.html' target='_blank'>verbosegc-output-tutorial-java</a>"));
        flowPanel.add(textArea);
        setWidget(flowPanel);
        flowPanel.add(new Image("http://1.bp.blogspot.com/-Vtb9LjOsgSo/TqYTijPwJQI/AAAAAAAAAT8/qrmRGIIPgGo/s1600/verboseGC_YoungGen_detail.png"));
        flowPanel.add(new Image("http://2.bp.blogspot.com/-dDa_norhVcQ/TqYUmSirZaI/AAAAAAAAAUE/AtKyC9ftA0A/s1600/verboseGC_OldGen_detail.png"));
        flowPanel.add(new Image("http://3.bp.blogspot.com/-C-09CuwRAec/TqYUxQX53oI/AAAAAAAAAUM/Cl7wJn2k66k/s1600/verboseGC_PermGen_detail.png"));
        flowPanel.add(new Image("http://4.bp.blogspot.com/-U4NiGRW-Sq0/TqYU8jkx0uI/AAAAAAAAAUU/rNoNBQJbhpQ/s1600/verboseGC_Java7_Heap_breakdown_detail.png"));
    }

    public void setText(String text) {
        textArea.setText(text);
        int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
        setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), 26);
    }
}
