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
