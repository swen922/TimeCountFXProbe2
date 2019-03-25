package com.horovod.timecountfxprobe.test;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import javafx.application.Platform;

import java.time.LocalDate;

public class TestBackgroundUpdate01 {


    public void testBackgroundAddTime() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AllData.addWorkTime(12, LocalDate.now(),3, 60);

                if (AllData.tableProjectsDesignerController != null) {
                    AllData.tableProjectsDesignerController.updateDesignerWindow();
                }
                if (AllData.tableProjectsManagerController != null) {
                    AllData.tableProjectsManagerController.initialize();
                }
                if (AllData.editProjectWindowControllers.get(12) != null) {
                    AllData.editProjectWindowControllers.get(12).updateEditProjectWindow();
                }

            }
        };

        Platform.runLater(runnable);

        /*Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();*/
    }

    public void testBackgroundDeleteTime() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AllData.addWorkTime(12, LocalDate.now(),3, 20);

                if (AllData.tableProjectsDesignerController != null) {
                    AllData.tableProjectsDesignerController.updateDesignerWindow();
                }
                if (AllData.tableProjectsManagerController != null) {
                    AllData.tableProjectsManagerController.initialize();
                }
                if (AllData.editProjectWindowControllers.get(12) != null) {
                    AllData.editProjectWindowControllers.get(12).updateEditProjectWindow();
                }
            }
        };

        Platform.runLater(runnable);

        /*Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();*/

    }

}
