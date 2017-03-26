package com.hyd.redisfx.controllers.conn;

import com.hyd.redisfx.Fx;
import com.hyd.redisfx.conn.Connection;
import com.hyd.redisfx.conn.ConnectionManager;
import com.hyd.redisfx.i18n.I18n;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
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

    public PasswordField txtPassphase;

    public Button btnCreate;

    public Button btnTest;

    public Button btnSave;

    public Button btnCopy;

    public Button btnDelete;

    public ListView<Connection> lstConnections;

    private SimpleObjectProperty<Connection> currentSelectedConnection = new SimpleObjectProperty<>();

    public void initialize() {
        this.spnPort.setValueFactory(new IntegerSpinnerValueFactory(1, 65535, DEFAULT_PORT));
        this.txtHost.textProperty().addListener((_ob, _old, _new) -> {
            btnSave.setDisable(StringUtils.isBlank(txtHost.getText()));
        });

        this.lstConnections.setItems(ConnectionManager.connectionsProperty());
        this.lstConnections.getSelectionModel().selectedItemProperty().addListener((_ob, _old, _new) -> {
            if (_new != null) {
                this.currentSelectedConnection.set(_new);
            }
        });

        this.currentSelectedConnection.addListener((_ob, _old, _new) -> {
            this.txtName.setText(_new.getName());
            this.txtHost.setText(_new.getHost());
            this.spnPort.getValueFactory().setValue(_new.getPort());
            this.txtPassphase.setText(_new.getPassphase());
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
        this.txtPassphase.setText("");
    }

    public void saveClicked(ActionEvent actionEvent) {

        if (StringUtils.isBlank(txtName.getText())) {
            String name = txtHost.getText() + ":" + spnPort.getValue();
            txtName.setText(name);
        }

        Connection connection = new Connection();
        connection.setName(txtName.getText());
        connection.setHost(txtHost.getText());
        connection.setPort(spnPort.getValue());
        connection.setPassphase(txtPassphase.getText());

        ConnectionManager.saveConnection(connection);
    }

    public void copyClicked(ActionEvent actionEvent) {

    }

    public void deleteClicked(ActionEvent actionEvent) {

    }

    public void testClicked(ActionEvent actionEvent) {

    }
}
