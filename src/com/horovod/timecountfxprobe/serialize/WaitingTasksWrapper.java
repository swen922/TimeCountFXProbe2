package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;
import javafx.concurrent.Task;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "waitingtaskswrapper")
public class WaitingTasksWrapper {

    private List<Task> waitingTasks = new ArrayList<>();

    public WaitingTasksWrapper() {
        this.waitingTasks.addAll(AllData.waitingTasks);
    }

    @XmlElement(name = "waitingtaskslist")
    public List<Task> getWaitingTasks() {
        return waitingTasks;
    }

    public void setWaitingTasks(List<Task> waitingTasks) {
        this.waitingTasks = waitingTasks;
    }
}
