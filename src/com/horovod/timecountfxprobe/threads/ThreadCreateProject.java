package com.horovod.timecountfxprobe.threads;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
        Project result = AllData.createProject(company, manager, description, date);
        if (result != null) {
            /*ObjectMapper mapper = new ObjectMapper();
            String jsonSerialize = mapper.writeValueAsString(result);

            URL url = new URL("http://localhost:8088/updateproject");
            //URL url = new URL("https://localhost:8443/readwrapper");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write(jsonSerialize);
            out.close();

            int responceCode = connection.getResponseCode();
            System.out.println("responceCode = " + responceCode);

            StringBuilder sb = new StringBuilder();
            String tmp = null;
            BufferedReader inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((tmp = inn.readLine()) != null) {
                sb.append(tmp);
            }
            inn.close();

            System.out.println("sb.toString = " + sb.toString());*/

        }

        return null;

    }
}
