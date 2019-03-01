package com.horovod.timecountfxprobe.test;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Generator {

    /*public static void generate() {
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
                AllData.addWorkTime(project.getIdNumber(), date, ID, newtime);
            }

        }
    }*/

    /*public static void generate2() {
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
                AllData.addWorkTime(project.getIdNumber(), LocalDate.now().minusDays(j * 2), ID, newtime);
            }

        }


    }*/


    public static void generateUsers() {

        /*AllUsers.createUser("des1", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des1").setFullName("Семенова Прасковья");
        AllUsers.createUser("des2", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des2").setFullName("Анисимов Сергей");
        AllUsers.createUser("des3", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des3").setFullName("Шахматов Сергей");
        AllUsers.createUser("des4", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des4").setFullName("Ремесленннннннный Олег Егорьевич");
        AllUsers.createUser("des5", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des5").setFullName("Малиновская Мария");
        AllUsers.createUser("des6", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des6").setFullName("Балясников Артем");
        AllUsers.createUser("des7", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des7").setFullName("Коршунов Игорь");

        AllUsers.createUser("des8", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des8").setFullName("Восьмой Артем");
        AllUsers.createUser("des9", "pass", Role.DESIGNER);
        AllUsers.getOneUser("des9").setFullName("Девятый Валерий Павлович");*/


        /*AllUsers.createUser("man1", "pass", Role.MANAGER);
        AllUsers.getOneUser("man1").setFullName("Анисимова Инесса");
        AllUsers.createUser("man2", "pass", Role.MANAGER);
        AllUsers.getOneUser("man2").setFullName("Анисимов Сергей-админ");
        AllUsers.createUser("admin ", "pass", Role.ADMIN);
        AllUsers.getOneUser("admin").setFullName("Админ ващета");

        AllUsers.addLoggedUser(AllUsers.getOneUser(2).getFullName());
        AllUsers.addLoggedUser(AllUsers.getOneUser(3).getFullName());*/
    }

    public static void generateUsers2() {

        String partName = "des";
        String partNameManager = "man";
        String pass = "pass";

        for (int i = 1; i <= 9; i++) {
            AllUsers.createUser(i, partName + i, pass, Role.DESIGNER);
        }

        for (int i = 1; i <= 2; i++) {
            AllUsers.createUser(9+i, partNameManager + i, pass, Role.MANAGER);
        }

        AllUsers.setCurrentUser(5);

        AllUsers.addLoggedUser(AllUsers.getOneUser(2).getFullName());
        AllUsers.addLoggedUser(AllUsers.getOneUser(3).getFullName());
    }


    public static void generateProjects() {

        for (int i = 100; i >=1; i--) {
            String descr = "project-" + i;

            AllData.createProject(i,"Nestle", "Ivanov", descr, LocalDate.now().minusDays(25));

            /*Project project = new Project("Nestle", "Ivanov", descr, LocalDate.now().minusDays(25));
            AllData.addNewProject(project);*/
        }

        int minusDays = 0;

        for (int j = 0; j <= 10000; j++) {
            int projectID = (int) (Math.random() * 99 + 1);
            if (j % 50 == 0) {
                minusDays++;
            }
            int ID = (int) (Math.random() * 11 + 1);
            int tmp = (int) ((Math.random() * 1000) / 30);
            double newtime = AllData.intToDouble(tmp);
            if (!LocalDate.now().minusDays(minusDays).getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
                    !LocalDate.now().minusDays(minusDays).getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                AllData.addWorkTime(projectID, LocalDate.now().minusDays(minusDays), ID, newtime);
            }

        }

        for (int j = 0; j <= 50; j++) {
            int projectID = (int) (Math.random() * 99 + 1);
            int ID = (int) (Math.random() * 11 + 1);
            int tmp = (int) ((Math.random() * 1000) / 30);
            double newtime = AllData.intToDouble(tmp);
            AllData.addWorkTime(projectID, LocalDate.now(), ID, newtime);
        }
    }

    public static void generateProjects2() {

        for (int i = 1; i <=251; i++) {
            String descr = "project-" + i;
            //Project project = new Project("Nestle", "Ivanov", descr, LocalDate.now().minusDays(90));
            Project project = AllData.createProject(i,"Nestle", "Ivanov", descr, LocalDate.now().minusDays(90));
            project.setBudget((int) (Math.random() * 90000));
            if (i == 5) {
                project.setPONumber("PO 2345676");
            }
            if (i == 7) {
                project.setPONumber("PO 8798755");
            }
        }

        String descr01 = "Maggi Potatoes promobox display 2018_12 - мейл от Сидоренко 06.12.2018 в 17.40 - дизайн нанесения на промокороб-дисплей по продукту Магги Картошечка";
        String descr02 = "Nesquik Kosmostarts CPW Legoland Dubai promo display-prepack 2018_12 - мейл от Гладченко 06.12.2018 в 18.59 - дизайн дисплея-препака по промо по сухим завтракам Несквик и Космостарс";
        String descr03 = "Nescafe Gold label doypack 150g 250g 2018_12 - мейл от Климовой 06.12.2018 в 12.52 - корректировка текстов упаковок Нескафе Голд дойпак 150 г и 250 г";
        String descr04 = "Rossia leaflet NEW";

        Project p12 = AllData.createProject(252,"Nestle", "Сидоренко Юлия", descr01, null);
        p12.setBudget((int) (Math.random() * 90000));
        Project p13 = AllData.createProject(253,"Nestle", "Гладченко Наталья, Елагина Мария", descr02, null);
        p13.setBudget((int) (Math.random() * 90000));
        Project p14 = AllData.createProject(254,"Nestle", "Климова Дарья", descr03, null);
        p14.setBudget((int) (Math.random() * 90000));
        Project p15 = AllData.createProject(255,"Nestle", "Сидоренко Юлия, Климова Дарья", descr04, null);
        p15.setBudget((int) (Math.random() * 90000));


        int minusDays = 0;

        for (int j = 1; j <= 1000; j++) {
            int projectID = (int) (Math.random() * 250 + 1);
            if (j % 5 == 0) {
                minusDays++;
            }
            int ID = (int) (Math.random() * 9 + 1);
            int tmp = (int) ((Math.random() * 1000) / 5);
            double newtime = AllData.intToDouble(tmp);

            if (!LocalDate.now().minusDays(minusDays).getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
                    !LocalDate.now().minusDays(minusDays).getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                AllData.addWorkTime(projectID, LocalDate.now().minusDays(minusDays), ID, newtime);
            }
        }

        /*for (int j = 0; j <= 20; j++) {
            int projectID = (int) (Math.random() * 250 + 1);
            int ID = (int) (Math.random() * 12 + 1);
            int tmp = (int) ((Math.random() * 1000) / 30);
            double newtime = AllData.intToDouble(tmp);
            AllData.addWorkTime(projectID, LocalDate.now(), ID, newtime);
        }*/



        //AllData.addWorkTime(2, LocalDate.now().minusDays(3), 3, 35.5);



    }



    public static void generateProjects3() {

        for (int i = 1; i <=25; i--) {
            String descr = "project-" + i;
            Project project = AllData.createProject(i,"Nestle", "Ivanov", descr, LocalDate.now().minusDays(30));
            project.setBudget((int) (Math.random() * 90000));
            if (i == 5) {
                project.setPONumber("PO 2345676");
            }
            if (i == 7) {
                project.setPONumber("PO 8798755");
            }
            AllData.getAllProjects().put(project.getIdNumber(), project);
        }


        String descr01 = "Maggi Potatoes promobox display 2018_12 - мейл от Сидоренко 06.12.2018 в 17.40 - дизайн нанесения на промокороб-дисплей по продукту Магги Картошечка";
        String descr02 = "Nesquik Kosmostarts CPW Legoland Dubai promo display-prepack 2018_12 - мейл от Гладченко 06.12.2018 в 18.59 - дизайн дисплея-препака по промо по сухим завтракам Несквик и Космостарс";
        String descr03 = "Nescafe Gold label doypack 150g 250g 2018_12 - мейл от Климовой 06.12.2018 в 12.52 - корректировка текстов упаковок Нескафе Голд дойпак 150 г и 250 г";
        String descr04 = "Rossia leaflet NEW";

        Project p12 = AllData.createProject(26,"Nestle", "Сидоренко Юлия", descr01, null);
        p12.setBudget((int) (Math.random() * 90000));
        Project p13 = AllData.createProject(27,"Nestle", "Гладченко Наталья, Елагина Мария", descr02, null);
        p13.setBudget((int) (Math.random() * 90000));
        Project p14 = AllData.createProject(28,"Nestle", "Климова Дарья", descr03, null);
        p14.setBudget((int) (Math.random() * 90000));
        Project p15 = AllData.createProject(29,"Nestle", "Сидоренко Юлия, Климова Дарья", descr04, null);
        p15.setBudget((int) (Math.random() * 90000));


        int minusDays = 0;

        for (int j = 1; j <= 2; j++) {
            int projectID = (int) (Math.random() * 25 + 1);
            if (j % 2 == 0) {
                minusDays++;
            }
            int ID = (int) (Math.random() * 8 + 1);
            int tmp = (int) ((Math.random() * 1000) / 5);
            double newtime = AllData.intToDouble(tmp);

            if (!LocalDate.now().minusDays(minusDays).getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
                    !LocalDate.now().minusDays(minusDays).getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                AllData.addWorkTime(projectID, LocalDate.now().minusDays(minusDays), ID, newtime);
            }
        }
    }


}
