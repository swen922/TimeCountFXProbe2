package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Map;

public class EditingCellTime extends TableCell<Map.Entry<Integer, Project>, String> {

    private TextField textField;

    public EditingCellTime() {
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }

    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText((String) getItem());
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
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(null);
            }
            else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        String oldText = getString();
        textField = new TextField(oldText);
        textField.setAlignment(Pos.CENTER);
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    commitEdit(formatStringInput(oldText, textField.getText()));
                    EditingCellTime.this.getTableView().requestFocus();
                    EditingCellTime.this.getTableView().getSelectionModel().selectAll();
                    AllData.updateAllWindows();

                }
            }
        });
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!newValue) {
                        commitEdit(formatStringInput(oldText, textField.getText()));
                        EditingCellTime.this.getTableView().requestFocus();
                        EditingCellTime.this.getTableView().getSelectionModel().selectAll();
                        AllData.updateAllWindows();
                    }
                }
            });
        EditingCellTime.this.textField.selectAll();

    }

    private String formatStringInput(String oldText, String input) {
        String newText = input.replaceAll(" ", ".");
        newText = newText.replaceAll("-", ".");
        newText = newText.replaceAll(",", ".");
        newText = newText.replaceAll("=", ".");

        Double newTimeDouble = null;
        try {
            newTimeDouble = Double.parseDouble(newText);
        } catch (NumberFormatException e) {
            return oldText;
        }
        if (newTimeDouble != null) {
            newText = String.valueOf(AllData.formatDouble(newTimeDouble, 2));
            return newText;
        }

        return oldText;
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
