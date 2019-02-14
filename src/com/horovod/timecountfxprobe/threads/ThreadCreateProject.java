package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import javafx.concurrent.Task;

import java.time.LocalDate;

// Для пробы скопирован, чтобы вспомнить
// Вообще нужны нити только по сетевым операциям

public class ThreadCreateProject extends Task<Project> {

    private String company;
    private String manager;
    private String description;
    private LocalDate date;

    public ThreadCreateProject(String company, String manager, String description, LocalDate newDate) {
        this.company = company;
        this.manager = manager;
        this.description = description;
        this.date = newDate;
    }

    @Override
    protected Project call() throws Exception {
        //return null;
        return AllData.createProject(company, manager, description, date);
    }
}
