package com.smexec.monitor.client.settings;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.shared.AbstractFullRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;

public class SettingsPopup<CS extends ConnectedServer, FR extends AbstractFullRefreshResult<CS>>
    extends DialogBox {

    private FlowPanel fp = new FlowPanel();
    private TextArea ta = new TextArea();
    private final MonitoringServiceAsync<CS, FR> service;

    public SettingsPopup(MonitoringServiceAsync<CS, FR> service) {
        this.service = service;
        setWidget(fp);
        fp.add(ta);
        setAutoHideEnabled(true);
        setAnimationEnabled(true);
        setModal(true);
        setSize("800px", "800px");
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
                ta.setSize("800px", "800px");

            }
        });
    }
}
