package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;

public class CountSalaryWindowController {

    private List<CheckBox> usersCheckBoxes = new ArrayList<>();


    @FXML
    private CheckBox designersOnlyCheckBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private Button clearFromDatePickerButton;

    @FXML
    private DatePicker tillDatePicker;

    @FXML
    private Button clearTillDatePickerButton;

    @FXML
    private Button selectAllButton;

    @FXML
    private Button deselectAllButton;

    @FXML
    private FlowPane usersFlowPane;

    @FXML
    private Button countSalaryButton;

    @FXML
    private TextArea textArea;


    @FXML
    private void initialize() {
        if (this.usersCheckBoxes == null) {
            this.usersCheckBoxes = new ArrayList<>();
        }

        designersOnlyCheckBox.setSelected(true);

        initUsers();

    }

    public void initUsers() {
        if (designersOnlyCheckBox.isSelected()) {
            for (User u : AllUsers.getActiveDesigners().values()) {
                CheckBox userCheckBox = new CheckBox(u.getFullName());
                userCheckBox.setSelected(true);
                usersFlowPane.getChildren().add(userCheckBox);
            }
        }
        else {
            for (User u : AllUsers.getActiveUsers().values()) {
                CheckBox userCheckBox = new CheckBox(u.getFullName());
                userCheckBox.setSelected(true);
                usersFlowPane.getChildren().add(userCheckBox);

            }
        }
    }











}
