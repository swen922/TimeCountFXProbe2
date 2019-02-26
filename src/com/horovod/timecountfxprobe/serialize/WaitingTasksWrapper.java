package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;
import javafx.concurrent.Task;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "waitingtaskswrapper")
public class WaitingTasksWrapper {

    private ArrayList<SerializeWrapper> waitingTasks = new ArrayList<>();

    public WaitingTasksWrapper() {
        if (!AllData.waitingTasks.isEmpty()) {
            this.waitingTasks.addAll(AllData.waitingTasks);
        }
    }

    @XmlElement(name = "waitingtaskslist")
    public ArrayList<SerializeWrapper> getWaitingTasks() {
        return waitingTasks;
    }

    public void setWaitingTasks(ArrayList<SerializeWrapper> waitingTasks) {
        this.waitingTasks = waitingTasks;
    }
}
