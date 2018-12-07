package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditProjectWindowController {

    private MainApp mainApp;
    private Project myProject;
    private EditProjectWindowController editProjectWindowController;

    private boolean isChanged = false;
    private List<String> changedFields = new ArrayList<>();



    @FXML
    private AnchorPane topColoredPane;

    @FXML
    private Label idNumberLabel;

    @FXML
    private TextArea projectNameTextArea;

    @FXML
    private Button openFolderButton;

    @FXML
    private CheckBox archiveCheckBox;


    @FXML
    private TextArea companyNameTextArea;

    @FXML
    private TextArea managerTextArea;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private TextField linkedProjectsTextField;

    @FXML
    private TextField POnumberTextField;

    @FXML
    private TextField pathToFolderTextField;


    @FXML
    private Label workSum;

    @FXML
    private Button exportToCSVButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button revertButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button saveAndCloseButton;


    @FXML
    private TableView<WorkTime> workTimeTableView;

    @FXML
    private TableColumn<WorkTime, String> datesTableColumn;



    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public Project getMyProject() {
        return myProject;
    }

    public void setMyProject(Project myProject) {
        this.myProject = myProject;
    }



    @FXML
    private void initialize() {

        if (myProject == null) {
            myProject = AllData.getAnyProject(AllData.tableProjectsManagerController.getIDnumberForEditProject());
        }

        // id-номер проекта
        idNumberLabel.setText(String.valueOf(myProject.getIdNumber()));

        // название папки
        projectNameTextArea.setText(myProject.getDescription().split(" - ")[0].trim());

        // инициализация кнопки открытия папки – прописано в FXML

        //стартовая инициализация чекбокса Архивный согласно состоянию проекта
        initializeArchiveCheckBox();

        companyNameTextArea.setText(myProject.getCompany());
        managerTextArea.setText(myProject.getInitiator());
        descriptionTextArea.setText(myProject.getDescription());
        commentTextArea.setText(myProject.getComment());

        if (myProject.getLinkedProjects() != null && !myProject.getLinkedProjects().isEmpty()) {
            StringBuilder sb = new StringBuilder("");
            for (int p : myProject.getLinkedProjects()) {
                sb.append(AllData.getAnyProject(p).getIdNumber()).append(", ");
            }
            String linked = sb.toString().trim();
            linked = linked.substring(0, (linked.length() - 1));
            linkedProjectsTextField.setText(linked);
        }

        POnumberTextField.setText(myProject.getPONumber());
        pathToFolderTextField.setText(myProject.getFolderPath());

    }

    public void initOpenFolderButton() {
        String startPath = "/Volumes/design/";
        String path;
        if (myProject.getFolderPath() != null) {
            path = myProject.getFolderPath();

            try {
                Desktop.getDesktop().browseFileDirectory(new File(path));
            } catch (Exception e) {
                String projectName = myProject.getDescription().split(" - ")[0].trim() + " id-" + myProject.getIdNumber();
                path = startPath + myProject.getCompany() + "/" + projectName;

                try {
                    Desktop.getDesktop().browseFileDirectory(new File(path));
                } catch (Exception e1) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Не удалось открыть папку");
                    alert.setHeaderText("Не удалось открыть папку");
                    alert.setContentText("Не удалось найти и открыть\nпапку проекта id-" + myProject.getIdNumber());
                    alert.showAndWait();
                }
            }

        }
        else {
            String projectName = myProject.getDescription().split(" - ")[0].trim() + " id-" + myProject.getIdNumber();
            path = startPath + myProject.getCompany() + "/" + projectName;

            try {
                Desktop.getDesktop().browseFileDirectory(new File(path));
            } catch (Exception e1) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Не удалось открыть папку");
                alert.setHeaderText("Не удалось открыть папку");
                alert.setContentText("Не удалось найти и открыть\nпапку проекта id-" + myProject.getIdNumber());
                alert.showAndWait();
            }
        }

    }



    private void initializeArchiveCheckBox() {
        if (myProject.isArchive()) {
            topColoredPane.setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
            openFolderButton.setDisable(true);
            archiveCheckBox.setSelected(true);
            projectNameTextArea.setEditable(false);
            companyNameTextArea.setEditable(false);
            managerTextArea.setEditable(false);
            descriptionTextArea.setEditable(false);
            POnumberTextField.setEditable(false);
            pathToFolderTextField.setEditable(false);
            workTimeTableView.setEditable(false);
        }
        else {
            topColoredPane.setStyle(null);
            openFolderButton.setDisable(false);
            archiveCheckBox.setSelected(false);
            projectNameTextArea.setEditable(true);
            companyNameTextArea.setEditable(true);
            managerTextArea.setEditable(true);
            descriptionTextArea.setEditable(true);
            POnumberTextField.setEditable(true);
            pathToFolderTextField.setEditable(true);
            workTimeTableView.setEditable(true);
        }
    }

    public void handleArchiveCheckBox() {
        if (archiveCheckBox.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение перевода в архив");
            alert.setHeaderText("Перевести проект id-" + myProject.getIdNumber() + " в архив?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK) {
                AllData.changeProjectArchiveStatus(myProject.getIdNumber(), true);
                initializeArchiveCheckBox();
            }
            else {
                AllData.changeProjectArchiveStatus(myProject.getIdNumber(), false);
                initializeArchiveCheckBox();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение вывода из архива");
            alert.setHeaderText("Вывести проект id-" + myProject.getIdNumber() + " из архива?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK) {
                AllData.changeProjectArchiveStatus(myProject.getIdNumber(), false);
                initializeArchiveCheckBox();
            }
            else if (option.get() == ButtonType.CANCEL) {
                AllData.changeProjectArchiveStatus(myProject.getIdNumber(), true);
                initializeArchiveCheckBox();
            }
        }
    }




    public void writeCSV() {
        /*Writer writer = null;
        File file = new File("/_jToys/example1.csv");
        writer = new BufferedWriter(new FileWriter(file));*/


    }

}
