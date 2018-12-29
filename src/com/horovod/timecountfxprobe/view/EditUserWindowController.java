package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class EditUserWindowController {

    private int userID;
    private boolean isChanged = false;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void closing() {

        if (!isChanged) {
            AllData.editUserStages.get(userID).close();
            if (AllData.editUserStages.containsKey(userID)) {
                AllData.editUserStages.remove(userID);
            }
            if (AllData.editUserWindowControllers.containsKey(userID)) {
                AllData.editUserWindowControllers.remove(userID);
            }

        }
    }

    public void initClosing() {
        System.out.println("inside initClosing()");
        AllData.editUserStages.get(userID).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                System.out.println("inside handle() EditUserWindowController");


                //AllData.editUserStages.get(userID).close();
                AllData.editUserStages.remove(userID);
                AllData.editUserWindowControllers.remove(userID);
                System.out.println("set on close");
                System.out.println("inside EditUserController: mAllData.editUserStages.containsKey(userID) = " + AllData.editUserStages.containsKey(userID));

                /*if (!isChanged) {
                    System.out.println("set on close");
                    AllData.editUserStages.get(userID).close();
                    AllData.editUserStages.remove(userID);
                    AllData.editUserWindowControllers.remove(userID);
                }
                else {

                }*/
            }
        });
    }
}
