package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.threads.ThreadIncrementAndSetProjectID;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;


public class CreateProjectWindowController {


    // TODO разбить на 2 этапа создание проекта – сначала получение нового id-номера у сервера

    private String existingDescription = "";

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField createIDTextField;

    @FXML
    private Button getProjectIDButton;

    @FXML
    private TextField existingProjectTextField;

    @FXML
    private Button clearExistingProjectButton;

    @FXML
    private TextField companyTextField;

    @FXML
    private TextArea managerTextArea;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;


    @FXML
    public void initialize() {
        initButtons();
        initTextFields();
        checkButtons();
        createIDTextField.textProperty().bind(AllData.createProjectID.asString());
        createIDTextField.setDisable(true);
        getProjectIDButton.setDisable(false);

    }


    private void initTextFields() {

        existingProjectTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.TAB) {
                    companyTextField.requestFocus();
                }
            }
        });

        existingProjectTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                existingProjectTextField.selectAll();
            }
        });

        companyTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.TAB) {
                    managerTextArea.requestFocus();
                }
            }
        });

        companyTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                companyTextField.selectAll();
            }
        });

        managerTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.TAB) {
                    descriptionTextArea.requestFocus();
                }
            }
        });

        managerTextArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                managerTextArea.selectAll();
            }
        });

        descriptionTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.TAB) {
                    createButton.requestFocus();
                }
            }
        });

        descriptionTextArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                descriptionTextArea.selectAll();
            }
        });

    }


    public void createProjectID() {

        getProjectIDButton.setDisable(true);

        /*AllData.createProjectID.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                createIDTextField.setDisable(false);
            }
        });*/

        ProgressIndicator progressIndicator = new ProgressIndicator();
        ThreadIncrementAndSetProjectID threadIncrementAndSetProjectID = new ThreadIncrementAndSetProjectID();

        threadIncrementAndSetProjectID.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                progressIndicator.progressProperty().unbind();
                anchorPane.getChildren().remove(progressIndicator);
                createIDTextField.setDisable(false);

            }
        });

        progressIndicator.setPrefSize(30, 30);
        anchorPane.getChildren().add(progressIndicator);
        progressIndicator.setLayoutX(470);
        progressIndicator.setLayoutY(15);
        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(threadIncrementAndSetProjectID.progressProperty());

        Updater.update(threadIncrementAndSetProjectID);

    }


    public void handleExistingProjectTextField() {
        existingDescription = "";
        String input = existingProjectTextField.getText();
        if (input != null && !input.isEmpty()) {
            Integer projectID = null;
            try {
                projectID = Integer.parseInt(input);
            } catch (NumberFormatException e) {
            }
            if (projectID != null) {
                if (AllData.isProjectExist(projectID)) {
                    Project existingProject = AllData.getAnyProject(projectID);
                    companyTextField.setText(existingProject.getCompany());
                    managerTextArea.setText(existingProject.getManager());
                    descriptionTextArea.setText(existingProject.getDescription());

                    existingDescription = existingProject.getDescription();
                }
                /*else {
                    handleClearExistingProjectButton();
                    Убрано, потому что юзер не поймет, что происходит: текст не набирвается,
                    проверил при пустой базе проектов
                }*/
            }
        }
    }

    public void handleClearExistingProjectButton() {
        existingProjectTextField.clear();
        companyTextField.clear();
        managerTextArea.clear();
        descriptionTextArea.clear();
        existingDescription = "";
    }

    private void initButtons() {
        cancelButton.setCancelButton(true);
        createButton.setDefaultButton(true);
    }

    public void checkButtons() {
        if (companyTextField.getText() == null || companyTextField.getText().isEmpty()) {
            createButton.setDisable(true);
            return;
        }
        if (managerTextArea.getText() == null || managerTextArea.getText().isEmpty()) {
            createButton.setDisable(true);
            return;
        }
        if (descriptionTextArea.getText() == null || descriptionTextArea.getText().isEmpty()) {
            createButton.setDisable(true);
            return;
        }
        createButton.setDisable(false);
    }

    public void handleCancelButton() {
        AllData.createProjectWindow.close();
    }

    public void handleCreateButton() {

        if (!existingDescription.isEmpty() && existingDescription.equalsIgnoreCase(descriptionTextArea.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попытка создать копию проекта");
            alert.setHeaderText("Нельзя оставлять описание\nиз прежнего проекта без изменений");
            alert.setContentText("Внесите изменения в описание проекта,\nчтобы он отличался от прежнего проекта id-" + existingProjectTextField.getText());
            alert.showAndWait();
        }
        else {
            boolean empty = false;
            if (companyTextField.getText() == null || companyTextField.getText().isEmpty()) {
                empty = true;
            }
            if (managerTextArea.getText() == null || managerTextArea.getText().isEmpty()) {
                empty = true;
            }
            if (descriptionTextArea.getText() == null || descriptionTextArea.getText().isEmpty()) {
                empty = true;
            }

            if (empty) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Заполните все поля!");
                alert.setHeaderText("Нельзя оставлять какое-либо поле пустым\nЗаполните все поля: Компания, Менеджер и Описание");
                alert.showAndWait();
                return;
            }

            Integer newID = null;
            if (createIDTextField.isDisabled()) {
                newID = Updater.getProjectID();
                if (newID == null) {
                    createIDTextField.setText("Ошибка!");
                    return;
                }
                else {
                    AllData.createProjectID.set(newID);
                }
            }
            else {
                try {
                    newID = Integer.parseInt(createIDTextField.getText());
                } catch (NumberFormatException e) {

                }
            }

            if (newID != null) {
                Project result = AllData.createProject(newID, companyTextField.getText(), managerTextArea.getText(), descriptionTextArea.getText(), LocalDate.now());

                if (AllData.tableProjectsManagerController != null) {
                    AllData.tableProjectsManagerController.initializeTable();
                }

                if (AllData.staffWindowController != null) {
                    AllData.staffWindowController.initializeTable();
                }

                getProjectIDButton.setDisable(false);

                AllData.createProjectWindow.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Создан проект id-" + result.getIdNumber());
                alert.setHeaderText("Создан проект id-" + result.getIdNumber());
                alert.show();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка создания проекта");
                alert.setHeaderText("Не удалось создать новый проект – не удалось получить от сервера новый ID-номер проекта");
                alert.showAndWait();
                return;
            }
        }
    }
}
