package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.SecurePassword;
import com.horovod.timecountfxprobe.user.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    String latinForPass = "1234567890abcdefghijklmnopqrstuvwxyz@.-_";

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
    private TextField passwordField;

    @FXML
    private TextArea fullNameTextArea;

    @FXML
    private CheckBox retiredCheckBox;

    @FXML
    private Label roleLabel;

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
        String email = AllUsers.getOneUser(userID).getEmail() == null ? "" : AllUsers.getOneUser(userID).getEmail();
        emailTextField.setText(email);
        textAreas.put(emailTextField, email);
        String hourPay = AllUsers.getOneUser(userID).getWorkHourValue() == 0 ? "" : AllData.formatInputInteger(AllUsers.getOneUser(userID).getWorkHourValue());
        hourPayTextField.setText(hourPay);
        textAreas.put(hourPayTextField, hourPay);

        initRetiredCheckBox();
        initHourPayTextField();
        initRoleLabel();
        initSaveButtons();

        System.out.println("inside EditUserWindow" + AllUsers.getOneUser(userID).getFullName());

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
                            if (AllData.countSalaryWindowController != null) {
                                AllData.countSalaryWindowController.updateUsers();
                            }
                            initialize();
                        }
                        else {
                            Alert notRetired = new Alert(Alert.AlertType.WARNING);
                            notRetired.setTitle("Ошибка в программе");
                            notRetired.setHeaderText("Не удалось уволить работника " + AllUsers.getOneUser(userID).getFullName() + ".\nОбратитесь за помощью к Анисимову");
                            notRetired.showAndWait();
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
                            if (AllData.countSalaryWindowController != null) {
                                AllData.countSalaryWindowController.updateUsers();
                            }
                            initialize();
                        }
                        else {
                            Alert notReturned = new Alert(Alert.AlertType.WARNING);
                            notReturned.setTitle("Ошибка в программе");
                            notReturned.setHeaderText("Не удалось вернуть работника " + AllUsers.getOneUser(userID).getFullName() + ".\nОбратитесь за помощью к Анисимову");
                            notReturned.showAndWait();
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

    private void initRoleLabel() {
        //roleLabel.textProperty().bind(new SimpleStringProperty(AllUsers.getOneUser(userID).getRole().toString()));
        roleLabel.setText(AllUsers.getOneUser(userID).getRole().getTextRole());
    }

    private void initHourPayTextField() {
        hourPayTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                hourPayTextField.setText("");
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


    public void handleRevertButton() {
        loginTextArea.setText(AllUsers.getOneUser(userID).getNameLogin());
        passwordField.setText("");
        fullNameTextArea.setText(AllUsers.getOneUser(userID).getFullName());
        String mail = AllUsers.getOneUser(userID).getEmail() == null ? "" : AllUsers.getOneUser(userID).getEmail();
        emailTextField.setText(mail);
        String hourPay = AllUsers.getOneUser(userID).getWorkHourValue() == 0 ? "" : AllData.formatInputInteger(AllUsers.getOneUser(userID).getWorkHourValue());
        hourPayTextField.setText(hourPay);
        isChanged = false;
        initSaveButtons();
    }


    public void handleSaveButton() {

        if (!textAreas.get(loginTextArea).equalsIgnoreCase(loginTextArea.getText().trim())) {
            if (loginTextArea.getText() == null || loginTextArea.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Неправильный логин");
                StringBuilder sb = new StringBuilder("Указанный вами новый логин\n");
                sb.append("для пользователя id-").append(userID);
                sb.append(" ").append(AllUsers.getOneUser(userID).getFullName()).append("\n");
                sb.append("не соответствует правилам создания логинов.\n");
                sb.append("Логин должен состоять не менее, чем из 1 символа\n");
                sb.append("и не должен включать символы пробела и иные посторонние символы.\n");
                sb.append("Укажите другой логин, состоящий только из латинских букв и цифр!");

                alert.setHeaderText(sb.toString());

                alert.showAndWait();
                return;
            }



            if (AllUsers.isNameLoginExist(loginTextArea.getText().trim())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Дублирующийся логин");
                StringBuilder sb = new StringBuilder("Указанный вами новый логин \"");
                sb.append(loginTextArea.getText()).append("\"\nдля пользователя id-").append(userID);
                sb.append(" ").append(AllUsers.getOneUser(userID).getFullName()).append("\n");
                sb.append("совпадает с логином другого пользователя\n");
                sb.append("Укажите другой логин!");

                alert.setHeaderText(sb.toString());

                alert.showAndWait();
                return;
            }

            boolean hasOtherSymbols = false;
            char[] newLoginArray = loginTextArea.getText().trim().toLowerCase().toCharArray();
            for (Character ch : newLoginArray) {
                if (!latinForPass.contains(ch.toString())) {
                    hasOtherSymbols = true;
                    break;
                }
            }
            if (loginTextArea.getText().trim().length() < 1 || hasOtherSymbols) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Неправильный логин");
                StringBuilder sb = new StringBuilder("Указанный вами новый логин\n");
                sb.append("для пользователя id-").append(userID);
                sb.append(" ").append(AllUsers.getOneUser(userID).getFullName()).append("\n");
                sb.append("не соответствует правилам создания логинов.\n");
                sb.append("Логин должен состоять не менее, чем из 1 символа\n");
                sb.append("и не должен включать символы пробела и иные посторонние символы.\n");
                sb.append("Укажите другой логин, состоящий только из латинских букв и цифр!");

                alert.setHeaderText(sb.toString());

                alert.showAndWait();
                return;
            }

            AllUsers.getOneUser(userID).setNameLogin(loginTextArea.getText().trim());
            textAreas.put(loginTextArea, loginTextArea.getText().trim());
        }

        if (passwordField.getText() != null && !passwordField.getText().trim().isEmpty()) {
            boolean hasOtherSymbols = false;
            char[] newPassArray = passwordField.getText().trim().toCharArray();
            for (Character ch : newPassArray) {
                if (!latinForPass.contains(ch.toString())) {
                    hasOtherSymbols = true;
                    break;
                }
            }

            if (passwordField.getText().trim().length() < 12 || hasOtherSymbols) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Неправильный пароль");

                StringBuilder header = new StringBuilder("Указанный вами пароль\nдля пользователя id-");
                header.append(userID).append(" ").append(AllUsers.getOneUser(userID).getFullName()).append("\n");
                header.append("не соответствует правилам создания паролей!\n");

                alert.setHeaderText(header.toString());

                StringBuilder sb = new StringBuilder("Пароль должен состоять не менее, чем из 12 символов ");
                sb.append("и не должен включать символы пробела и иные посторонние символы.\n");
                sb.append("Укажите другой пароль, состоящий только из латинских букв и цифр, ");
                sb.append("либо очистите поле пароля, чтобы оставить пароль прежним.");

                alert.setContentText(sb.toString());

                alert.showAndWait();
                return;
            }

            SecurePassword sp = SecurePassword.getInstance(passwordField.getText().trim());
            AllUsers.getUsersPass().put(userID, sp);
            passwordField.setText(null);
        }


        if (!textAreas.get(fullNameTextArea).equalsIgnoreCase(fullNameTextArea.getText())) {

            if (fullNameTextArea.getText() == null || fullNameTextArea.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Неправильное имя");
                StringBuilder sb = new StringBuilder("Указанное вами новое имя\n");
                sb.append("для пользователя id-").append(userID);
                sb.append(" ").append(AllUsers.getOneUser(userID).getFullName()).append("\n");
                sb.append("не соответствует правилам создания имен.\n");
                sb.append("Имя должно состоять не менее, чем из 1 символа\n");
                sb.append("Укажите другое имя, состоящее более, чем из 1 символа!");

                alert.setHeaderText(sb.toString());

                alert.showAndWait();
                return;
            }

            if (AllUsers.isFullNameExist(fullNameTextArea.getText().trim())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Дублирующееся имя");
                StringBuilder sb = new StringBuilder("Указанное вами новое имя \"");
                sb.append(fullNameTextArea.getText()).append("\"\nдля пользователя id-").append(userID);
                sb.append(" ").append(AllUsers.getOneUser(userID).getFullName()).append("\n");
                sb.append("совпадает с именем другого пользователя.\n");
                sb.append("Укажите другое имя!");

                alert.setHeaderText(sb.toString());

                alert.showAndWait();
                return;
            }

            if (AllUsers.isUsersLoggedContainsUser(AllUsers.getOneUser(userID).getFullName())) {
                AllUsers.deleteLoggedUser(AllUsers.getOneUser(userID).getFullName());
                AllUsers.addLoggedUser(fullNameTextArea.getText().trim());
            }
            AllUsers.getOneUser(userID).setFullName(fullNameTextArea.getText().trim());
            textAreas.put(fullNameTextArea, fullNameTextArea.getText());


        }


        if ((textAreas.get(emailTextField) == null && emailTextField.getText() != null) ||
                (textAreas.get(emailTextField) != null && !textAreas.get(emailTextField).equalsIgnoreCase(emailTextField.getText()))) {

            if (emailTextField.getText() == null || emailTextField.getText().trim().isEmpty()) {
                AllUsers.getOneUser(userID).setEmail("");
            }
            else {
                boolean hasOtherSymbols = false;
                char[] newEmailArray = emailTextField.getText().trim().toCharArray();
                for (Character ch : newEmailArray) {
                    if (!latinForPass.contains(ch.toString()) && !ch.toString().equalsIgnoreCase("@")) {
                        hasOtherSymbols = true;
                        break;
                    }
                }

                if (!emailTextField.getText().trim().contains("@") || hasOtherSymbols || emailTextField.getText().trim().length() < 3) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Неправильный е-мейл");
                    StringBuilder sb = new StringBuilder("Указанный вами новый е-мейл\n");
                    sb.append("для пользователя id-").append(userID);
                    sb.append(" ").append(AllUsers.getOneUser(userID).getFullName()).append("\n");
                    sb.append("не соответствует правилам создания е-мейлов.\n");
                    sb.append("Е-мейл должен состоять из нескольких символов,\n");
                    sb.append("одним из которых должен быть \"@\"\n");
                    sb.append("Укажите другой е-мейл, состоящий из правильных символов!");

                    alert.setHeaderText(sb.toString());

                    alert.showAndWait();
                    return;
                }

                AllUsers.getOneUser(userID).setEmail(emailTextField.getText());
            }
            textAreas.put(emailTextField, emailTextField.getText());
        }



        if (!textAreas.get(hourPayTextField).equalsIgnoreCase(hourPayTextField.getText())) {

            if (hourPayTextField.getText() == null || hourPayTextField.getText().trim().isEmpty()) {
                AllUsers.getOneUser(userID).setWorkHourValue(0);
                hourPayTextField.setText("");
                textAreas.put(hourPayTextField, "");
                return;
            }

            Integer hourPay = null;
            try {
                hourPay = Integer.parseInt(hourPayTextField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Неправильная сумма");
                StringBuilder sb = new StringBuilder("Указанный вами новый размер часовой оплаты\n");
                sb.append("для пользователя id-").append(userID);
                sb.append(" ").append(AllUsers.getOneUser(userID).getFullName()).append("\nне является корректным числом\n");
                sb.append("Укажите правильную сумму!");

                alert.setHeaderText(sb.toString());

                alert.showAndWait();
                return;
            }

            if (hourPay != null) {
                AllUsers.getOneUser(userID).setWorkHourValue(hourPay);
                hourPayTextField.setText(AllData.formatInputInteger(hourPay));
                textAreas.put(hourPayTextField, AllData.formatInputInteger(hourPay));
            }
        }

        isChanged = false;
        initSaveButtons();
        AllData.staffWindowController.initializeTable();
        AllData.tableProjectsManagerController.initialize();
    }




    public void handleSaveAndCloseButton() {
        handleSaveButton();
        if (!isChanged) {
            myStage.close();
        }
    }


    public void handleCloseButton() {
        if (!isChanged) {
            myStage.close();
        }
        else {
            handleAlerts();
        }
    }


    private void handleAlerts() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение закрытия");
        alert.setHeaderText("В текст полей пользователя id-" + userID + " " +
                AllUsers.getOneUser(userID).getFullName() +
                "\nбыли внесены изменения.\nСохранить их или проигнорировать?");

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
            alert.close();
            myStage.close();
        }
        else {
            alert.close();
            handleSaveAndCloseButton();
        }
    }



    public void listenChanges() {

        if (myStage != null) {
            myStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    handleCloseButton();
                    event.consume();
                }
            });
        }

        isChanged = false;

        if (textAreas.get(emailTextField) == null && emailTextField.getText() != null) {
            isChanged = true;
            initSaveButtons();
            return;
        }

        if (!textAreas.get(loginTextArea).equalsIgnoreCase(loginTextArea.getText()) ||
                (passwordField.getText() != null && !passwordField.getText().isEmpty()) ||
                !textAreas.get(fullNameTextArea).equalsIgnoreCase(fullNameTextArea.getText()) ||
                (textAreas.get(emailTextField) != null && !textAreas.get(emailTextField).equalsIgnoreCase(emailTextField.getText())) ||
                !textAreas.get(hourPayTextField).equalsIgnoreCase(hourPayTextField.getText())) {
            isChanged = true;
        }
        initSaveButtons();
    }


    public void close() {
        myStage.close();
    }
}
