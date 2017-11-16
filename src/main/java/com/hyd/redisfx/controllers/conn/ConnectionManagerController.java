package com.hyd.redisfx.controllers.conn;

import com.hyd.redisfx.App;
import com.hyd.redisfx.Fx;
import com.hyd.redisfx.conn.Connection;
import com.hyd.redisfx.conn.ConnectionManager;
import com.hyd.redisfx.controllers.client.JedisManager;
import com.hyd.redisfx.event.EventType;
import com.hyd.redisfx.i18n.I18n;
import com.hyd.redisfx.nodes.IntegerSpinner;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Protocol;

import java.net.Proxy;

/**
 * @author yiding_he
 */
public class ConnectionManagerController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManagerController.class);

    public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;

    public TextField txtName;

    public TextField txtHost;

    public IntegerSpinner spnPort;

    public PasswordField txtPassphase;

    public Button btnCreate;

    public Button btnTest;

    public Button btnSave;

    public Button btnCopy;

    public Button btnDelete;

    public Button btnOpen;

    public ListView<Connection> lstConnections;

    public TextField txtProxyHost;

    public IntegerSpinner spnProxyPort;

    private SimpleObjectProperty<Connection> currentSelectedConnection = new SimpleObjectProperty<>();

    public void initialize() {

        initSpinners();
        initListView();

        this.txtHost.textProperty().addListener((_ob, _old, _new) -> {
            btnSave.setDisable(StringUtils.isBlank(txtHost.getText()));
            btnTest.setDisable(StringUtils.isBlank(txtHost.getText()));
            btnCopy.setDisable(StringUtils.isBlank(txtHost.getText()));
            btnOpen.setDisable(StringUtils.isBlank(txtHost.getText()));
        });

        this.currentSelectedConnection.addListener((_ob, _old, _new) -> {
            this.btnDelete.setDisable(_new == null);
            if (_new != null) {
                updateForm(_new);
            } else {
                resetFields();
            }
        });
    }

    private void initSpinners() {
        this.spnPort.setRangeAndValue(1, 65535, DEFAULT_PORT);
        this.spnProxyPort.setRangeAndValue(0, 65535, 0);
    }

    private void updateForm(Connection _new) {
        this.txtName.setText(_new.getName());
        this.txtHost.setText(_new.getHost());
        this.spnPort.getValueFactory().setValue(_new.getPort());
        this.txtPassphase.setText(_new.getPassphase());
        this.txtProxyHost.setText(_new.getProxyHost());
        this.spnProxyPort.getValueFactory().setValue(_new.getProxyPort());
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
        this.txtProxyHost.setText("");
        this.spnProxyPort.getValueFactory().setValue(0);
    }

    public void saveClicked() {

        if (StringUtils.isBlank(txtName.getText())) {
            String name = txtHost.getText() + ":" + spnPort.getValue();
            txtName.setText(name);
        }

        Connection connection = this.currentSelectedConnection.get() != null ?
                this.currentSelectedConnection.get() : new Connection();

        updateConnectionFromUI(connection);

        ConnectionManager.saveConnection(connection);
        lstConnections.getSelectionModel().select(connection);
    }

    private void updateConnectionFromUI(Connection connection) {
        connection.setName(txtName.getText());
        connection.setHost(txtHost.getText());
        connection.setPort(spnPort.getValue());
        connection.setPassphase(txtPassphase.getText());
        connection.setProxyType(Proxy.Type.SOCKS.name());
        connection.setProxyHost(txtProxyHost.getText());
        connection.setProxyPort(spnProxyPort.getValue());
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
            Connection connection = new Connection();
            updateConnectionFromUI(connection);

            try {
                JedisManager.connect(connection);
                Fx.info("连接成功", "连接到 " + connection.getHost() + ":" + connection.getPort() + " 成功。");
            } catch (Exception e) {
                LOG.error("", e);
                Fx.error("连接失败", "连接到 " + connection.getHost() + ":" + connection.getPort() + " 失败：\n\n" + e.toString());
            } finally {
                btnTest.setDisable(false);
            }
        };

        new Thread(runnable).start();
    }

    public void openConnectionClicked() {
        Connection connection = new Connection();
        updateConnectionFromUI(connection);
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
