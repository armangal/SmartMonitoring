package com.smexec.monitor.client.login;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.smexec.monitor.client.MonitoringServiceAsync;
import com.smexec.monitor.shared.AbstractRefreshResult;
import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.config.ClientConfigurations;

public class LoginWidget<CS extends ConnectedServer, R extends AbstractRefreshResult<CS>, FR extends FullRefreshResult<R, CS>>
    extends Composite {

    public interface LoggedInCallBack {

        void loggedIn(ClientConfigurations cc);
    }

    private final MonitoringServiceAsync<CS, R, FR> service;
    private ClientConfigurations cc;

    private LoggedInCallBack callBack;

    private FlowPanel fp = new FlowPanel();
    private TextBox userName = new TextBox();
    private PasswordTextBox password = new PasswordTextBox();
    private Button login = new Button("Login");

    private KeyPressHandler enterhandler = new KeyPressHandler() {

        @Override
        public void onKeyPress(KeyPressEvent event) {
            Log.debug("Key:" + event.getNativeEvent().getKeyCode());
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                login();
            }
        }
    };

    public LoginWidget(MonitoringServiceAsync<CS, R, FR> service) {
        this.service = service;

        if (!GWT.isScript()) {
            userName.setText("admin");
            password.setText("password");
        }
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

        password.addKeyPressHandler(enterhandler);
        password.getElement().setId("input");

        userName.addKeyPressHandler(enterhandler);
        userName.getElement().setId("input");

        login.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                login();
            }

        });

        final HTML version = new HTML();
        version.getElement().setId("version");
        fp.add(version);
        service.getClientConfigurations(new AsyncCallback<ClientConfigurations>() {

            @Override
            public void onSuccess(ClientConfigurations result) {
                version.setText("Env:" + result.getTitle() + ", Version:" + result.getVersion());
                Window.setTitle(result.getTitle() + ", v:" + result.getVersion());
                cc = result;

            }

            @Override
            public void onFailure(Throwable caught) {
                version.setText("Error getting version:" + caught.getMessage());
            }
        });

    }

    @Override
    protected void onAttach() {
        super.onAttach();
        userName.setFocus(true);
    }

    private void login() {
        service.authenticate(userName.getText(), password.getText(), new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (result.booleanValue() == true) {
                    callBack.loggedIn(cc);
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

    public void registerCallBack(LoggedInCallBack callBack) {
        this.callBack = callBack;
    }

}
