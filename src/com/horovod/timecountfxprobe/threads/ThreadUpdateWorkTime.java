package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.concurrent.Callable;

public class ThreadUpdateWorkTime extends Task<WorkTime> {

    private int projectIDnumber;
    private LocalDate date;
    private int idUser;
    private double time;

    public ThreadUpdateWorkTime(int projectIDnumber, LocalDate date, int idUser, double time) {
        this.projectIDnumber = projectIDnumber;
        this.date = date;
        this.idUser = idUser;
        this.time = time;
    }


    @Override
    public WorkTime call() throws Exception {

        WorkTime workTime = null;

        System.out.println("inside call");

        workTime = AllData.getAnyProject(projectIDnumber).getWorkTimeForDesignerAndDate(idUser, date);

        try {

            ObjectMapper mapper = new ObjectMapper();
            String jsonSerialize = mapper.writeValueAsString(workTime);

            URL url = new URL("http://localhost:8088/updateworktime");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(jsonSerialize);
            out.close();

            int responceCode = connection.getResponseCode();

            StringBuilder sb = new StringBuilder();
            String tmp = null;
            BufferedReader inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((tmp = inn.readLine()) != null) {
                sb.append(tmp);
            }
            inn.close();

            System.out.println("responceCode = " + responceCode);
            System.out.println("received from server = " + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
