package com.hyd.redisfx.controllers.conn;

import com.hyd.redisfx.Fx;
import com.hyd.redisfx.conn.Connection;
import com.hyd.redisfx.i18n.I18n;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Protocol;

/**
 *
 * @author yiding_he
 */
public class ConnectionManagerController extends BaseController {

    public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;

    public TextField txtName;

    public TextField txtHost;

    public Spinner<Integer> spnPort;

    public PasswordField txtPassword;

    private SimpleObjectProperty<Connection> currentSelectedConnection;

    public void initialize() {
        this.spnPort.setValueFactory(new IntegerSpinnerValueFactory(1, 65535, DEFAULT_PORT));
        this.txtHost.textProperty().addListener((_ob, _old, _new) -> {
            if (!StringUtils.isBlank(txtHost.getText())) {

            }
        });
    }

    public void closeClicked(ActionEvent actionEvent) {
        this.getStage().close();
    }

    public void createClicked(ActionEvent actionEvent) {

        String title = I18n.getString("op_create");
        String message = I18n.getString("msg_create_confirm");
        if (!Fx.confirmYesNo(title, message)) {
            return;
        }

        this.txtName.setText("");
        this.txtHost.setText("");
        this.spnPort.getValueFactory().setValue(DEFAULT_PORT);
        this.txtPassword.setText("");
    }

    public void saveClicked(ActionEvent actionEvent) {

        if (StringUtils.isBlank(txtName.getText())) {
            String name = txtHost
        }

        Connection connection = new Connection();
        connection.setName();
    }

    public void copyClicked(ActionEvent actionEvent) {

    }

    public void deleteClicked(ActionEvent actionEvent) {

    }

    public void testClicked(ActionEvent actionEvent) {

    }
}
