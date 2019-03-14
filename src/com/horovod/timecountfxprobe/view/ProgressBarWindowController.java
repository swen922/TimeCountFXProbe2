package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.threads.ThreadSendBaseToServer;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ProgressBarWindowController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void initialize() {
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(AllData.taskForProgressBar.progressProperty());
    }

    private void initClosing() {
        AllData.progressBarStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

            }
        });
    }
}
