package com.smexec.monitor.client.servers;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.smexec.monitor.shared.runtime.ThreadDump;
import com.smexec.monitor.shared.runtime.ThreadInfo;

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