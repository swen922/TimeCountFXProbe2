package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginWindowController {

    private Stage stage;


    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage newStage) {
        this.stage = newStage;
    }

    @FXML
    private Label headLine;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passField;

    @FXML
    private Button buttonOK;

    @FXML
    private Button buttonCancel;



    @FXML
    private void initialize() {

        loginField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loginField.clear();
            }
        });

        passField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                passField.clear();
            }
        });

    }


    public void handlOK() {

        while (true) {
            String login = loginField.getText();
            String pass = passField.getText();

            if (login == null || login.isEmpty()) {
                loginField.setText("Введите логин и пароль!");
                passField.setText("");
                break;
            }
            if (pass == null || pass.isEmpty()) {
                loginField.setText("Введите логин и пароль!");
                passField.setText("");
                break;
            }

            User user = AllUsers.getOneUser(login);
            if (user == null) {
                loginField.setText("Неправильно! Введите еще раз логин и пароль!");
                passField.setText("");
                break;
            }
            boolean result = AllUsers.isPassCorrectForUser(user.getIDNumber(), pass);
            if (!result) {
                loginField.setText("Неправильно! Введите еще раз логин и пароль!");
                passField.setText("");
                break;
            }
            else {
                AllUsers.setCurrentUser(user.getIDNumber());
                String fullName = user.getFullName();

                if (!AllUsers.isUsersLoggedContainsUser(fullName)) {
                    AllUsers.addLoggedUser(fullName);
                }

                Role role = user.getRole();
                if (role.equals(Role.DESIGNER)) {
                    this.stage.close();
                    AllData.mainApp.showTableProjectsDesigner();
                }
                else if (role.equals(Role.MANAGER)) {
                    this.stage.close();
                    AllData.mainApp.showTableProjectsManager();
                }
                else if (role.equals(Role.ADMIN)) {
                    this.stage.close();
                    AllData.mainApp.showAdminWindow();
                }
                break;
            }
        }
    }

    public void handleCancel() {
        this.stage.close();
        User us = AllUsers.getOneUser(AllUsers.getCurrentUser());
        if (us.getRole().equals(Role.DESIGNER)) {
            AllData.mainApp.showTableProjectsDesigner();
        }
        else if (us.getRole().equals(Role.MANAGER)) {
            AllData.mainApp.showTableProjectsManager();
        }
        else if (us.getRole().equals(Role.ADMIN)) {
            AllData.mainApp.showAdminWindow();
        }
    }

}
