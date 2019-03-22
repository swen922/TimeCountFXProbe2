package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.threads.*;
import com.horovod.timecountfxprobe.user.*;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.horovod.timecountfxprobe.serialize.Updater.getGlobalUpdateTaskService;

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
    private Button loaderLoadOnServerButton;
    @FXML
    private Button loaderSaveOnServerButton;


    // Client fields

    @FXML
    private Label taskQueueSizeLabel;
    @FXML
    private Label waitingTaskSizeLabel;
    @FXML
    private Button createUserButton;
    @FXML
    private Button createProjectButton;
    @FXML
    private Button loaderLoadButton;
    @FXML
    private Button loaderSaveButton;
    @FXML
    private Button globalUpdateButton;
    @FXML
    private TextField globalUpdatePeriodTextField;
    @FXML
    private Button checkWaitingTasksButton;
    @FXML
    private TextField checkWaitingPeriodTextField;
    @FXML
    private Button importFromSQLButton;
    @FXML
    private RadioButton usersRadioButton;
    @FXML
    private RadioButton projectsRadioButton;
    @FXML
    private RadioButton timeRadioButton;
    @FXML
    private CheckBox replaceProjectsCheckBox;
    @FXML
    private Button deleteWaitingTasksButton;
    @FXML
    private Button setHttpAddressButton;
    @FXML
    private TextField httpAddressTextField;



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

        initStatisticTextFields();
        initTextFields();
        initPeriods();
        initLoggedUsersChoiceBox();
        initRadioButtons();
        initClosing();

    }

    public void updateAdmin() {

        initStatisticTextFields();
        initTextFields();
    }



    private void initStatisticTextFields() {
        this.userListSizeLabel.setText(String.valueOf(AllUsers.getUsers().size()));
        this.projectListSizeLabel.setText(String.valueOf(AllData.getAllProjects().size()));
        this.taskQueueSizeLabel.setText(String.valueOf(Updater.tasksQueue.size()));
        this.waitingTaskSizeLabel.setText(String.valueOf(AllData.waitingTasks.size()));
    }
    private void initTextFields() {
        this.currentIDprojectsTextField.setText(String.valueOf(AllData.createProjectID.get()));
        this.currentIDUsersTextField.setText(String.valueOf(AllUsers.createUserID.get()));
        this.globalUpdatePeriodTextField.setText(String.valueOf(AllData.globalUpdatePeriod));
        this.checkWaitingPeriodTextField.setText(String.valueOf(AllData.checkWaitingPeriod));
        this.httpAddressTextField.setText(AllData.httpAddress);
    }

    private void initPeriods() {
        this.globalUpdatePeriodTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    String input = globalUpdatePeriodTextField.getText();
                    int global = AllData.getIntFromText(AllData.globalUpdatePeriod, input);
                    if (global > 0) {
                        AllData.globalUpdatePeriod = global;
                        globalUpdatePeriodTextField.requestFocus();

                        Updater.getGlobalUpdateTaskService().shutdown();
                        ScheduledExecutorService globalUpdateTaskService = Executors.newSingleThreadScheduledExecutor();
                        Updater.setGlobalUpdateTaskService(globalUpdateTaskService);
                        Updater.getGlobalUpdateTaskService().scheduleAtFixedRate(new ThreadGlobalUpdate(), 5, global, TimeUnit.SECONDS);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Период запуска изменен");
                        alert.setHeaderText("Период запуска глобального обновления базы успешно изменен. \nНовый период = " + global + " секунд.");
                        alert.showAndWait();
                    }
                    else {
                        globalUpdatePeriodTextField.setText(String.valueOf(AllData.globalUpdatePeriod));
                    }
                }
            }
        });

        this.checkWaitingPeriodTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    String input = checkWaitingPeriodTextField.getText();
                    int check = AllData.getIntFromText(AllData.checkWaitingPeriod, input);
                    if (check > 0) {
                        AllData.checkWaitingPeriod = check;
                        checkWaitingPeriodTextField.requestFocus();

                        Updater.getRepeatWaitingTaskService().shutdown();
                        ScheduledExecutorService repeatWaitingTaskService = Executors.newSingleThreadScheduledExecutor();
                        Updater.setRepeatWaitingTaskService(repeatWaitingTaskService);
                        Updater.getRepeatWaitingTaskService().scheduleAtFixedRate(new ThreadCheckWaitingTasks(), 5, check, TimeUnit.SECONDS);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Период запуска изменен");
                        alert.setHeaderText("Период запуска проверки неисполненных задач успешно изменен. \nНовый период = " + check + " секунд.");
                        alert.showAndWait();

                    }
                    else {
                        checkWaitingPeriodTextField.setText(String.valueOf(AllData.checkWaitingPeriod));
                    }
                }
            }
        });
    }

    private void initRadioButtons() {
        ToggleGroup importSQLGroup = new ToggleGroup();
        usersRadioButton.setToggleGroup(importSQLGroup);
        projectsRadioButton.setToggleGroup(importSQLGroup);
        timeRadioButton.setToggleGroup(importSQLGroup);
        usersRadioButton.setSelected(true);

    }

    public void initLoggedUsersChoiceBox() {

        usersLoggedChoiceBox.setItems(AllUsers.getUsersLogged());

        if (!usersLoggedChoiceBox.getItems().contains(AllData.toLoginWindow)) {
            usersLoggedChoiceBox.getItems().add(AllData.toLoginWindow);
        }

        /*System.out.println("usersLoggedChoiceBox = " + usersLoggedChoiceBox);
        System.out.println("usersLoggedChoiceBox.getItems().size() = " + usersLoggedChoiceBox.getItems().size());
        for (String val : usersLoggedChoiceBox.getItems()) {
            System.out.println("val = " + val);
        }
        System.out.println("AllUsers.getCurrentUser() = " + AllUsers.getCurrentUser());
        System.out.println("AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName() = " + AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName());*/

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
        AllData.result = false;
        setCurrentIDProjectsButton.setDisable(true);
        int num = 0;
        String numString = currentIDprojectsTextField.getText();
        if (numString != null && !numString.isEmpty()) {
            try {
                num = Integer.parseInt(numString);
            } catch (NumberFormatException e) {

            }
        }

        if (num != 0) {
            AllData.taskForProgressBar = new ThreadSetServerProjectID(num);
            AllData.taskForProgressBar.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    setCurrentIDProjectsButton.setDisable(false);
                    initTextFields();
                    if (AllData.progressBarStage.isShowing()) {
                        AllData.progressBarStage.close();
                    }

                    if (AllData.result) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Нумерация проектов изменена");
                        alert.setHeaderText("Установлен новый текущий номер в нумерации проектов = " + AllData.createProjectID.get());
                        alert.showAndWait();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Ошибка изенения нумерации проектов");
                        alert.setHeaderText("Произошла ошибка при установке \nнового текущего номера в нумерации проектов.");
                        alert.showAndWait();
                    }
                }
            });
            AllData.mainApp.showProgressBarWindow();
            Updater.update(AllData.taskForProgressBar);

        }
    }

    public void handleSetCurrentUDUsersButton() {
        AllData.result = false;
        setCurrentIDUsersButton.setDisable(true);
        int num = 0;
        String numString = currentIDUsersTextField.getText();
        if (numString != null && !numString.isEmpty()) {
            try {
                num = Integer.parseInt(numString);
            } catch (NumberFormatException e) {

            }
        }

        if (num != 0) {
            AllData.taskForProgressBar = new ThreadSetServerUserID(num);
            AllData.taskForProgressBar.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    setCurrentIDUsersButton.setDisable(false);
                    initTextFields();
                    if (AllData.progressBarStage.isShowing()) {
                        AllData.progressBarStage.close();
                    }

                    if (AllData.result) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Нумерация юзеров изменена");
                        alert.setHeaderText("Установлен новый текущий номер в нумерации юзеров = " + AllUsers.createUserID.get());
                        alert.showAndWait();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Ошибка изенения нумерации юзеров");
                        alert.setHeaderText("Произошла ошибка при установке \nнового текущего номера в нумерации юзеров.");
                        alert.showAndWait();
                    }

                }
            });
            AllData.mainApp.showProgressBarWindow();
            Updater.update(AllData.taskForProgressBar);

        }
    }

    public void handleSendBaseToServerButton() {
        AllData.result = false;
        sendBaseToServerButton.setDisable(true);

        AllData.taskForProgressBar = new ThreadSendBaseToServer();
        AllData.taskForProgressBar.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                sendBaseToServerButton.setDisable(false);
                if (AllData.progressBarStage.isShowing()) {
                    AllData.progressBarStage.close();
                }

                if (AllData.result) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("База на сервере обновлена");
                    alert.setHeaderText("Локальная база данных успешно отправлена на сервер.");
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка отправки базы на сервер");
                    alert.setHeaderText("Произошла ошибка при отправке базы на сервер.");
                    alert.showAndWait();
                }
            }
        });
        AllData.mainApp.showProgressBarWindow();
        Updater.update(AllData.taskForProgressBar);

    }


    public void handleGetBaseFromServerButton() {

        AllData.result = false;
        getBaseFromServerButton.setDisable(true);

        String download = AllData.pathToDownloads;
        String name = "base_from_server_" + AllData.formatDate(LocalDate.now()) + ".xml";

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML file", "*.xml");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(download));
        chooser.setInitialFileName(name);
        File file = chooser.showSaveDialog(AllData.primaryStage);

        if (file != null) {
            AllData.taskForProgressBar = new ThreadGetBaseFromServer(file);
            AllData.taskForProgressBar.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    getBaseFromServerButton.setDisable(false);
                    if (AllData.progressBarStage.isShowing()) {
                        AllData.progressBarStage.close();
                    }

                    if (AllData.result) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("База с сервера получена");
                        alert.setHeaderText("Серверная база данных успешно скачана с сервера.");
                        alert.setContentText("Файл находится в указанной вами папке.");
                        alert.showAndWait();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Ошибка получения базы с сервера");
                        alert.setHeaderText("Произошла ошибка при получении базы от сервера.");
                        alert.showAndWait();
                    }
                }
            });
            AllData.mainApp.showProgressBarWindow();
            Updater.update(AllData.taskForProgressBar);
        }
    }

    public void handleReadBaseOnServer() {
        AllData.result = false;
        loaderLoadOnServerButton.setDisable(true);

        AllData.taskForProgressBar = new ThreadReadBaseOnServer();
        AllData.taskForProgressBar.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                loaderLoadOnServerButton.setDisable(false);
                AllData.progressBarStage.close();

                if (AllData.result) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Сервер прочитал базу с диска");
                    alert.setHeaderText("Сервер прочитал текущую базу с диска");
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка чтения базы сервером");
                    alert.setHeaderText("Произошла ошибка при попытке чтения базы сервером.");
                    alert.showAndWait();
                }
            }
        });
        AllData.mainApp.showProgressBarWindow();
        Updater.update(AllData.taskForProgressBar);
    }


    public void handleSaveBaseOnServer() {
        AllData.result = false;
        loaderSaveOnServerButton.setDisable(true);

        AllData.taskForProgressBar = new ThreadSaveBaseOnServer();
        AllData.taskForProgressBar.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                loaderSaveOnServerButton.setDisable(false);
                AllData.progressBarStage.close();

                if (AllData.result) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Сервер записал базу на диск");
                    alert.setHeaderText("Сервер записал текущую базу на диск");
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка записи базы сервером");
                    alert.setHeaderText("Произошла ошибка при попытке записи базы сервером на диск.");
                    alert.showAndWait();
                }
            }
        });
        AllData.mainApp.showProgressBarWindow();
        Updater.update(AllData.taskForProgressBar);
    }


    public void handleCreateUserButton() {
        AllData.mainApp.showCreateUserWindow();
    }

    public void handleCreateProjectButton() {
        AllData.mainApp.showCreateProjectWindow();
    }

    public void handleLoaderLoadButton() {
        loadBase();
        initialize();
    }

    public void handleLoaderSaveButton() {
        saveBase();
    }

    public void handleGlobalUpdateButton() {
        ThreadGlobalUpdate globalUpdate = new ThreadGlobalUpdate();
        Updater.update(globalUpdate);
    }

    public void handleCheckWaitingTasksButton() {
        ThreadCheckWaitingTasks task = new ThreadCheckWaitingTasks();
        Updater.update(task);
    }

    public void handleDeleteWaitingTasksButton() {
        AllData.waitingTasks.clear();
        Loader loader = new Loader();
        loader.saveWatingTasks();
    }

    public void handleSetHttpAddressButton() {
        String address = httpAddressTextField.getText();
        if (address != null && !address.isEmpty()) {
            AllData.httpAddress = address;
            AllData.rebuildHTTPAddresses();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Интернет-адрес сервера изменен");
            alert.setHeaderText("Интернет-адрес сервера успешно изменен.");
            alert.setContentText("Изменения вступят в силу после перезапуска программы");
            alert.showAndWait();
        }

    }

    public void handleImportFromSQLButton() {

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(AllData.pathToHomeFolder));
        File file = chooser.showOpenDialog(AllData.primaryStage);

        if (file != null && file.exists()) {

            StringBuilder sb = null;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                sb = new StringBuilder("");
                while (reader.ready()) {
                    sb.append(reader.readLine()).append("\n");
                }
            } catch (IOException e) {
                AllData.logger.error("AdminWindowController.handleImportFromSQLButton - Ошибка чтения файла с диска.");
                AllData.logger.error(e.getMessage(), e);
                return;
            }
            String input = sb.toString();

            if (usersRadioButton.isSelected()) {

                /** коварная строчка удаляет текущего админа ваще пипец*/
                //AllUsers.getUsers().clear();

                input = input.replaceAll("'", "");
                input = input.replaceAll("\\(", "");
                input = input.replaceAll("\\)", "");
                input = input.replaceAll(",", "");
                String[] inputArray = input.split("\n");

                int counterUsers = 0;

                for (String line : inputArray) {
                    String[] userLine = line.split(" ");
                    String idString = userLine[0];
                    int id = Integer.parseInt(idString);
                    String login = userLine[1];
                    String pass = userLine[2];
                    SecurePassword securePassword = SecurePassword.getInstance(pass);

                    String mail = userLine[3];
                    String role = userLine[4];

                    User user = null;
                    if (role.equalsIgnoreCase("designer")) {
                        user = new Designer(id, login, securePassword);
                        user.setEmail(mail);
                        AllUsers.getUsers().put(user.getIDNumber(), user);

                    }
                    else if (role.equalsIgnoreCase("manager")) {
                        user = new Manager(id, login, securePassword);
                        user.setEmail(mail);
                        AllUsers.getUsers().put(user.getIDNumber(), user);
                    }
                    else if (role.equalsIgnoreCase("admin")) {
                        user = new Admin(id, login, securePassword);
                        user.setEmail(mail);
                        AllUsers.getUsers().put(user.getIDNumber(), user);
                    }
                    else if (role.equalsIgnoreCase("surveyor")) {
                        user = new Surveyor(id, login, securePassword);
                        user.setEmail(mail);
                        AllUsers.getUsers().put(user.getIDNumber(), user);
                    }

                    if (user != null) {
                        AllUsers.getUsers().put(user.getIDNumber(), user);
                        counterUsers++;
                    }
                    else {
                        AllData.logger.error("Ошибка парсинга строки в юзера. Строка = " + line);
                    }
                }

                if (counterUsers > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Импорт пользователей проведен успешно");
                    alert.setHeaderText("Импорт пользователей проведен успешно. \nВсего импортировано " + counterUsers + " юзеров");
                    alert.showAndWait();
                }

            }
            else if (projectsRadioButton.isSelected()) {

                if (replaceProjectsCheckBox.isSelected()) {
                    AllData.getAllProjects().clear();
                    AllData.getActiveProjects().clear();
                }

                String[] inputArray = input.split("\n");
                int counterProjects = 0;

                for (String line : inputArray) {

                    String inputLine = new String(line);

                    int id = 0;
                    String compAndManager = "";
                    String company = "";
                    String manager = "";
                    String description = "";
                    String archiveString = "";
                    LocalDate date = null;

                    //Project project = null;

                    /** Первый паттерн – для поиска номера id*/
                    Pattern p = Pattern.compile("\\(\\d+,");
                    Matcher m = p.matcher(inputLine);

                    String idString = "";
                    if (m.find()) {
                        // Найден id-номер
                        idString = m.group();

                        // Урезаем строку
                        int i = inputLine.indexOf(idString, 0);
                        int ii = i + idString.length();
                        inputLine = inputLine.substring(ii, inputLine.length()).replaceAll("'\\),", "',").trim();

                        // Найден id-номер
                        idString = idString.replaceAll("\\(", "");
                        idString = idString.replaceAll(",", "");

                        try {
                            id = Integer.parseInt(idString);
                        } catch (NumberFormatException e) {
                            AllData.logger.error("AdminWindowController.handleImportFromSQLButton - Ошибка парсинга id-номера.");
                            AllData.logger.error(e.getMessage(), e);
                        }
                    }


                    /** Второй паттерн – для поиска текста в одинарных скобках ' */
                    Pattern p2 = Pattern.compile("('.+?',)");
                    Matcher m2 = p2.matcher(inputLine);


                    if (m2.find()) {
                        // Найдена компания и менеджер
                        compAndManager = m2.group();

                        // Урезаем строку
                        int i = inputLine.indexOf(compAndManager, 0);
                        int ii = i + compAndManager.length() + 1;
                        inputLine = inputLine.substring(ii, inputLine.length()).replaceAll("'\\),", "',").trim();


                        // Чистим компанию и менеджера
                        compAndManager = compAndManager.replaceAll("',", "");
                        compAndManager = compAndManager.replaceAll("'", "");

                        // Разделяем компанию и менеджера
                        String[] cm = compAndManager.split(" +- +");
                        if (cm.length == 2) {
                            company = cm[0];
                            manager = cm[1];
                        }
                        else if (cm.length == 1) {
                            company = cm[0];
                        }

                        if (company == null || company.isEmpty()) {
                            company = "someCompany";
                        }
                        if (manager == null || manager.isEmpty()) {
                            manager = "someManager";
                        }
                    }


                    if (m2.find()) {
                        // Найдено описание проекта
                        description = m2.group();

                        // Урезаем строку
                        int i = inputLine.indexOf(description, 0);
                        int ii = i + description.length() + 1;
                        inputLine = inputLine.substring(ii, inputLine.length()).trim();

                        // Чистим описание проекта
                        description = description.replaceAll("',", "");
                        description = description.replaceAll("'", "");

                        // Найден булин архивный ли проект
                        archiveString = inputLine.substring(0, 1);
                    }



                    /** Третий паттерн – для поиска даты в описании проекта */
                    Pattern p3 = Pattern.compile("\\d+\\.\\d+\\.\\d+");
                    Matcher m3 = p3.matcher(description);

                    // Найдена дата создания проекта
                    String dateStringInput = "";
                    if (m3.find()) {
                        dateStringInput = m3.group();
                        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy");
                        try {
                            date = DATE_FORMATTER.parse(dateStringInput, LocalDate::from);
                        } catch (Exception e) {
                            AllData.logger.error("AdminWindowController.handleImportFromSQLButton - Ошибка парсинга даты.");
                            AllData.logger.error(e.getMessage(), e);
                        }
                    }

                    /*int id = 0;
                    String compAndManager = "";
                    String company = "";
                    String manager = "";
                    String description = "";
                    String archiveString = "";
                    LocalDate date = null;*/

                    Project project = null;
                    if (id != 0 && !compAndManager.isEmpty() && !description.isEmpty() && !archiveString.isEmpty()) {

                        project = AllData.createProject(id, company, manager, description, date);

                        if (date == null) {
                            AllData.logger.error("У проекта id-" + project.getIdNumber() + " не удалось распознать дату создания проекта");
                        }
                    }
                    else {
                        AllData.logger.error("Ошибка парсинга строки Один или несколько компонентов не распознаны. Строка = " + line);
                    }

                    if (project != null) {
                        //AllData.getAllProjects().put(project.getIdNumber(), project);
                        counterProjects++;

                        if (archiveString.equals("1")) {
                            project.setArchive(true);
                        }

                    }
                    else {
                        AllData.logger.error("Ошибка создания проекта. Строка = " + line);
                    }

                }

                if (counterProjects > 0) {

                    AllData.rebuildActiveProjects();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Импорт проектов проведен успешно");
                    alert.setHeaderText("Импорт проектов проведен успешно. \nВсего импортировано " + counterProjects + " проектов");
                    alert.showAndWait();
                }
                AllData.rebuildActiveProjects();
            }
            else if (timeRadioButton.isSelected()) {

                String[] inputArray = input.split("\n");
                int counterTime = 0;

                for (String line : inputArray) {
                    if (line != null && !line.isEmpty()) {

                        String inputLine = new String(line);

                        inputLine = inputLine.replaceAll("\\(", "");
                        inputLine = inputLine.replaceAll("\\)", "");
                        inputLine = inputLine.replaceAll("'", "");
                        inputLine = inputLine.replaceAll(",", "");
                        inputLine = inputLine.replaceAll(";", "");



                        String[] array = inputLine.split(" ");

                        int userID = 0;
                        int projectID = 0;
                        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy");
                        LocalDate date = null;
                        Double timeD = null;

                        try {
                            userID = Integer.parseInt(array[1]);
                        } catch (NumberFormatException e) {

                        }


                        try {
                            projectID = Integer.parseInt(array[2]);
                        } catch (NumberFormatException e) {

                        }

                        try {
                            date = DATE_FORMATTER.parse(array[3], LocalDate::from);
                        } catch (Exception e) {
                        }


                        try {
                            timeD = Double.parseDouble(array[4]);
                        } catch (NumberFormatException e) {

                        }



                        if (userID != 0 && projectID != 0 && timeD != null) {
                            //AllData.addWorkTimeForImport(projectID, date, userID, timeD);

                            Project pr = AllData.getAnyProject(projectID);
                            if (pr != null) {
                                if (date == null) {
                                    date = LocalDate.now();
                                    String error = "Не удалось восстановить дату из строчки для проекта id-" + projectID + " Строка = " + inputLine;
                                    AllData.updateAllStatus(error);
                                    AllData.logger.error(error);
                                }

                                Integer res = pr.addWorkTime(date, userID, timeD);

                                if (res == null) {
                                    String error = "Ошибка добавления времени: Project.addWorkTime() вернул NULL. Строка = " + inputLine;
                                    AllData.updateAllStatus(error);
                                    AllData.logger.error(error);
                                }
                                else {
                                    counterTime++;
                                }
                            }
                            else {
                                String error = "Ошибка добавления времени: Полученный проект равен NULL. Строка = " + inputLine;
                                AllData.updateAllStatus(error);
                                AllData.logger.error(error);
                            }
                        }
                        else {
                            String error = "Не удалось прочитать строчку: " + inputLine + "   |   projectID = " + projectID +  ", userID = " + userID + ", timeD = " + timeD + ", date = " + date;
                            AllData.updateAllStatus(error);
                            AllData.logger.error(error);
                        }
                    }
                }
                if (counterTime > 0) {

                    LocalDate today = LocalDate.now();
                    AllData.rebuildTodayWorkSumProperty();
                    AllData.rebuildWeekWorkSumProperty(today.getYear(), today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                    AllData.rebuildMonthWorkSumProperty(today.getYear(), today.getMonthValue());
                    AllData.rebuildYearWorkSumProperty(today.getYear());

                    AllData.rebuildActiveProjects();

                    AllData.rebuildWorkSum();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Импорт времени проведен успешно");
                    alert.setHeaderText("Импорт рабочего времени проведен успешно. \nВсего импортировано " + counterTime + " объектов WorkTime");
                    alert.showAndWait();
                }

            }

        }

        AllData.updateAllWindows();
    }


    private void saveBase() {
        Loader loader = new Loader();
        boolean writed = loader.save();
        if (writed) {
            serializeOK();
        }
        else {
            alertSerialize();
        }
    }

    private void serializeOK() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("База успешно записана");
        alert.setHeaderText("База успешно записана в файл");
        alert.showAndWait();
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
        if (loaded) {
            deSerializeOK();
        }
        else {
            alertDeSerialize();
        }
    }

    private void deSerializeOK() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("База успешно прочитана");
        alert.setHeaderText("База успешно прочитана из файла");
        alert.showAndWait();
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
