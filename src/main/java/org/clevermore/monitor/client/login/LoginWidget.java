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
package org.clevermore.monitor.client.login;

import java.util.Date;

import org.clevermore.monitor.client.ConfigurationServiceAsync;
import org.clevermore.monitor.client.GeneralService;
import org.clevermore.monitor.client.GeneralServiceAsync;
import org.clevermore.monitor.shared.config.ClientConfigurations;
import org.clevermore.monitor.shared.utils.StringFormatter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class LoginWidget<CC extends ClientConfigurations>
    extends Composite {

    private static final String SSIP = "ssip";
    private static final String SSIU = "ssiu";
    private static final String SSICH = "ssich";

    public interface LoggedInCallBack<CC> {

        void loggedIn(final CC cc, final String loggedInUser);
    }

    private final GeneralServiceAsync service = GWT.create(GeneralService.class);
    private CC cc;

    private LoggedInCallBack<CC> callBack;

    private FlowPanel fp = new FlowPanel();
    private TextBox userName = new TextBox();
    private PasswordTextBox password = new PasswordTextBox();
    private Button login = new Button("Login");
    private CheckBox staySignedIn = new CheckBox("Stay signed in");
    private Label errorMsg = new Label();

    private KeyPressHandler enterhandler = new KeyPressHandler() {

        @Override
        public void onKeyPress(KeyPressEvent event) {
            Log.debug("Key:" + event.getNativeEvent().getKeyCode());
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                login();
            }
        }
    };

    public LoginWidget(ConfigurationServiceAsync<CC> service) {

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
        fp.add(staySignedIn);
        fp.add(errorMsg);
        errorMsg.getElement().getStyle().setColor("red");
        errorMsg.getElement().getStyle().setFontStyle(FontStyle.ITALIC);

        password.addKeyPressHandler(enterhandler);
        password.getElement().setId("input");

        userName.addKeyPressHandler(enterhandler);
        userName.getElement().setId("input");

        login.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                login.setEnabled(false);
                login();
            }

        });

        final HTML version = new HTML();
        version.getElement().setId("version");
        fp.add(version);
        service.getClientConfigurations(new AsyncCallback<CC>() {

            @Override
            public void onSuccess(CC result) {
                version.setText("Env:" + result.getTitle() + ", Version:" + result.getVersion());
                Window.setTitle(result.getTitle() + ", v:" + result.getVersion());
                cc = result;

                staySignedIn.setValue(Cookies.getCookie(SSICH) != null && Cookies.getCookie(SSICH).equals("on"));
                String user = StringFormatter.Base64Decode(Cookies.getCookie(SSIU));
                String pass = StringFormatter.Base64Decode(Cookies.getCookie(SSIP));
                if (staySignedIn.getValue() && user != null && pass != null) {
                    userName.setText(user);
                    password.setText(pass);
                    login();
                }
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
        errorMsg.setText("");
    }

    private void login() {
        service.authenticate(userName.getText(), password.getText(), new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                try {
                    if (result.booleanValue() == true) {
                        if (staySignedIn.getValue()) {
                            Log.debug("Store cookie");
                            Cookies.setCookie(SSICH, "on", new Date(Long.MAX_VALUE));
                            Cookies.setCookie(SSIU, StringFormatter.Base64Encode(userName.getText().trim()), new Date(Long.MAX_VALUE));
                            Cookies.setCookie(SSIP, StringFormatter.Base64Encode(password.getText().trim()), new Date(Long.MAX_VALUE));
                        } else {
                            Log.debug("Cleaning cookie");
                            Cookies.removeCookie(SSIP);
                            Cookies.removeCookie(SSIU);
                            Cookies.removeCookie(SSICH);
                        }
                        callBack.loggedIn(cc, userName.getText().trim());
                    } else {
                        errorMsg.setText("Can't login, try again.");
                    }
                } finally {
                    login.setEnabled(true);
                }

            }

            @Override
            public void onFailure(Throwable caught) {
                errorMsg.setText("Error loging-in:" + caught.getMessage());
                login.setEnabled(true);
            }
        });
    }

    public void registerCallBack(LoggedInCallBack<CC> callBack) {
        this.callBack = callBack;
    }

}
