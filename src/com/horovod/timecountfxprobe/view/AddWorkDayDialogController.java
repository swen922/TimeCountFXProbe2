package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddWorkDayDialogController {
    private int projectIDnumber;
    private ObservableList<String> designers = FXCollections.observableArrayList();
    private Stage myStage;

    public void setProjectIDnumber(int projectIDnumber) {
        this.projectIDnumber = projectIDnumber;
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }


    @FXML
    private DatePicker datePicker;

    @FXML
    private Button clearDatePickerButton;

    @FXML
    private ChoiceBox<String> designersChoiceBox;

    @FXML
    private Button clearDesignersChoiceBoxButton;

    @FXML
    private TextField workTimeTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;



    @FXML
    private void initialize() {

        initButtons();
        checkButtons();

        if (designers == null) {
            designers = FXCollections.observableArrayList();
        }

        for (User u : AllUsers.getActiveDesigners().values()) {
            designers.add(u.getFullName());
        }
        designersChoiceBox.setItems(designers);

        designersChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkButtons();
            }
        });

        workTimeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    Double result = checkWorkTime();
                    if (result == null) {
                        workTimeTextField.setText("");
                    }
                    else {
                        workTimeTextField.setText(AllData.formatWorkTime(result));
                    }
                }
            }
        });
    }

    private void initButtons() {
        cancelButton.setCancelButton(true);
        okButton.setDefaultButton(true);
    }

    public void checkButtons() {
        LocalDate date = getCorrectDate();
        Double time = checkWorkTime();
        User u = AllUsers.getOneUserForFullName(designersChoiceBox.getValue());

        if (date == null || time == null || u == null) {
            okButton.setDisable(true);
        }
        else {
            okButton.setDisable(false);
        }
    }


    public void handleClearDatePicker() {
        datePicker.setValue(null);
        checkButtons();
    }

    public void handleClearChoiceBox() {
        designersChoiceBox.setValue(null);
        checkButtons();
    }

    public void handleCancelButton() {
        myStage.close();
    }

    public void handleOKButton() {
        LocalDate date = getCorrectDate();
        Double time = checkWorkTime();
        User user = AllUsers.getOneUserForFullName(designersChoiceBox.getValue());

        if (date != null && time != null && user != null) {

            AllData.addWorkTime(projectIDnumber, date, user.getIDNumber(), time);

            AllData.tableProjectsManagerController.initialize();
            if (AllData.editProjectWindowControllers.get(projectIDnumber) != null) {
                AllData.editProjectWindowControllers.get(projectIDnumber).initializeTable();
            }
            if (AllData.staffWindowController != null) {
                AllData.staffWindowController.initializeTable();
            }
            if (AllData.statisticManagerWindowController != null) {
                AllData.statisticManagerWindowController.handleButtonReloadBarChart();
            }
            myStage.close();
        }
    }

    public LocalDate getCorrectDate() {

        if (datePicker.getValue() == null) {
            return null;
        }

        if (datePicker.getValue().isAfter(LocalDate.now())) {
            datePicker.setValue(LocalDate.now());
        }

        /*if (datePicker.getValue().isBefore(AllData.parseDate(AllData.getAnyProject(projectIDnumber).getDateCreationString()))) {
            datePicker.setValue(AllData.parseDate(AllData.getAnyProject(projectIDnumber).getDateCreationString()));
        }*/

        return datePicker.getValue();
    }

    public void checkDatePicker() {
        if (datePicker.getValue() != null) {
            if (datePicker.getValue().isAfter(LocalDate.now())) {
                datePicker.setValue(LocalDate.now());
            }
        }

        /*if (datePicker.getValue().isBefore(AllData.parseDate(AllData.getAnyProject(projectIDnumber).getDateCreationString()))) {
            datePicker.setValue(AllData.parseDate(AllData.getAnyProject(projectIDnumber).getDateCreationString()));
        }*/

        checkButtons();
    }



    private Double checkWorkTime() {

        if (workTimeTextField.getText() == null || workTimeTextField.getText().isEmpty()) {
            return null;
        }

        return AllData.getDoubleFromText(0, workTimeTextField.getText(), 1);

    }

}
