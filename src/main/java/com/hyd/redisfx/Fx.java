package com.hyd.redisfx;

import com.hyd.redisfx.controllers.conn.BaseController;
import com.hyd.redisfx.i18n.I18n;
import java.io.IOException;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (description)
 * created at 17/03/20
 *
 * @author yiding_he
 */
public class Fx {

    public static final KeyCodeCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);

    public static final KeyCodeCombination CTRL_C = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

    public static final KeyCodeCombination CTRL_B = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);

    private static final Logger LOG = LoggerFactory.getLogger(Fx.class);

    public static void error(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    private static Function<String, FXMLLoader> fxmlLoaderSupplier = fxmlPath -> {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Fx.class.getResource(fxmlPath));
        return fxmlLoader;
    };

    public static void setFxmlLoaderSupplier(Function<String, FXMLLoader> fxmlLoaderSupplier) {
        Fx.fxmlLoaderSupplier = fxmlLoaderSupplier;
    }

    public static FXMLLoader getFxmlLoader(String fxmlPath) {
        FXMLLoader fxmlLoader = fxmlLoaderSupplier.apply(fxmlPath);
        fxmlLoader.setResources(I18n.UI_MAIN_BUNDLE);
        return fxmlLoader;
    }

    @SuppressWarnings("unchecked")
    public static FXMLLoader loadFxml(String fxmlPath) {
        FXMLLoader fxmlLoader = getFxmlLoader(fxmlPath);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            LOG.error("", e);
            error(e.toString());
        }

        return fxmlLoader;
    }

    public static FXMLLoader showDialog(Stage owner, String title, String fxmlPath) {
        FXMLLoader fxmlLoader = loadFxml(fxmlPath);

        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);

        if (owner != null) {
            dialogStage.initOwner(owner);
            dialogStage.initModality(Modality.WINDOW_MODAL);
        }

        Object controller = fxmlLoader.getController();
        if (controller instanceof BaseController) {
            ((BaseController) controller).setStage(dialogStage);
        }

        Icons.Logo.setToStage(dialogStage);

        dialogStage.setScene(new Scene(fxmlLoader.getRoot()));
        dialogStage.show();
        return fxmlLoader;
    }

    //////////////////////////////////////////////////////////////

    public static void runInFxApplicationThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    //////////////////////////////////////////////////////////////

    public static void error(String title, String message) {
        alert(Alert.AlertType.ERROR, title, message);
    }

    public static void info(String title, String message) {
        alert(Alert.AlertType.INFORMATION, title, message);
    }

    public static void warn(String title, String message) {
        alert(Alert.AlertType.WARNING, title, message);
    }

    private static void alert(Alert.AlertType alertType, String title, String message) {
        runInFxApplicationThread(() -> {
            Alert alert = new Alert(alertType, message, ButtonType.OK);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    public static boolean confirmOkCancel(String title, String message) {
        return confirm(Alert.AlertType.CONFIRMATION, title, message, ButtonType.OK, ButtonType.CANCEL) == ButtonType.OK;
    }

    public static boolean confirmYesNo(String title, String message) {
        return confirm(Alert.AlertType.CONFIRMATION, title, message, ButtonType.YES, ButtonType.NO) == ButtonType.YES;
    }

    public static ButtonType confirmYesNoCancel(String title, String message) {
        return confirm(Alert.AlertType.WARNING, title, message, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    }

    public static ButtonType confirm(Alert.AlertType alertType, String title, String message, ButtonType... buttonTypes) {
        Alert alert = new Alert(alertType, message, buttonTypes);
        alert.setTitle(title);
        alert.setHeaderText(null);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Icons.Logo.setToStage(stage);

        return alert.showAndWait().orElse(ButtonType.CANCEL);
    }

    //////////////////////////////////////////////////////////////

    @SafeVarargs
    public static void fixIntegerSpinners(Spinner<Integer>... spinners) {
        for (Spinner<Integer> spinner : spinners) {
            fixIntegerSpinner(spinner);
        }
    }

    public static void fixIntegerSpinner(Spinner<Integer> spinner) {
        spinner.getEditor().textProperty().addListener((_ob, _old, _new) -> {
            try {
                int value = Integer.parseInt(_new);
                spinner.getValueFactory().setValue(value);
            } catch (NumberFormatException e) {
                // nothing
            }
        });
    }

    //////////////////////////////////////////////////////////////

    public static void copyText(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    ///////////////////////////////////////////////////////////////

    public static void nodeOnKeyPress(
            Node node, KeyCombination keyCombination, Runnable onAction) {

        node.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (keyCombination.match(event)) {
                try {
                    onAction.run();
                } catch (Exception e) {
                    LOG.error("", e);
                    error(e.toString());
                } finally {
                    event.consume();
                }
            }
        });
    }


}
