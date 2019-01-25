package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoProjectWindowController {

    private Project myProject;
    private Stage myStage;

    private ObservableMap<String, WorkDay> workDays;
    private ObservableList<WorkDay> workDaysList;
    private List<TableColumn<WorkDay, String>> listColumns = FXCollections.observableArrayList();


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
    private TextField linkedProjectsTextField;


    @FXML
    private Label workSum;

    @FXML
    private Label hoursSum;

    @FXML
    private Label designersWorkSums;

    @FXML
    private Button closeButton;

    @FXML
    private TableView<WorkDay> workTimeTableView;



    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }



    @FXML
    private void initialize() {

        if (myProject == null) {
            myProject = AllData.getAnyProject(AllData.IDnumberForEdit);
        }

        // id-номер проекта
        idNumberLabel.setText(String.valueOf(myProject.getIdNumber()));
        dateCreationLabel.setText(myProject.getDateCreationString());
        descriptionTextArea.setText(myProject.getDescription());
        companyNameTextArea.setText(myProject.getCompany());
        managerTextArea.setText(myProject.getManager());
        commentTextArea.setText(myProject.getComment());
        linkedProjectsTextField.setText(myProject.getLinkedProjects());
        workSum.textProperty().bind(myProject.workSumProperty());
        initializeTable();

    }

    public void initializeTable() {

    }

    private void initClosing() {
        if (myStage != null) {
            myStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (AllData.openInfoProjectStages.containsKey(myProject.getIdNumber())) {
                        AllData.openInfoProjectStages.remove(myProject.getIdNumber());
                    }
                    if (AllData.openInfoProjectStages.containsKey(myProject.getIdNumber())) {
                        AllData.openInfoProjectStages.remove(myProject.getIdNumber());
                    }
                }
            });
        }
    }

    private void handleCloseButton() {
        /*if (!isChanged) {
            if (AllData.openEditProjectStages.containsKey(myProject.getIdNumber())) {
                AllData.openEditProjectStages.remove(myProject.getIdNumber());
            }
            if (AllData.editProjectWindowControllers.containsKey(myProject.getIdNumber())) {
                AllData.editProjectWindowControllers.remove(myProject.getIdNumber());

            }
            myStage.close();
        }
        else {
            handleAlerts();
        }*/
    }

}
