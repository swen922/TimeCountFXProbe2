package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private Button loaderSave;
    @FXML
    private Button loaderLoad;
    @FXML
    private Button clearLoggedUsers;


    @FXML
    private Button setHomeFolderButton;

    @FXML
    private TextField homeFolderTextField;

    @FXML
    private Label statusLabel;

    @FXML
    private ChoiceBox<String> usersLoggedChoiceBox;

    @FXML
    public void initialize() {
        AllData.resetStatus();
        currentIDprojectsTextField.setText(String.valueOf(AllData.getIdNumber()));
        currentIDUsersTextField.setText(String.valueOf(AllUsers.getIDCounterAllUsers()));
        initLoggedUsersChoiceBox();
        initPathToHomeFolder();
        initClosing();
    }

    private void initPathToHomeFolder() {
        homeFolderTextField.setText(AllData.pathToHomeFolder);
    }

    public void handleSetHomeFolderButton() {
        String oldHome = homeFolderTextField.getText();
        String newHome = "";
        FileChooser chooser = new FileChooser();
        //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
        //chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(oldHome));
        File file = chooser.showSaveDialog(AllData.primaryStage);

        if (file != null) {
            newHome = file.getParent();
        }
        if (!newHome.isEmpty()) {
            homeFolderTextField.setText(newHome);
            AllData.pathToHomeFolder = newHome;
        }

        /*if (homeFolderTextField.getText() != null && !homeFolderTextField.getText().isEmpty()) {
            String newHome = homeFolderTextField.getText();
            File file = null;
            try {
                Files.createDirectories(Paths.get(newHome));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file != null) {
                AllData.pathToHomeFolder = newHome;
            }
        }*/
    }

    private synchronized void initClosing() {

        if (AllData.primaryStage != null) {

            AllData.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    saveBase();
                    Platform.exit();
                    System.exit(0);
                }
            });
        }
    }


    public void initLoggedUsersChoiceBox() {

        usersLoggedChoiceBox.setItems(AllUsers.getUsersLogged());

        if (!usersLoggedChoiceBox.getItems().contains(AllData.toLoginWindow)) {
            usersLoggedChoiceBox.getItems().add(AllData.toLoginWindow);
        }

        usersLoggedChoiceBox.setValue(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName());

        usersLoggedChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String selectUser = usersLoggedChoiceBox.getValue();

                if (selectUser != null && !selectUser.isEmpty()) {
                    if (selectUser.equalsIgnoreCase(AllData.toLoginWindow)) {
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


    public void handleLoaderSaveButton() {
        saveBase();
    }

    public void handleLoaderLoadButton() {
        loadBase();
        initialize();
    }

    private void saveBase() {
        Loader loader = new Loader();
        try {
            loader.save();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.status = "Не удалось записать базу в файл: IOException";
            updateStatus();
            alertSerialize(e.toString());
            AllData.logger.error(e.getMessage(), e);
        } catch (JAXBException e) {
            e.printStackTrace();
            System.out.println("");
            System.out.println(e.getMessage());
            System.out.println();
            System.out.println(e.getCause());
            AllData.status = "Ошибка сериализации в XML: JAXBException";
            updateStatus();
            alertSerialize(e.toString());
            AllData.logger.error(e.getMessage(), e);

        }
    }

    private void alertSerialize(String exceptionName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка записи в файл");
        alert.setHeaderText("Не удалось записать базу в файл: " + exceptionName);
        alert.showAndWait();
    }

    private void loadBase() {

        Loader loader = new Loader();
        try {
            loader.load();
        } catch (JAXBException e) {
            e.printStackTrace();
            AllData.status = "Ошибка чтения XML: " + e.toString();
            updateStatus();
            alertDeSerialize(e.toString());
            AllData.logger.error(e.getMessage(), e);

        }
    }

    private void alertDeSerialize(String exceptionName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка чтения файла");
        alert.setHeaderText("Не удалось прочесть базу из файла: " + exceptionName);
        alert.showAndWait();
    }

    public void updateStatus() {
        statusLabel.setText("Статус: " + AllData.status);
    }


    public void handleClearLoggedUsersButton() {
        AllData.mainApp.showDeleteLoggedUserWindow();
    }


    public void handleExitButton() {
        saveBase();
        Platform.exit();
        System.exit(0);
    }

}
