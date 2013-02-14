package com.smexec.monitor.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.smexec.monitor.client.MonitoringService;
import com.smexec.monitor.client.MonitoringServiceAsync;

public class LoginWidget
    extends Composite {

    public interface LoggedInCallBack {

        void loggedIn();
    }

    private final MonitoringServiceAsync service = GWT.create(MonitoringService.class);

    private LoggedInCallBack callBack;

    private FlowPanel fp = new FlowPanel();
    private TextBox userName = new TextBox();
    private PasswordTextBox password = new PasswordTextBox();
    private Button login = new Button("Login");

    public LoginWidget() {
//        if (!GWT.isScript()) {
            userName.setText("admin");
            password.setText("password");
//        }
        initWidget(fp);

        fp.setStyleName("login");
        Label un = new Label("User Name:");
        un.getElement().setId("label");
        fp.add(un);
        fp.add(userName);
        Label pw = new Label("Password:");
        pw.getElement().setId("label");
        fp.add(pw);
        fp.add(password);
        fp.add(login);

        login.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                service.authenticate(userName.getText(), password.getText(), new AsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        if (result.booleanValue() == true) {
                            callBack.loggedIn();
                        } else {
                            Window.alert("Can't login");
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

    }

    public void registerCallBack(LoggedInCallBack callBack) {
        this.callBack = callBack;
    }

}
