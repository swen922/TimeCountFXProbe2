package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.WeekFields;
import java.util.*;

public class EditUserWindowController {

    private Integer userID;
    private Stage myStage;
    private boolean isChanged = false;
    private Map<Node, String> textAreas = new HashMap<>();

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    @FXML
    private Label todayWorkSumLabel;

    @FXML
    private Label weekWorkSumLabel;

    @FXML
    private Label monthWorkSumLabel;

    @FXML
    private Label yearWorkSumLabel;

    @FXML
    private Label hoursToday;

    @FXML
    private Label hoursWeek;

    @FXML
    private Label hoursMonth;

    @FXML
    private Label hoursYear;


    @FXML
    private AnchorPane topColoredAnchorPane;

    @FXML
    private Label userIDLabel;

    @FXML
    private TextArea loginTextArea;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextArea fullNameTextArea;

    @FXML
    private CheckBox retiredCheckBox;

    @FXML
    private ChoiceBox<Role> roleChoiceBox;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField hourPayTextField;



    @FXML
    private Button revertButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button saveAndCloseButton;

    @FXML
    private Button closeButton;




    @FXML
    private void initialize() {
        if (this.userID == null) {
            this.userID = AllData.IDnumberForEdit;
        }
        initWorkSum();
        userIDLabel.setText(String.valueOf(userID));

        loginTextArea.setText(AllUsers.getOneUser(userID).getNameLogin());
        textAreas.put(loginTextArea, AllUsers.getOneUser(userID).getNameLogin());
        fullNameTextArea.setText(AllUsers.getOneUser(userID).getFullName());
        textAreas.put(fullNameTextArea, AllUsers.getOneUser(userID).getFullName());
        emailTextField.setText(AllUsers.getOneUser(userID).getEmail());
        textAreas.put(emailTextField, AllUsers.getOneUser(userID).getEmail());
        hourPayTextField.setText(AllData.formatInputInteger(AllUsers.getOneUser(userID).getWorkHourValue()));
        textAreas.put(hourPayTextField, AllData.formatInputInteger(AllUsers.getOneUser(userID).getWorkHourValue()));

        initRetiredCheckBox();
        initRoleChoiceBox();
        initSaveButtons();


    }

    private void initWorkSum() {

        if (AllUsers.getOneUser(userID).isRetired()) {
            todayWorkSumLabel.setText("0");
            hoursToday.setText("часов");
            weekWorkSumLabel.setText("0");
            hoursWeek.setText("часов");
            monthWorkSumLabel.setText("0");
            hoursMonth.setText("часов");
            yearWorkSumLabel.setText("0");
            hoursYear.setText("часов");
        }
        else {
            LocalDate today = LocalDate.now();
            int week = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
            int year = Year.from(today).getValue();
            int month = today.getMonth().getValue();

            List<Project> projects = AllData.getAllProjectsForDesignerAndDate(userID, today);
            int daySum = 0;
            for (Project p : projects) {
                daySum += p.getWorkSumForDesignerAndDate(userID, today);
            }
            todayWorkSumLabel.setText(AllData.formatWorkTime(AllData.intToDouble(daySum)));
            hoursToday.setText(AllData.formatHours(String.valueOf(AllData.intToDouble(daySum))));

            projects = AllData.getAllProjectsForDesignerAndWeek(userID, year, week);
            int weekSum = 0;
            for (Project p : projects) {
                weekSum += p.getWorkSumForDesignerAndWeek(userID, year, week);
            }
            weekWorkSumLabel.setText(AllData.formatWorkTime(AllData.intToDouble(weekSum)));
            hoursWeek.setText(AllData.formatHours(String.valueOf(AllData.intToDouble(weekSum))));

            projects = AllData.getAllProjectsForDesignerAndMonth(userID, year, month);
            int monthSum = 0;
            for (Project p : projects) {
                monthSum += p.getWorkSumForDesignerAndMonth(userID, year, month);
            }
            monthWorkSumLabel.setText(AllData.formatWorkTime(AllData.intToDouble(monthSum)));
            hoursMonth.setText(AllData.formatHours(String.valueOf(AllData.intToDouble(monthSum))));

            projects = AllData.getAllProjectsForDesignerAndYear(userID, year);
            int yearSum = 0;
            for (Project p : projects) {
                yearSum += p.getWorkSumForDesignerAndYear(userID, year);
            }
            yearWorkSumLabel.setText(AllData.formatWorkTime(AllData.intToDouble(yearSum)));
            hoursYear.setText(AllData.formatHours(String.valueOf(AllData.intToDouble(yearSum))));
        }
    }

    private void initRetiredCheckBox() {
        if (AllUsers.getOneUser(userID).isRetired()) {
            retiredCheckBox.setSelected(true);
            topColoredAnchorPane.setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
        }
        else {
            retiredCheckBox.setSelected(false);
            topColoredAnchorPane.setStyle(null);
        }

        retiredCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (retiredCheckBox.isSelected()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Подтверждение увольнения");
                    alert.setHeaderText("Уволить работника\n" + AllUsers.getOneUser(userID).getFullName() + "?");

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        boolean retired = AllUsers.deleteUser(userID);
                        if (retired) {
                            retiredCheckBox.setSelected(true);
                            topColoredAnchorPane.setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                            AllData.staffWindowController.initializeTable();
                            initialize();
                        }
                        else {
                            Alert notRetired = new Alert(Alert.AlertType.WARNING);
                            notRetired.setTitle("Ошибка в программе");
                            notRetired.setHeaderText("Не удалось уволить работника " + AllUsers.getOneUser(userID).getFullName() + ".\nОбратитесь за помощью к Анисимову");
                        }
                    }
                    else {
                        retiredCheckBox.setSelected(false);
                        topColoredAnchorPane.setStyle(null);
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Подтверждение возвращения");
                    alert.setHeaderText("Принять работника\n" + AllUsers.getOneUser(userID).getFullName() + "\nснова на работу?");

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == ButtonType.OK) {
                        boolean returned = AllUsers.resurrectUser(userID);
                        if (returned) {
                            retiredCheckBox.setSelected(false);
                            topColoredAnchorPane.setStyle(null);
                            AllData.staffWindowController.initializeTable();
                            initialize();
                        }
                        else {
                            Alert notReturned = new Alert(Alert.AlertType.WARNING);
                            notReturned.setTitle("Ошибка в программе");
                            notReturned.setHeaderText("Не удалось вернуть работника " + AllUsers.getOneUser(userID).getFullName() + ".\nОбратитесь за помощью к Анисимову");
                        }
                    }
                    else {
                        retiredCheckBox.setSelected(true);
                        topColoredAnchorPane.setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                    }

                }
            }
        });
    }

    private void initRoleChoiceBox() {
        roleChoiceBox.setItems(FXCollections.observableArrayList(Role.values()));
        roleChoiceBox.setValue(AllUsers.getOneUser(userID).getRole());
        roleChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!roleChoiceBox.getValue().equals(AllUsers.getOneUser(userID).getRole())) {
                    isChanged = true;
                    /*AllUsers.getOneUser(userID).setRole(roleChoiceBox.getValue());
                    AllData.staffWindowController.initializeTable();*/
                }
            }
        });
    }


    public void initSaveButtons() {
        if (!isChanged) {
            revertButton.setDisable(true);
            saveButton.setDisable(true);
            saveAndCloseButton.setDisable(true);
        }
        else {
            revertButton.setDisable(false);
            saveButton.setDisable(false);
            saveAndCloseButton.setDisable(false);
        }
    }



    public void handleSaveButton() {

        textAreas.put(loginTextArea, loginTextArea.getText());
        for (User u : AllUsers.getUsers().values()) {
            if (u.getNameLogin().equalsIgnoreCase(loginTextArea.getText())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
            }
        }

        AllUsers.getOneUser(userID).setNameLogin(loginTextArea.getText());




        isChanged = false;
        initSaveButtons();
        AllData.tableProjectsManagerController.initialize();
    }


    public void handleRevertButton() {
        loginTextArea.setText(AllUsers.getOneUser(userID).getNameLogin());
        passwordField.setText(null);
        fullNameTextArea.setText(AllUsers.getOneUser(userID).getFullName());
        roleChoiceBox.setValue(AllUsers.getOneUser(userID).getRole());
        emailTextField.setText(AllUsers.getOneUser(userID).getEmail());
        hourPayTextField.setText(AllData.formatInputInteger(AllUsers.getOneUser(userID).getWorkHourValue()));
        isChanged = false;
    }

    public void handleSaveAndCloseButton() {
        handleSaveButton();
        myStage.close();
        AllData.editUserStages.remove(userID);
        AllData.editProjectWindowControllers.remove(userID);
    }


    public void handleCloseButton() {

        if (!isChanged) {
            myStage.close();
            AllData.editUserStages.remove(userID);
            AllData.editProjectWindowControllers.remove(userID);
        }
        else {
            handleAlerts();
        }
    }

    private void handleAlerts() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение закрытия");
        alert.setHeaderText("В текст полей пользователя id-" + userID + " " + AllUsers.getOneUser(userID).getFullName() + "\nбыли внесены изменения.\nСохранить их или проигнорировать?");

        ButtonType returnButton = new ButtonType("Вернуться обратно");
        ButtonType dontSaveButton = new ButtonType("Не сохранять");
        ButtonType saveButton = new ButtonType("Сохранить");

        // Remove default ButtonTypes
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(returnButton, dontSaveButton, saveButton);

        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == returnButton) {
            alert.close();
        }
        else if (option.get() == dontSaveButton) {
            handleRevertButton();
            AllData.editUserStages.remove(userID);
            AllData.editProjectWindowControllers.remove(userID);
            alert.close();
            myStage.close();
        }
        else {
            alert.close();
            handleSaveAndCloseButton();
        }
    }






    public void closing() {

        if (!isChanged) {
            if (AllData.editUserStages.containsKey(userID)) {
                AllData.editUserStages.get(userID).close();
                AllData.editUserStages.remove(userID);
            }
            if (AllData.editUserWindowControllers.containsKey(userID)) {
                AllData.editUserWindowControllers.remove(userID);
            }

        }
    }

    public void initClosing() {
        System.out.println("inside initClosing()");
        AllData.editUserStages.get(userID).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                System.out.println("inside handle() EditUserWindowController");

                //AllData.editUserStages.get(userID).close();
                AllData.editUserStages.remove(userID);
                AllData.editUserWindowControllers.remove(userID);
                System.out.println("set on close");
                System.out.println("inside EditUserController: mAllData.editUserStages.containsKey(userID) = " + AllData.editUserStages.containsKey(userID));

                /*if (!isChanged) {
                    System.out.println("set on close");
                    AllData.editUserStages.get(userID).close();
                    AllData.editUserStages.remove(userID);
                    AllData.editUserWindowControllers.remove(userID);
                }
                else {

                }*/
            }
        });
    }
}
