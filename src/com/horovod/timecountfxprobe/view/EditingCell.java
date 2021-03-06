package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Map;

public class EditingCell<T, String> extends TableCell<T, String> {

    private TextField textField;
    private FillChartMode fillChartMode;

    public EditingCell(FillChartMode fillChartMode) {
        this.fillChartMode = fillChartMode;
    }

    /*public EditingCell() {
    }*/

    @Override
    public void startEdit() {
        try {

            Integer num = (Integer) this.getTableView().getColumns().get(1).getCellObservableValue(this.getIndex()).getValue();
                    Project p = AllData.getAnyProject(num);
            if (!p.isArchive() && !AllUsers.getOneUser(AllUsers.getCurrentUser()).isRetired()) {
                if (!isEmpty()) {
                    super.startEdit();
                    createTextField();
                    setText(null);
                    setGraphic(textField);
                    textField.selectAll();
                }
            }
        } catch (Exception e) {
            AllData.logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText((java.lang.String) getItem());
        setGraphic(null);
    }

    @Override
    protected void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);
        if (empty) {

            setText(null);
            setGraphic(null);
        }
        else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText((java.lang.String) getString());
                }
                setText(null);
                setGraphic(null);
            }
            else {
                setText((java.lang.String) getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        String oldText = getString();
        textField = new TextField((java.lang.String) oldText);
        textField.setAlignment(Pos.CENTER);
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (fillChartMode.equals(FillChartMode.TIME)) {
                    textField.setText("");
                }
            }
        });
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    if (fillChartMode.equals(FillChartMode.TIME)) {
                        commitEdit((String) AllData.formatStringInputDouble((java.lang.String) oldText, textField.getText(), 1));
                    }
                    else {
                        commitEdit((String) AllData.formatStringInputInteger((java.lang.String) oldText, textField.getText()));

                    }
                    EditingCell.this.getTableView().requestFocus();
                    EditingCell.this.getTableView().getSelectionModel().selectAll();
                    //initialize();
                    //projectsTable.refresh();
                }
            }
        });
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    if (fillChartMode.equals(FillChartMode.TIME)) {
                        commitEdit((String) AllData.formatStringInputDouble((java.lang.String) oldText, textField.getText(), 1));
                    }
                    else {
                        commitEdit((String) AllData.formatStringInputInteger((java.lang.String) oldText, textField.getText()));
                    }
                    EditingCell.this.getTableView().requestFocus();
                    EditingCell.this.getTableView().getSelectionModel().selectAll();
                }
            }
        });
        textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                textField.selectAll();
            }
        });
        EditingCell.this.textField.selectAll();

    }

    private String getString() {

        return (String) (getItem() == null ? "-" : getItem().toString());
    }

}
