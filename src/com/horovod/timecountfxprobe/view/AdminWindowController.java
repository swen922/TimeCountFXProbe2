package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.threads.ThreadSendBaseToServer;
import com.horovod.timecountfxprobe.threads.ThreadSetProjectID;
import com.horovod.timecountfxprobe.threads.ThreadSetServerProjectID;
import com.horovod.timecountfxprobe.threads.ThreadSetServerUserID;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
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


    // Server fields

    @FXML
    private Label userListSizeLabel;
    @FXML
    private Label projectListSizeLabel;
    @FXML
    private TextField currentIDprojectsTextField;
    @FXML
    private Button setCurrentIDProjectsButton;
    @FXML
    private TextField currentIDUsersTextField;
    @FXML
    private Button setCurrentIDUsersButton;
    @FXML
    private Button sendBaseToServerButton;
    @FXML
    private Button getBaseFromServerButton;
    @FXML
    private Button loaderSaveOnServerButton;
    @FXML
    private Button loaderLoadOnServerButton;
    @FXML
    private Button setHomeFolderOnServerButton;
    @FXML
    private TextField homeFolderOnServerTextField;
    @FXML
    private Button setHTTPAddressServerButton;
    @FXML
    private TextField HTTPAddressServerTextField;


    // Client fields

    @FXML
    private Label taskQueueSizeLabel;
    @FXML
    private Label waitingTaskSizeLabel;
    @FXML
    private Button uploadBaseToClientButton;
    @FXML
    private Button getBaseFromClientButton;
    @FXML
    private Button loaderSaveOnClientButton;
    @FXML
    private Button loaderLoadOnClientButton;
    @FXML
    private Button startGlobalUpdateFromServerButton;
    @FXML
    private Button checkWaitingTasksButton;
    @FXML
    private Button importFromSQLButton;
    @FXML
    private Button saveSQLToXMLButton;
    @FXML
    private CheckBox usersCheckBox;
    @FXML
    private CheckBox projectsCheckBox;
    @FXML
    private CheckBox timeCheckBox;
    @FXML
    private Button setHomeFolderButton;
    @FXML
    private TextField homeFolderTextField;
    @FXML
    private Button createUserButton;
    @FXML
    private Button createProjectButton;


    // Window fields

    @FXML
    private Label statusLabel;
    @FXML
    private Button clearLoggedUsersButton;
    @FXML
    private ChoiceBox<String> usersLoggedChoiceBox;
    @FXML
    private Button ExitNoSaveButton;
    @FXML
    private Button ExitButton;


    @FXML
    private void initialize() {

        AllData.resetStatus();

        initStatistcTextFields();
        initProjectIDTextField();
        initUserIDTextField();
        initHomeFolderOnServer();
        initHTTPAddressServer();
        initHomeFolder();
        initLoggedUsersChoiceBox();
        initClosing();
    }

    public void updateAdminWindow() {
        initStatistcTextFields();
        initProjectIDTextField();
        initUserIDTextField();
        initHomeFolderOnServer();
        initHTTPAddressServer();
        initHomeFolder();
        initLoggedUsersChoiceBox();
        initClosing();
    }

    private void initStatistcTextFields() {
        this.userListSizeLabel.setText(String.valueOf(AllUsers.getUsers().size()));
        this.projectListSizeLabel.setText(String.valueOf(AllData.getAllProjects().size()));
        this.taskQueueSizeLabel.setText(String.valueOf(AllData.tasksQueue.size()));
        this.waitingTaskSizeLabel.setText(String.valueOf(AllData.waitingTasks.size()));
    }

    private void initProjectIDTextField() {
        this.currentIDprojectsTextField.setText(String.valueOf(AllData.createProjectID.get()));
    }

    private void initUserIDTextField() {
        this.currentIDUsersTextField.setText(String.valueOf(AllUsers.createUserID.get()));
    }

    private void initHomeFolderOnServer() {
        this.homeFolderOnServerTextField.setText(AllData.pathToHomeFolderOnServer);
    }

    private void initHTTPAddressServer() {
        this.HTTPAddressServerTextField.setText(AllData.httpAddress);
    }

    private void initHomeFolder() {
        this.homeFolderTextField.setText(AllData.pathToHomeFolder);
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

    public void handleSetCurrentIDProjectsButton() {
        ThreadSetServerProjectID threadSetServerProjectID = new ThreadSetServerProjectID();
        threadSetServerProjectID.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                initProjectIDTextField();
            }
        });
    }

    public void handleSetCurrentUDUsersButton() {
        ThreadSetServerUserID threadSetServerUserID = new ThreadSetServerUserID();
        threadSetServerUserID.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                initUserIDTextField();
            }
        });
    }

    public void handleSendBaseToServerButton() {
        AllData.taskForProgressBar = new ThreadSendBaseToServer();
        AllData.mainApp.showProgressBarWindow(AllData.primaryStage);
    }



    public void handleCreateUserButton() {
        AllData.mainApp.showCreateUserWindow();
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







    public void handleLoaderSaveButton() {
        saveBase();
    }

    public void handleLoaderLoadButton() {
        loadBase();
        updateAdminWindow();
    }

    private void saveBase() {
        Loader loader = new Loader();
        boolean writed = loader.save();
        if (!writed) {
            alertSerialize();
        }
    }

    private void alertSerialize() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка записи базы");
        alert.setHeaderText("Не удалось записать базу в файл");
        alert.showAndWait();
    }

    private void loadBase() {
        Loader loader = new Loader();
        boolean loaded = loader.load();
        if (!loaded) {
            alertDeSerialize();
        }
    }

    private void alertDeSerialize() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка чтения базы");
        alert.setHeaderText("Не удалось прочитать базу из файла");
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
