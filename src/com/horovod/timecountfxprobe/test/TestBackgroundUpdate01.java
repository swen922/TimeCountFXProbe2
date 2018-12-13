package com.horovod.timecountfxprobe.test;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import javafx.application.Platform;

import java.time.LocalDate;

public class TestBackgroundUpdate01 {



    public void test5() {

        AllData.addWorkTime(12, LocalDate.now(),5, 20);
        AllData.addWorkTime(12, LocalDate.now(),5, 40);
        AllData.addWorkTime(12, LocalDate.now(),5, 60);
    }

    int prevous = 0;

    public void testBackgroundAddTime() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                /*Project p = AllData.getOneActiveProject(10);
                if (p.containsWorkTime(5, LocalDate.now().minusDays(2))) {
                    prevous = AllData.getOneActiveProject(10).getWorkSumForDesignerAndDate(5, LocalDate.now().minusDays(2));
                }*/

                System.out.println("inside run");

                AllData.addWorkTime(12, LocalDate.now(),5, 100);

                /*Platform.runLater(new Runnable() {
                    @Override public void run() {
                        AllData.getTableProjectsDesignerController().initialize();
                    }
                });*/
            }
        };

        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
    }

    public void testBackgroundDeleteTime() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AllData.addWorkTime(10, LocalDate.now().minusDays(2),5, AllData.intToDouble(prevous));

                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        AllData.getTableProjectsDesignerController().initialize();
                    }
                });
            }
        };

        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();

    }

}
