package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

        if (designers == null) {
            designers = FXCollections.observableArrayList();
        }

        for (User u : AllUsers.getUsers().values()) {
            if (u.getRole().equals(Role.DESIGNER)) {
                designers.add(u.getFullName());
            }
        }
        designersChoiceBox.setItems(designers);

        workTimeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    handleOKButton();
                }
            }
        });
    }


    public void handleClearDatePicker() {
        datePicker.setValue(null);
    }

    public void handleClearChoiceBox() {
        designersChoiceBox.setValue(null);
    }

    public void handleCancelButton() {
        myStage.close();
    }

    public void handleOKButton() {

        LocalDate date = checkDatePicker();
        Double time = checkWorkTime();

        if (date == null || designersChoiceBox.getValue() == null || time == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Укажите все данные");
            alert.setHeaderText("Укажите корректную дату,\nдизайнера и рабочее время");
            alert.showAndWait();
        }
        else if (date.compareTo(AllData.parseDate(AllData.getOneProject(projectIDnumber).getDateCreationString())) < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Неправильная дата");
            alert.setHeaderText("Дата не может быть раньше даты создания проекта");
            StringBuilder text = new StringBuilder("Дата создания проекта id-");
            text.append(projectIDnumber).append(" – ").append(AllData.getOneProject(projectIDnumber).getDateCreationString());
            text.append("\nУказанная вами дата должна быть равна или позже даты создания проекта");
            alert.setContentText(text.toString());
            alert.showAndWait();
        }
        else {
            AllData.addWorkTime(projectIDnumber, datePicker.getValue(), AllUsers.getOneUserForFullName(designersChoiceBox.getValue()).getIDNumber(), time);
            AllData.tableProjectsManagerController.initialize();
            if (AllData.editProjectWindowControllers.get(projectIDnumber) != null) {
                AllData.editProjectWindowControllers.get(projectIDnumber).initializeTable();
            }
            if (AllData.staffWindowController != null) {
                AllData.staffWindowController.initializeTable();
            }
            myStage.close();
        }
    }

    private LocalDate checkDatePicker() {

        if (datePicker.getValue() == null) {
            return null;
        }

        if (datePicker.getValue().isAfter(LocalDate.now())) {
            datePicker.setValue(null);
            return null;
        }

        return datePicker.getValue();
    }

    private Double checkWorkTime() {

        String newText = workTimeTextField.getText().replaceAll(" ", ".");
        newText = newText.replaceAll("-", ".");
        newText = newText.replaceAll(",", ".");
        newText = newText.replaceAll("=", ".");

        Double result = null;
        try {
            result = Double.parseDouble(newText);
        } catch (NumberFormatException e) {
            workTimeTextField.setText(null);
        }
        return result;
    }



}
