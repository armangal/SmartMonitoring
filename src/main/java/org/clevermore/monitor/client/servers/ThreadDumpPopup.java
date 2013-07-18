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

import org.clevermore.monitor.shared.runtime.ThreadDump;
import org.clevermore.monitor.shared.runtime.ThreadInfo;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;

public class ThreadDumpPopup
    extends DialogBox {

    final TextArea textArea = new TextArea();
    final FlowPanel fp = new FlowPanel();
    final FlexTable ft = new FlexTable();

    public ThreadDumpPopup() {
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        setModal(true);
        setSize("900px", "600px");
        textArea.setText("Wait....");
        textArea.setSize("990px", "590px");

        ft.getElement().setId("infoTable");
        ft.setCellPadding(0);
        ft.setCellSpacing(0);
        ft.setText(0, 0, "Name");
        ft.setText(0, 1, "ID");
        ft.setText(0, 2, "Status");
        ft.getRowFormatter().getElement(0).setId("th");
        ft.setWidth("70%");
        fp.add(ft);
        fp.add(textArea);
        setWidget(fp);

    }

    public void setDump(ThreadDump threadDump) {
        textArea.setText(threadDump.getDump());
        int i = 1;
        for (ThreadInfo ti : threadDump.getThreads()) {
            ft.setText(i, 0, ti.getName());
            ft.setText(i, 1, "" + ti.getId());
            ft.setText(i, 2, ti.getState());
            
            if ("BLOCKED".equalsIgnoreCase(ti.getState())) {
                Style style = ft.getFlexCellFormatter().getElement(i, 2).getStyle();
                style.setBackgroundColor("#C00000");
                style.setFontWeight(FontWeight.BOLDER);
                style.setColor("white");
            }
            i++;
        }
    }
}
