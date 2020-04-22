package com.hyd.redisfx.fx;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

import com.hyd.redisfx.App;
import com.hyd.redisfx.Icons;
import com.hyd.redisfx.i18n.I18n;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * (description)
 * created at 2017/7/12
 *
 * @author yidin
 */
public abstract class FormDialog extends Stage {

    private final Button cancelButton = new Button(I18n.getString("word_cancel"));

    private final Button okButton = new Button(I18n.getString("word_ok"));

    private final GridPane contentPane = new GridPane();

    private boolean ok;

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

        okButton.setOnAction(this::okButtonClicked);
        cancelButton.setOnAction(this::cancelButtonClicked);
        this.setOnCloseRequest(this::closeButtonClicked);
    }

    public boolean isOk() {
        return ok;
    }

    //////////////////////////////////////////////////////////////

    protected abstract void okButtonClicked(ActionEvent event);

    protected void cancelButtonClicked(ActionEvent event) {
        this.close();
    }

    protected void closeButtonClicked(WindowEvent event) {
        this.close();
    }

    //////////////////////////////////////////////////////////////

    private GridPane getContentPane() {
        contentPane.setHgap(10);
        contentPane.setVgap(10);

        // top-align all child nodes
        contentPane.getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(node ->
                            GridPane.setValignment(node, VPos.TOP));
                }
            }
        });

        ColumnConstraints titleCC = new ColumnConstraints();
        titleCC.setPrefWidth(USE_COMPUTED_SIZE);
        titleCC.setMinWidth(USE_COMPUTED_SIZE);

        ColumnConstraints valueCC = new ColumnConstraints();
        valueCC.setFillWidth(true);
        valueCC.setHgrow(Priority.ALWAYS);

        contentPane.getColumnConstraints().addAll(titleCC, valueCC);

        VBox.setVgrow(contentPane, Priority.ALWAYS);
        return contentPane;
    }

    private HBox getButtonsPane() {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        hBox.getChildren().addAll(okButton, cancelButton);
        return hBox;
    }

    protected void closeOK() {
        this.ok = true;
        this.close();
    }

    protected void closeNotOK() {
        this.ok = false;
        this.close();
    }

    //////////////////////////////////////////////////////////////

    private List<FormField> formFields = new ArrayList<>();

    protected void addField(FormField formField) {
        this.formFields.add(formField);
        layoutFormField(formField, this.formFields.size() - 1);
    }

    private void layoutFormField(FormField formField, int rowIndex) {
        formField.renderTo(this.contentPane, rowIndex);
    }

}
