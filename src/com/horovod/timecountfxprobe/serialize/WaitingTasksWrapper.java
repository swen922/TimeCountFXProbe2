package com.horovod.timecountfxprobe.serialize;

import com.horovod.timecountfxprobe.project.AllData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;

@XmlRootElement(name = "waitingtaskswrapper")
public class WaitingTasksWrapper {

    //private ArrayList<SerializeWrapper> waitingTasks = new ArrayList<>();
    private HashMap<SerializeWrapper, UpdateType> waitingTasks = new HashMap<>();

    public WaitingTasksWrapper() {
        if (!AllData.waitingTasks.isEmpty()) {
            ArrayList<SerializeWrapper> listTMP = new ArrayList<>();
            listTMP.addAll(AllData.waitingTasks);
            for (SerializeWrapper wr : listTMP) {
                this.waitingTasks.put(wr, wr.getUpdateType());
            }
        }
    }

    @XmlElement(name = "waitingtasksmap")
    public HashMap<SerializeWrapper, UpdateType> getWaitingTasks() {
        return waitingTasks;
    }

    public void setWaitingTasks(HashMap<SerializeWrapper, UpdateType> waitingTasks) {
        this.waitingTasks = waitingTasks;
    }

    /*@XmlElement(name = "waitingtaskslist")
    public ArrayList<SerializeWrapper> getWaitingTasks() {
        return waitingTasks;
    }

    public void setWaitingTasks(ArrayList<SerializeWrapper> waitingTasks) {
        this.waitingTasks = waitingTasks;
    }*/

    @Override
    public String toString() {
        return "WaitingTasksWrapper{" +
                "waitingTasks=" + waitingTasks +
                '}';
    }
}
