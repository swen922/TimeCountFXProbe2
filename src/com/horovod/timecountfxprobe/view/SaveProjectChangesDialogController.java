package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SaveProjectChangesDialogController {

    private int projectIDnumber;
    private Stage myStage;
    private Stage editProjectStage;
    private EditProjectWindowController editProjectWindowController;

    @FXML
    private Button returnButton;

    @FXML
    private Button dontSaveButton;

    @FXML
    private Button saveButton;


    public void setProjectIDnumber(int projectIDnumber) {
        this.projectIDnumber = projectIDnumber;
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public void setEditProjectStage(Stage editProjectStage) {
        this.editProjectStage = editProjectStage;
    }

    public void setEditProjectWindowController(EditProjectWindowController editProjectWindowController) {
        this.editProjectWindowController = editProjectWindowController;
    }


    public void handleReturnButton() {
        myStage.close();
    }

    public void handleDontSaveButton() {

        AllData.editProjectWindowControllers.get(projectIDnumber).handleRevertButton();
        AllData.editProjectWindowControllers.remove(projectIDnumber);
        AllData.openEditProjectStages.remove(projectIDnumber);

        myStage.close();
        editProjectStage.close();
    }

    public void handleSaveButton() {
        editProjectWindowController.handleSaveAndCloseButton();
    }


}
