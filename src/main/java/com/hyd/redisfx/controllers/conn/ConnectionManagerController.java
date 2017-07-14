package com.hyd.redisfx.controllers.conn;

import com.hyd.redisfx.App;
import com.hyd.redisfx.Fx;
import com.hyd.redisfx.conn.Connection;
import com.hyd.redisfx.conn.ConnectionManager;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.event.EventType;
import com.hyd.redisfx.i18n.I18n;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Protocol;

/**
 * @author yiding_he
 */
public class ConnectionManagerController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManagerController.class);

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

    public Button btnOpen;

    public ListView<Connection> lstConnections;

    private SimpleObjectProperty<Connection> currentSelectedConnection = new SimpleObjectProperty<>();

    public void initialize() {
        this.spnPort.setValueFactory(new IntegerSpinnerValueFactory(1, 65535, DEFAULT_PORT));
        this.txtHost.textProperty().addListener((_ob, _old, _new) -> {
            btnSave.setDisable(StringUtils.isBlank(txtHost.getText()));
            btnTest.setDisable(StringUtils.isBlank(txtHost.getText()));
            btnCopy.setDisable(StringUtils.isBlank(txtHost.getText()));
            btnOpen.setDisable(StringUtils.isBlank(txtHost.getText()));
        });

        initListView();

        this.currentSelectedConnection.addListener((_ob, _old, _new) -> {
            this.btnDelete.setDisable(_new == null);
            if (_new != null) {
                this.txtName.setText(_new.getName());
                this.txtHost.setText(_new.getHost());
                this.spnPort.getValueFactory().setValue(_new.getPort());
                this.txtPassphase.setText(_new.getPassphase());
            } else {
                resetFields();
            }
        });
    }

    private void initListView() {
        this.lstConnections.setItems(ConnectionManager.connectionsProperty());
        this.lstConnections.getSelectionModel().selectedItemProperty()
                .addListener((_ob, _old, _new) -> this.currentSelectedConnection.set(_new));

        this.lstConnections.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (this.currentSelectedConnection.get() != null) {
                    openConnection(this.currentSelectedConnection.get());
                }
            }
        });
    }

    public void closeClicked() {
        this.getStage().close();
    }

    public void createClicked() {
        resetFields();
        this.currentSelectedConnection.set(null);
        this.txtHost.requestFocus();
    }

    private void resetFields() {
        this.txtName.setText("");
        this.txtHost.setText("");
        this.spnPort.getValueFactory().setValue(DEFAULT_PORT);
        this.txtPassphase.setText("");
        this.btnDelete.setDisable(true);
    }

    public void saveClicked() {

        if (StringUtils.isBlank(txtName.getText())) {
            String name = txtHost.getText() + ":" + spnPort.getValue();
            txtName.setText(name);
        }

        Connection connection = this.currentSelectedConnection.get() != null ?
                this.currentSelectedConnection.get() : new Connection();

        connection.setName(txtName.getText());
        connection.setHost(txtHost.getText());
        connection.setPort(spnPort.getValue());
        connection.setPassphase(txtPassphase.getText());

        ConnectionManager.saveConnection(connection);
        lstConnections.getSelectionModel().select(connection);
    }

    public void copyClicked() {
        if (this.currentSelectedConnection.get() == null) {
            return;
        }

        Connection connection = this.currentSelectedConnection.get().clone();
        connection.setName(connection.getName() + "_COPY");
        ConnectionManager.saveConnection(connection);
    }

    public void deleteClicked() {

        String title = I18n.getString("op_delete");
        String message = I18n.getString("msg_delete_confirm");
        if (!Fx.confirmYesNo(title, message)) {
            return;
        }

        this.lstConnections.getItems().remove(this.currentSelectedConnection.get());
        resetFields();
        ConnectionManager.saveConnections();
    }

    public void testClicked() {

        btnTest.setDisable(true);

        Runnable runnable = () -> {
            String host = txtHost.getText();
            Integer port = spnPort.getValue();
            String passphase = txtPassphase.getText();

            try {
                JedisManager.connect(host, port, passphase);
                Fx.info("连接成功", "连接到 " + host + ":" + port + " 成功。");
            } catch (Exception e) {
                LOG.error("", e);
                Fx.error("连接失败", "连接到 " + host + ":" + port + " 失败：\n\n" + e.toString());
            } finally {
                btnTest.setDisable(false);
            }
        };

        new Thread(runnable).start();
    }

    public void openConnectionClicked() {
        Connection connection = new Connection();

        connection.setName(txtName.getText());
        connection.setHost(txtHost.getText());
        connection.setPort(spnPort.getValue());
        connection.setPassphase(txtPassphase.getText());

        openConnection(connection);
    }

    private void openConnection(Connection connection) {
        try {
            JedisManager.connect(connection);
            App.getEventBus().post(EventType.ConnectionOpened);
            this.getStage().close();
        } catch (Exception e) {
            LOG.error("", e);
            Fx.error("连接失败", "连接到 " + connection.getHost() +
                    ":" + connection.getPort() + " 失败：\n\n" + e.toString());
        }
    }
}
