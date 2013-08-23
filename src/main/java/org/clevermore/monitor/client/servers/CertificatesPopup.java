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

import java.util.HashMap;
import java.util.List;

import org.clevermore.monitor.shared.certificate.Certificate;
import org.mortbay.log.Log;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A popup with all monitored certificates details
 */
public class CertificatesPopup
    extends DialogBox {

    final FlowPanel fp = new FlowPanel();
    final FlexTable ft = new FlexTable();

    public CertificatesPopup(HashMap<String, List<Certificate>> result) {
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        setModal(true);
        setSize(Window.getClientWidth() - 50 + "px", "200px");

        ft.getElement().setId("infoTable");
        ft.setCellPadding(0);
        ft.setCellSpacing(0);
        ft.setText(0, 0, "Domain");
        ft.setText(0, 1, "Certificate");
        ft.setText(0, 2, "Alert Raised");
        ft.getRowFormatter().getElement(0).setId("th");
        ft.setWidth("100%");
        fp.add(ft);
        setWidget(fp);

        center();

        int i = 1;
        for (String key : result.keySet()) {
            Label l = new Label(key);
            l.getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            ft.setWidget(i, 0, l);
            ft.setText(i, 1, "");
            ft.setText(i, 2, "");
            i++;
            Log.debug("Certificate:" + result.get(key));
            for (Certificate c : result.get(key)) {
                ft.setText(i, 0, c.getCommonName());
                ft.setText(i, 1, c.toString());
                ft.setText(i, 2, "" + c.isAlertRaised());
                i++;
            }
        }
        int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
        setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), 26);

    }
}
