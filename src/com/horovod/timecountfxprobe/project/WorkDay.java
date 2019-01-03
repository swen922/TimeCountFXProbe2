package com.horovod.timecountfxprobe.project;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WorkDay {

    private String dateString;
    private Map<Integer, Double> workTimeMap;

    public WorkDay(LocalDate localDate) {
        this.dateString = AllData.formatDate(localDate);
        this.workTimeMap = new HashMap<>();
    }

    public WorkDay(String newDateString) {
        this.dateString = newDateString;
        this.workTimeMap = new HashMap<>();
    }

    public String getDateString() {
        return dateString;
    }

    public LocalDate getDate() {
        return AllData.parseDate(this.dateString);
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public void setDate(LocalDate date) {
        this.dateString = AllData.formatDate(date);
    }


    public Map<Integer, Double> getWorkTimeMap() {
        return workTimeMap;
    }

    public void setWorkTimeMap(Map<Integer, Double> workTimeMap) {
        this.workTimeMap = workTimeMap;
    }

    public void addWorkTime(int designerIDnumber, double timeDouble) {
        if (this.workTimeMap.containsKey(designerIDnumber)) {
            double oldtime = this.workTimeMap.get(designerIDnumber);
            this.workTimeMap.put(designerIDnumber, AllData.formatDouble(oldtime + timeDouble, 1));
        }
        else {
            getWorkTimeMap().put(designerIDnumber, timeDouble);
        }
        deleteZeroWorks();
    }

    private void deleteZeroWorks() {
        Iterator<Map.Entry<Integer, Double>> iter = workTimeMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Double> entry = iter.next();
            if (entry.getValue() == 0) {
                iter.remove();
            }
        }
    }

    public double getWorkTimeForDesigner(int designerID) {
        if (workTimeMap.containsKey(designerID)) {
            return workTimeMap.get(designerID);
        }
        else {
            return 0;
        }
    }

    public boolean containsWorkTime() {
        return !workTimeMap.isEmpty();
    }

    public boolean containsWorkTimeForDesigner(int designerIDnumber) {
        return workTimeMap.containsKey(designerIDnumber);
    }
}
