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
    private Stage myStage;


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


    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    @FXML
    public void initialize() {
        initRoleChoiceBox();
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

    public void handleCancelButton() {
        myStage.close();
    }

    public void handleCreateUserButton() {

        if (!isTextEmpty()) {
            String login = loginTextField.getText();
            String pass = passTextField.getText();
            Role role = roleChoiceBox.getValue();

            if (AllUsers.isNameLoginExist(login)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Логин недопустим");
                alert.setHeaderText("Такой логин уже существует\nВведите другой логин.");
                alert.showAndWait();
                return;
            }

            User user = AllUsers.createUser(login, pass, role);

            System.out.println(user.getNameLogin());
            System.out.println(user.getIDNumber());
            System.out.println(user.getRole());
            System.out.println(user.getFullName());

            if (user == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка создания пользователя");
                alert.setHeaderText("Не удалось создать пользователя.\nОбратитесь к Анисимову");
                alert.showAndWait();
                return;
            }

            if (fullNameTextField.getText() != null || !fullNameTextField.getText().isEmpty()) {
                user.setFullName(fullNameTextField.getText());
            }

            AllData.staffWindowController.initializeTable();
            myStage.close();
        }
    }

    private boolean isTextEmpty() {
        if (loginTextField.getText() == null || loginTextField.getText().isEmpty()) {
            return true;
        }
        if (passTextField.getText() == null || passTextField.getText().isEmpty()) {
            return true;
        }
        return false;
    }

}
