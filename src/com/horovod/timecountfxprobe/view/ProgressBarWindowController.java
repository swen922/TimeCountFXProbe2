package com.horovod.timecountfxprobe.view;

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

    private Stage myStage;
    private Task task;

    @FXML
    private ProgressBar progressBar;

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @FXML
    public void initialize() {

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                progressBar.progressProperty().unbind();
                myStage.close();
            }
        });

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());

        //Updater.getService().submit(task);
        Updater.update(task);

    }
}
