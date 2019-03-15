package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkDay;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class InfoProjectWindowController {

    private Project myProject;
    private int myProjectID;
    private Stage myStage;

    private ObservableMap<String, WorkDay> workDays;
    private ObservableList<WorkDay> workDaysList;
    private List<TableColumn<WorkDay, String>> listColumns = FXCollections.observableArrayList();


    @FXML
    private AnchorPane topColoredPane;

    @FXML
    private Label idNumberLabel;

    @FXML
    private Label dateCreationLabel;

    @FXML
    private Button openFolderButton;


    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextArea companyNameTextArea;

    @FXML
    private TextArea managerTextArea;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private TextField tagsTextField;


    @FXML
    private Label workSum;

    @FXML
    private Label hoursSum;

    @FXML
    private Button closeButton;

    @FXML
    private Button revertButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button saveAndCloseButton;


    @FXML
    private TableView<WorkDay> workTimeTableView;

    @FXML
    private TableColumn<WorkDay, String> datesColumn;

    @FXML
    private TableColumn<WorkDay, String> timeColumn;




    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }
    public void setMyProject(int myProjectID) {
        this.myProject = AllData.getAnyProject(myProjectID);
    }

    @FXML
    public void initialize() {
        myProjectID = AllData.IDnumberForEdit;

        if (myProject == null) {
            myProject = AllData.getAnyProject(myProjectID);
        }

        initProjectFields();
        checkArchives();
        initializeTable();
        initSaveButtons();
        initClosing();

    }


    private void initProjectFields() {
        // id-номер проекта
        idNumberLabel.setText(String.valueOf(myProjectID));
        dateCreationLabel.setText(myProject.getDateCreationString());
        descriptionTextArea.setText(myProject.getDescription());
        companyNameTextArea.setText(myProject.getCompany());
        managerTextArea.setText(myProject.getManager());
        commentTextArea.setText(myProject.getComment());
        tagsTextField.setText(myProject.getTags());
    }


    public void updateProject() {

        myProject = AllData.getAnyProject(myProjectID);

        initProjectFields();
        checkArchives();
        initializeTable();
        initSaveButtons();
        initClosing();

    }

    private void checkArchives() {
        if (AllUsers.getOneUser(AllUsers.getCurrentUser()).isRetired() || AllData.getAnyProject(myProjectID).isArchive()) {
            openFolderButton.setDisable(true);
            companyNameTextArea.setEditable(false);
            managerTextArea.setEditable(false);
            descriptionTextArea.setEditable(false);
            commentTextArea.setEditable(false);
            tagsTextField.setEditable(false);
        }
        else {
            openFolderButton.setDisable(false);
            companyNameTextArea.setEditable(true);
            managerTextArea.setEditable(true);
            descriptionTextArea.setEditable(true);
            commentTextArea.setEditable(true);
            tagsTextField.setEditable(true);
        }

        if (AllData.getAnyProject(myProjectID).isArchive()) {
            topColoredPane.setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
        }
        else {
            topColoredPane.setStyle(null);
        }
    }

    public void initializeTable() {

        workSum.setText(AllData.formatWorkTime(AllData.intToDouble(myProject.getWorkSumForDesigner(AllUsers.getCurrentUser()))));
        hoursSum.setText(AllData.formatHours(AllData.formatWorkTime(AllData.intToDouble(myProject.getWorkSumForDesigner(AllUsers.getCurrentUser())))));

        if (workDays == null) {
            workDays = FXCollections.observableHashMap();
        }
        workDays.clear();


        for (WorkTime wt : myProject.getWork()) {

            if (wt.getDesignerID() == AllUsers.getCurrentUser()) {
                String dateString = wt.getDateString();

                if (!workDays.containsKey(dateString)) {
                    WorkDay workDay = new WorkDay(dateString);
                    workDay.addWorkTime(wt.getDesignerID(), wt.getTimeDouble());
                    workDays.put(dateString, workDay);
                }
                else {
                    WorkDay existingDay = workDays.get(wt.getDateString());
                    existingDay.addWorkTime(wt.getDesignerID(), wt.getTimeDouble());
                }
            }
        }

        workDaysList = FXCollections.observableArrayList(workDays.values());

        workDaysList.sort(new Comparator<WorkDay>() {
            @Override
            public int compare(WorkDay o1, WorkDay o2) {
                return o2.getDateString().compareTo(o1.getDateString());
            }
        });

        SortedList<WorkDay> sortedList = new SortedList<>(workDaysList, new Comparator<WorkDay>() {
            @Override
            public int compare(WorkDay o1, WorkDay o2) {
                return o2.getDateString().compareTo(o1.getDateString());
            }
        });

        datesColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WorkDay, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WorkDay, String> param) {
                return new SimpleStringProperty(param.getValue().getDateString());
            }
        });

        datesColumn.setStyle("-fx-alignment: CENTER;");


        timeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WorkDay, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WorkDay, String> param) {
                return new SimpleStringProperty(AllData.formatWorkTime(param.getValue().getWorkTimeForDesigner(AllUsers.getCurrentUser())));
            }
        });

        timeColumn.setStyle("-fx-alignment: CENTER;");

        workTimeTableView.setItems(sortedList);

        sortedList.comparatorProperty().bind(workTimeTableView.comparatorProperty());

    }

    public void initClosing() {
        if (myStage != null) {
            myStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    event.consume();
                    handleCloseButton();
                }
            });
        }
    }


    public void handleOpenFolderButton() {
        String startPath = "/Volumes/design/";
        String projectName = myProject.getDescription().split(" +- +")[0].trim() + " id-" + myProjectID;
        String path = startPath + myProject.getCompany() + "/" + projectName;

        if (myProject.getFolderPath() != null) {

            try {
                browsDir(myProject.getFolderPath());
            } catch (Exception e) {
                try {
                    browsDir(path);
                } catch (Exception e1) {
                    showAlertOpenFolder(myProjectID);

                }
            }
        }
        else {
            try {
                browsDir(path);
            } catch (Exception e) {
                showAlertOpenFolder(myProjectID);
            }
        }
    }

    private void browsDir(String path) throws Exception {
        Desktop.getDesktop().browseFileDirectory(new File(path));
    }

    private void showAlertOpenFolder(int projectID) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Не удалось открыть папку");
        alert.setHeaderText("Не удалось открыть папку");
        alert.setContentText("Не удалось найти и открыть\nпапку проекта id-" + projectID);
        alert.showAndWait();
    }

    public void initSaveButtons() {

        if (myProject.getComment() == null || myProject.getComment().isEmpty()) {
            if (commentTextArea.getText() == null || commentTextArea.getText().isEmpty()) {
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
        else if (myProject.getComment().equals(commentTextArea.getText())) {
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

        if (myProject.getComment() == null || myProject.getComment().isEmpty()){
            commentTextArea.clear();
        }
        else {
            commentTextArea.setText(myProject.getComment());
        }
        initSaveButtons();
    }

    public void handleSaveButton() {
        if (commentTextArea.getText() == null || commentTextArea.getText().isEmpty()) {
            myProject.setComment("");
        }
        else {
            myProject.setComment(commentTextArea.getText());
        }
        initSaveButtons();
        AllData.updateAllStatus("InfoProjectWindowController.handleSaveButton() - Текст комментария в проекте id-" + myProjectID + " изменен");
        AllData.logger.info(AllData.status);
    }

    public void handleSaveAndCloseButton() {
        handleSaveButton();
        handleCloseButton();
    }


    public void handleCloseButton() {

        if (myProject.getComment() == null || myProject.getComment().isEmpty()) {
            if (commentTextArea.getText() == null || commentTextArea.getText().isEmpty()) {
                procedeClosing();
                return;
            }
            else {
                handleAlerts();
                return;
            }
        }

        if (myProject.getComment().equals(commentTextArea.getText())) {
            procedeClosing();
        }
        else {
            handleAlerts();
        }
    }

    private void procedeClosing() {
        if (AllData.openInfoProjectStages.containsKey(myProjectID)) {
            AllData.openInfoProjectStages.get(myProjectID).close();
            AllData.openInfoProjectStages.remove(myProjectID);
        }
        if (AllData.infoProjectWindowControllers.containsKey(myProjectID)) {
            AllData.infoProjectWindowControllers.remove(myProjectID);
        }
    }

    private void handleAlerts() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение закрытия");
        alert.setHeaderText("В текст комментария в проекте id-" + myProjectID + " были внесены изменения.\nСохранить их или проигнорировать?");

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
            handleCloseButton();
            alert.close();
        }
        else {
            alert.close();
            handleSaveAndCloseButton();
        }
    }

}
