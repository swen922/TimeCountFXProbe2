package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class AdminWindowController {

    @FXML
    private TextField currentIDprojectsTextField;
    @FXML
    private Button setCurrentIDProjectsButton;
    @FXML
    private TextField currentIDUsersTextField;
    @FXML
    private Button setCurrentIDUsersButton;
    @FXML
    private Button saveProjectsListButton;
    @FXML
    private Button loadProjectsListButton;
    @FXML
    private Button saveUsersListButton;
    @FXML
    private Button loadUsersListButton;

    @FXML
    private ChoiceBox<String> usersLoggedChoiceBox;

    @FXML
    public void initialize() {
        currentIDprojectsTextField.setText(String.valueOf(AllData.getIdNumber()));
        currentIDUsersTextField.setText(String.valueOf(AllUsers.getIDCounterAllUsers()));
        initLoggedUsersChoiceBox();
    }

    public void handleExitButton() {
        Platform.exit();
        System.exit(0);
    }

    public void initLoggedUsersChoiceBox() {

        String toLoginWindow = "Выйти в окно логина";

        usersLoggedChoiceBox.setItems(AllUsers.getUsersLogged());

        if (!usersLoggedChoiceBox.getItems().contains(toLoginWindow)) {
            usersLoggedChoiceBox.getItems().add(toLoginWindow);
        }

        usersLoggedChoiceBox.setValue(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName());

        usersLoggedChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String selectUser = usersLoggedChoiceBox.getValue();

                if (selectUser != null && !selectUser.isEmpty()) {
                    if (selectUser.equalsIgnoreCase(toLoginWindow)) {
                        AllData.rootLayout.setCenter(null);
                        AllData.mainApp.showLoginWindow();
                    }
                    else if (!selectUser.equalsIgnoreCase(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName())) {
                        User user = AllUsers.getOneUserForFullName(selectUser);

                        Role role = user.getRole();

                        if (role.equals(Role.DESIGNER)) {
                            AllData.rootLayout.setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            AllData.mainApp.showTableProjectsDesigner();
                        }
                        else if (role.equals(Role.MANAGER)) {
                            AllData.rootLayout.setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            AllData.mainApp.showTableProjectsManager();
                        }
                        else if (role.equals(Role.ADMIN)) {
                            AllData.rootLayout.setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            AllData.mainApp.showAdminWindow();
                        }
                    }
                }
            }
        });
    }

}
