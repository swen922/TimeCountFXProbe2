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

public class ProgressBarWindowController {

    private Task task;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void initialize() {

        task = AllData.taskForProgressBar;

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                progressBar.progressProperty().unbind();
                AllData.progressBarStage.close();
            }
        });

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());

        //Updater.getService().submit(task);
        Updater.update(task);

    }
}
