package com.horovod.timecountfxprobe.threads;

import com.horovod.timecountfxprobe.project.AllData;
import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadBaseOnServer extends Task<Boolean> {



    @Override
    protected Boolean call() throws Exception {

        System.out.println("inside call");

        //return null;
        try {
            URL url = new URL("http://localhost:8088/readbase");
            //URL url = new URL(AllData.httpUpdate);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            System.out.println(connection);

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            out.write("readNow");
            out.close();

            int responceCode = connection.getResponseCode();

            System.out.println(responceCode);

            StringBuilder sb = new StringBuilder("");
            String receivedString = null;
            BufferedReader inn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((receivedString = inn.readLine()) != null) {
                sb.append(receivedString);
            }
            inn.close();

            System.out.println(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
