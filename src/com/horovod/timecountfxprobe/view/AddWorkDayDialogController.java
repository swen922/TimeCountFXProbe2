package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddWorkDayDialogController {
    int projectIDnumber;
    ObservableList<String> designers = FXCollections.observableArrayList();
    Stage myStage;


    public int getProjectIDnumber() {
        return projectIDnumber;
    }

    public void setProjectIDnumber(int projectIDnumber) {
        this.projectIDnumber = projectIDnumber;
    }

    public Stage getMyStage() {
        return myStage;
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

        if (datePicker.getValue() == null || designersChoiceBox.getValue() == null ||
                workTimeTextField.getText() == null || workTimeTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Укажите все данные");
            alert.setHeaderText("Укажите корректную дату,\nдизайнера и рабочее время");
            alert.showAndWait();
        }
        else {
            LocalDate date = checkDatePicker();
            Double time = checkWorkTime();
            if (date == null) {
                datePicker.setValue(null);
                return;
            }
            if (time == null) {
                workTimeTextField.setText(null);
                return;
            }

            System.out.println(projectIDnumber);
            System.out.println(datePicker.getValue());
            System.out.println(AllUsers.getOneUserForFullName(designersChoiceBox.getValue()).getIDNumber());
            System.out.println(time);
            AllData.addWorkTime(projectIDnumber, datePicker.getValue(), AllUsers.getOneUserForFullName(designersChoiceBox.getValue()).getIDNumber(), time);
        }
    }

    private LocalDate checkDatePicker() {
        LocalDate result = null;
        if (datePicker.getValue().isAfter(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Некорректно указана дата!");
            alert.setHeaderText("Укажите корректную дату");
            alert.showAndWait();
        }
        else {
            result = datePicker.getValue();
        }
        return result;
    }

    private double checkWorkTime() {

        String newText = workTimeTextField.getText().replaceAll(" ", ".");
        newText = newText.replaceAll("-", ".");
        newText = newText.replaceAll(",", ".");
        newText = newText.replaceAll("=", ".");

        Double result = null;
        try {
            result = Double.parseDouble(newText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Некорректно указано время!");
            alert.setHeaderText("Укажите корректное рабочее время");
            alert.showAndWait();
        }
        System.out.println(result == null);
        return result;
    }



}
