package com.hyd.redisfx.fx;

import com.hyd.redisfx.App;
import com.hyd.redisfx.Icons;
import com.hyd.redisfx.i18n.I18n;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public abstract class FormDialog extends Stage {

    private final Button cancelButton = new Button(I18n.getString("word_cancel"));

    private final Button okButton = new Button(I18n.getString("word_ok"));

    public FormDialog() {
        this(App.getMainController().getPrimaryStage());
    }

    public FormDialog(Stage owner) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(
                getContentPane(),
                new Separator(Orientation.HORIZONTAL),
                getButtonsPane()
        );
        root.getStylesheets().add("css/style.css");
        root.setMinWidth(200);
        root.setMinHeight(100);
        setScene(new Scene(root));

        if (owner != null) {
            this.initModality(Modality.WINDOW_MODAL);
            this.initOwner(owner);
        }

        Icons.Logo.setToStage(this);

        okButton.setOnAction(event -> this.okButtonClicked());
        cancelButton.setOnAction(event -> this.cancelButtonClicked());
    }

    //////////////////////////////////////////////////////////////

    protected abstract void okButtonClicked();

    protected abstract void cancelButtonClicked();

    //////////////////////////////////////////////////////////////

    private VBox getContentPane() {
        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        return vBox;
    }

    private HBox getButtonsPane() {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        hBox.getChildren().addAll(okButton, cancelButton);
        return hBox;
    }
}
