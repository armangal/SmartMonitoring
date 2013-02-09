package com.smexec.monitor.client.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.smexec.monitor.client.MonitoringService;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.shared.ConnectedServer;

public class MemoryWidget
    extends AbstractMonitoringWidget {

    private final MonitoringServiceAsync service = GWT.create(MonitoringService.class);

    private NumberFormat formatLong = NumberFormat.getDecimalFormat();

    public MemoryWidget() {

        super("Memory");
        setStyleName("memoryWidget");
    }

    public void update(ArrayList<ConnectedServer> list) {
        getScrollPanel().clear();
        FlexTable ft = new FlexTable();
        ft.getElement().setId("infoTable");
        getScrollPanel().add(ft);
        ft.setCellPadding(0);
        ft.setCellSpacing(0);

        Collections.sort(list, new Comparator<ConnectedServer>() {

            @Override
            public int compare(ConnectedServer o1, ConnectedServer o2) {
                if (o1.getStatus() && o2.getStatus()) {
                    return (int) (o2.getMemoryUsage().getPercentage() - o1.getMemoryUsage().getPercentage());
                }
                return 0;
            }
        });

        int i = 0;
        ft.setText(i, 0, "Code, Name");
        ft.setText(i, 1, "Used Memory");
        ft.setText(i, 2, "Max Memory");
        ft.setText(i, 3, "Usage %");
        ft.getRowFormatter().getElement(i++).setId("th");

        for (ConnectedServer cs : list) {
            if (cs.getStatus()) {
                final HTML name = new HTML("<a href=#>" + cs.getServerCode() + ", " + cs.getName() + "</a>");
                name.getElement().setAttribute("code", "" + cs.getServerCode());
                name.setTitle("Click to get Thread Dump");
                name.addMouseOverHandler(new MouseOverHandler() {

                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        name.getElement().getStyle().setCursor(Cursor.POINTER);
                    }
                });

                name.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        String code = ((Widget) event.getSource()).getElement().getAttribute("code");
                        service.getThreadDump(Integer.valueOf(code), new AsyncCallback<String>() {

                            @Override
                            public void onSuccess(String result) {
                                DialogBox db = new DialogBox();
                                db.setAnimationEnabled(true);
                                db.setAutoHideEnabled(true);
                                db.setModal(true);
                                db.setSize("900px", "600px");
                                TextArea textArea = new TextArea();
                                textArea.setText(result);
                                textArea.setSize("990px", "590px");
                                db.setWidget(textArea);
                                db.center();
                            }

                            @Override
                            public void onFailure(Throwable caught) {
                                Window.alert(caught.getMessage());
                            }
                        });
                    }
                });

                ft.setWidget(i, 0, name);
                ft.setText(i, 1, formatLong.format(cs.getMemoryUsage().getUsed() / 1048576) + " MB");
                ft.setText(i, 2, formatLong.format(cs.getMemoryUsage().getMax() / 1048576) + " MB");
                ft.setText(i++, 3, formatLong.format(cs.getMemoryUsage().getPercentage()) + "%");

                if (cs.getMemoryUsage().getPercentage() > 90) {
                    ft.getRowFormatter().getElement(i - 1).setId("memoryVeryHigh");
                } else if (cs.getMemoryUsage().getPercentage() > 80) {
                    ft.getRowFormatter().getElement(i - 1).setId("memoryHigh");
                } else if (cs.getMemoryUsage().getPercentage() > 70) {
                    ft.getRowFormatter().getElement(i - 1).setId("memoryWarn");
                }
            }
        }

    }
}
