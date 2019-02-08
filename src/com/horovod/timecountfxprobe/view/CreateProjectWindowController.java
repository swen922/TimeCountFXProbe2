package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CreateProjectWindowController {

    private String existingDescription = "";

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

        companyTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.TAB) {
                    managerTextArea.requestFocus();
                }
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

        descriptionTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.TAB) {
                    createButton.requestFocus();
                }
            }
        });

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
                else {
                    handleClearExistingProjectButton();
                }
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
            Project result = AllData.createProject(companyTextField.getText(), managerTextArea.getText(), descriptionTextArea.getText(), LocalDate.now());
            AllData.tableProjectsManagerController.initializeTable();
            if (AllData.staffWindowController != null) {
                AllData.staffWindowController.initializeTable();
            }

            AllData.createProjectWindow.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Создан проект id-" + result.getIdNumber());
            alert.setHeaderText("Создан проект id-" + result.getIdNumber());
            alert.show();
        }
    }
}
