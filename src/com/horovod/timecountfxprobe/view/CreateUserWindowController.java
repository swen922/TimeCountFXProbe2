package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class CreateUserWindowController {

    String latinForPass = "1234567890abcdefghijklmnopqrstuvwxyz@.-_";

    @FXML
    private TextField loginTextField;

    @FXML
    private Button clearLoginButton;

    @FXML
    private TextField passTextField;

    @FXML
    private Button clearPassButton;

    @FXML
    private TextField fullNameTextField;

    @FXML
    private Button clearFullNameButton;

    @FXML
    private ChoiceBox<Role> roleChoiceBox;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;


    @FXML
    public void initialize() {
        initRoleChoiceBox();
        initButtons();
        handleButtons();
    }


    private void initRoleChoiceBox() {
        roleChoiceBox.getItems().addAll(Role.DESIGNER, Role.MANAGER, Role.ADMIN, Role.SURVEYOR);
        roleChoiceBox.setValue(Role.DESIGNER);
        roleChoiceBox.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role object) {
                return object.getTextRole();
            }

            @Override
            public Role fromString(String string) {
                return null;
            }
        });
    }

    private void initButtons() {

        cancelButton.setCancelButton(true);
        createButton.setDefaultButton(true);
    }

    public void handleButtons() {
        if (loginTextField.getText() == null || loginTextField.getText().isEmpty()) {
            createButton.setDisable(true);
            return;
        }
        if (passTextField.getText() == null || passTextField.getText().isEmpty()) {
            createButton.setDisable(true);
            return;
        }
        createButton.setDisable(false);
    }

    public void handleCancelButton() {
        AllData.createUserWindow.close();
    }

    public void handleCreateUserButton() {

        String login = loginTextField.getText().trim();
        loginTextField.setText(login);
        String pass = passTextField.getText().trim();
        passTextField.setText(pass);
        Role role = roleChoiceBox.getValue();

        if (login.length() < 1 || hasOtherSymbols(login)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Неправильный логин");
            StringBuilder sb = new StringBuilder("Указанный вами логин не соответствует\n");
            sb.append("правилам создания логинов.");
            alert.setHeaderText(sb.toString());

            StringBuilder text = new StringBuilder("Логин должен состоять не менее, чем из 1 латинского символа. ");
            text.append("и не должен включать символы пробела и иные посторонние символы.\n");
            text.append("Укажите другой логин, состоящий только из латинских букв, цифр, дефисов, подчеркиваний");

            alert.setHeaderText(sb.toString());

            alert.showAndWait();
            return;
        }


        if (AllUsers.isNameLoginExist(login)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Логин недопустим");
            alert.setHeaderText("Такой логин уже существует.\nВведите другой логин!");
            alert.showAndWait();
            return;
        }

        if (pass.length() < 12 || hasOtherSymbols(pass)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Неправильный пароль");
            alert.setHeaderText("Указанный вами пароль не соответствует\nправилам создания паролей.");

            StringBuilder sb = new StringBuilder("Пароль должен состоять не менее, чем из 12 символов\n");
            sb.append("и не должен включать символы пробела и иные посторонние символы.\n");
            sb.append("Укажите другой пароль, состоящий только из латинских букв и цифр.");

            alert.setContentText(sb.toString());

            alert.showAndWait();
            return;
        }

        if (fullNameTextField.getText() != null && !fullNameTextField.getText().isEmpty()) {
            String fullName = fullNameTextField.getText().trim();
            if (AllUsers.isFullNameExist(fullName)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Дублирующееся имя");
                StringBuilder sb = new StringBuilder("Указанное вами имя\n");
                sb.append(fullName).append("\nсовпадает с именем другого пользователя.\n");
                sb.append("Укажите другое имя!");

                alert.setHeaderText(sb.toString());

                alert.showAndWait();
                return;
            }
        }

        User user = AllUsers.createUser(login, pass, role);

        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка создания пользователя");
            alert.setHeaderText("Не удалось создать пользователя.\nОбратитесь к Анисимову");
            alert.showAndWait();
            return;
        }

        if (fullNameTextField.getText() != null && !fullNameTextField.getText().isEmpty()) {
            user.setFullName(fullNameTextField.getText());
        }

        AllData.staffWindowController.initializeTable();

        AllData.createUserWindow.close();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Создан пользователь id-" + user.getIDNumber());
        alert.setHeaderText("Создан пользователь id-" + user.getIDNumber() + "\nлогин = " + user.getNameLogin() + "\nимя = " + user.getFullName());
        alert.show();
    }

    private boolean hasOtherSymbols(String input) {
        boolean result = false;
        char[] newLoginArray = input.toLowerCase().toCharArray();
        for (Character ch : newLoginArray) {
            if (!latinForPass.contains(ch.toString())) {
                result = true;
                break;
            }
        }
        return result;
    }


    public void handleClearLoginButton() {
        loginTextField.clear();
    }

    public void handleClearPassButton() {
        loginTextField.clear();
    }

    public void handleClearFullNameButton() {
        loginTextField.clear();
    }

}