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

    public void checkDatePicker() {
        LocalDate date = datePicker.getValue();
        if (date != null) {
            if (date.compareTo(LocalDate.now()) > 0) {
                datePicker.setValue(null);
            }
        }
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
        System.out.println("inside handleOKButton()");
        if (datePicker.getValue() == null || designersChoiceBox.getValue() == null ||
                workTimeTextField.getText() == null || workTimeTextField.getText().isEmpty()) {
            System.out.println("NOK!");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Укажите все данные");
            alert.setHeaderText("Укажите корректную дату,\nдизайнера и рабочее время");
            alert.showAndWait();
        }
        else {
            String timeString = workTimeTextField.getText();
        }
    }



}
