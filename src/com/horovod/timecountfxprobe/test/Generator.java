package com.horovod.timecountfxprobe.test;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;

import java.time.LocalDate;

public class Generator {

    public static void generate() {
        for (int i = 1; i <= 10; i++) {
            AllUsers.createUser("des" + i, "pass", Role.DESIGNER);
        }

        for (int i = 1; i <= 2; i++) {
            AllUsers.createUser("manager" + i, "pass", Role.MANAGER);
        }


        for (int j = 1; j<=100; j++) {
            String companyClient;
            String init;
            int year;
            int month;
            int day;

            if (j % 10 == 0) {
                companyClient = "Philips";
                init = "Бриксин Сергей, Донская Светлана";
                year = 2018;
                month = (int) (Math.random() * 10 + 1);
                if (month < 10) {
                    month = 11;
                }
                day = month * 2;

            }
            else if (j % 2 == 0) {
                companyClient = "McCormick";
                init = "Чепига Людмила";
                year = 2018;
                month = (int) (Math.random() * 10 + 1);
                if (month < 10) {
                    month = 10;
                }
                day = (int) (Math.random() * 25 + 2);
            }
            else {
                companyClient = "Nestle";
                init = "Романко Марина, Чечелева Ксения, Киндякова Полина, Лялина Ольга";
                year = 2018;
                //month = (int) (Math.random() * 10 + 1);
                month = 11;
                //day = (int) (Math.random() * 27 + 1);
                day = 16;
            }
            LocalDate date = LocalDate.of(year, month, day);
            String descr = new StringBuilder("project number ").append(j).append(" is of ").append(init).append(", and company is ").append(companyClient).toString();
            if (j % 10 == 0) {
                descr = descr + " | " + descr + " | " + descr;
            }
            Project project = new Project(companyClient, init, descr, date);

            int works = (int) (Math.random() * 5);

            AllData.addNewProject(project);

            for (int k = 0; k <= works; k++) {
                int ID = (int) (Math.random() * 10 + 1);
                int tmp = (int) ((Math.random() * 1000) / 30);
                double newtime = AllData.intToDouble(tmp);
                if (ID == 5) {
                    System.out.println("it's designer ID = 5 ! and worktime = " + newtime + " projectID = " + project.getIdNumber() +
                            "   and date is " + project.getDateCreationString());
                }
                AllData.addWorkTime(project.getIdNumber(), date, ID, newtime);
            }

        }
    }

    public static void generate2() {
        for (int i = 1; i <= 10; i++) {
            AllUsers.createUser("des" + i, "pass", Role.DESIGNER);
        }

        for (int i = 1; i <= 2; i++) {
            AllUsers.createUser("manager" + i, "pass", Role.MANAGER);
        }

        for (int i = 50; i >=1; i--) {
            String descr = "project-" + i;
            Project project = new Project("Nestle", "Ivanov", descr, LocalDate.now().minusDays(15));
            AllData.addNewProject(project);

            for (int j = 0; j <= 2; j++) {
                int ID = (int) (Math.random() * 10 + 1);
                int tmp = (int) ((Math.random() * 1000) / 30);
                double newtime = AllData.intToDouble(tmp);
                if (ID == 5) {
                    System.out.println("it's designer ID = 5 ! and worktime = " + newtime + " projectID = " + project.getIdNumber() +
                            "   and date is " + LocalDate.now().minusDays(j));
                }
                AllData.addWorkTime(project.getIdNumber(), LocalDate.now().minusDays(j * 2), ID, newtime);
            }

        }

        /*for (int k = 1; k <=12; k+=3) {
            int tmp2 = (int) ((Math.random() * 1000) / 30);
            double newtime2 = AllData.intToDouble(tmp2);
            AllData.addWorkTime(k, LocalDate.now(), 5, newtime2);
        }*/
    }


    public static void generateUsers() {
        for (int i = 1; i <= 7; i++) {
            AllUsers.createUser("des" + i, "pass", Role.DESIGNER);
            AllUsers.getOneUser("des"+i).setFullName("Good Designer Number " + i);
        }

        for (int i = 1; i <= 2; i++) {
            AllUsers.createUser("man" + i, "pass", Role.MANAGER);
            AllUsers.getOneUser("man"+i).setFullName("Good Manager Number " + i);
        }

        AllUsers.addLoggedUserByIDnumber(2);
        AllUsers.addLoggedUserByIDnumber(3);
    }


    public static void generateProjects() {

        for (int i = 100; i >=1; i--) {
            String descr = "project-" + i;
            Project project = new Project("Nestle", "Ivanov", descr, LocalDate.now().minusDays(25));
            AllData.addNewProject(project);
        }

        int minusDays = 0;

        for (int j = 0; j <= 10000; j++) {
            int projectID = (int) (Math.random() * 99 + 1);
            if (j % 50 == 0) {
                minusDays++;
            }
            int ID = (int) (Math.random() * 6 + 1);
            int tmp = (int) ((Math.random() * 1000) / 30);
            double newtime = AllData.intToDouble(tmp);
            AllData.addWorkTime(projectID, LocalDate.now().minusDays(minusDays), ID, newtime);
        }

        for (int j = 0; j <= 50; j++) {
            int projectID = (int) (Math.random() * 99 + 1);
            int ID = (int) (Math.random() * 6 + 1);
            int tmp = (int) ((Math.random() * 1000) / 30);
            double newtime = AllData.intToDouble(tmp);
            AllData.addWorkTime(projectID, LocalDate.now(), ID, newtime);
        }
    }

    public static void generateProjects2() {

        for (int i = 11; i >=1; i--) {
            String descr = "project-" + i;
            Project project = new Project("Nestle", "Ivanov", descr, LocalDate.now().minusDays(25));
            if (i == 5) {
                project.setPONumber("PO 2345676");
            }
            if (i == 7) {
                project.setPONumber("PO 8798755");
            }
            AllData.addNewProject(project);

        }

        int minusDays = 0;

        for (int j = 0; j <= 100; j++) {
            int projectID = (int) (Math.random() * 10 + 1);
            if (j % 2 == 0) {
                minusDays++;
            }
            int ID = (int) (Math.random() * 6 + 1);
            int tmp = (int) ((Math.random() * 1000) / 30);
            double newtime = AllData.intToDouble(tmp);
            AllData.addWorkTime(projectID, LocalDate.now().minusDays(minusDays), ID, newtime);
        }

        for (int j = 0; j <= 20; j++) {
            int projectID = (int) (Math.random() * 9 + 1);
            int ID = (int) (Math.random() * 6 + 1);
            int tmp = (int) ((Math.random() * 1000) / 30);
            double newtime = AllData.intToDouble(tmp);
            AllData.addWorkTime(projectID, LocalDate.now(), ID, newtime);
        }

        String descr01 = "Maggi Potatoes promobox display 2018_12 - мейл от Сидоренко 06.12.2018 в 17.40 - дизайн нанесения на промокороб-дисплей по продукту Магги Картошечка";
        String descr02 = "Nesquik Kosmostarts CPW Legoland Dubai promo display-prepack 2018_12 - мейл от Гладченко 06.12.2018 в 18.59 - дизайн дисплея-препака по промо по сухим завтракам Несквик и Космостарс";
        String descr03 = "Nescafe Gold label doypack 150g 250g 2018_12 - мейл от Климовой 06.12.2018 в 12.52 - корректировка текстов упаковок Нескафе Голд дойпак 150 г и 250 г";

        AllData.addNewProject(new Project("Nestle", "Сидоренко Юлия", descr01));
        AllData.addNewProject(new Project("Nestle", "Гладченко Наталья, Елагина Мария", descr02));
        AllData.addNewProject(new Project("Nestle", "Климова Дарья", descr03));

    }

}
