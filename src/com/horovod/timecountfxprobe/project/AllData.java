package com.horovod.timecountfxprobe.project;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.serialize.SerializeWrapper;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.view.*;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParsePosition;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class AllData {


    public static final Logger logger = Logger.getLogger(AllData.class);

    //private static volatile AtomicInteger idNumber = new AtomicInteger(0);
    //private static volatile IntegerProperty idNumberProperty = new SimpleIntegerProperty(idNumber.get());

    private static ObservableMap<Integer, Project> allProjects = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());
    private static ObservableMap<Integer, Project> activeProjects = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());

    private static volatile AtomicInteger workSumProjects = new AtomicInteger(0);
    private static volatile DoubleProperty workSumProjectsProperty = new SimpleDoubleProperty(AllData.intToDouble(workSumProjects.get()));

    public static volatile IntegerProperty createProjectID = new SimpleIntegerProperty(0);

    private static DoubleProperty todayWorkSumProperty = new SimpleDoubleProperty(0);
    private static DoubleProperty weekWorkSumProperty = new SimpleDoubleProperty(0);
    private static DoubleProperty monthWorkSumProperty = new SimpleDoubleProperty(0);
    private static DoubleProperty yearWorkSumProperty = new SimpleDoubleProperty(0);

    /** В чистовой версии перенести все поля по суммам рабочего времени дизайнера за день, неделю, месяц и год
     * внутрь класса TableProjectsDesignerController, а в AllData оставить только глобальные суммы по всем дизайнерам */
    private static DoubleProperty designerDayWorkSumProperty = new SimpleDoubleProperty(0);
    private static IntegerProperty designerRatingPosition = new SimpleIntegerProperty(0);
    private static DoubleProperty designerWeekWorkSumProperty = new SimpleDoubleProperty(0);
    private static DoubleProperty designerMonthWorkSumProperty = new SimpleDoubleProperty(0);
    private static DoubleProperty designerYearWorkSumProperty = new SimpleDoubleProperty(0);

    private static String meUser = System.getProperty("user.name");
    public static String pathToHomeFolder = "/Users/" + meUser + "/Library/Application Support/TimeCountProbeFX";
    public static String pathToDownloads = "/Users/" + meUser + "/Downloads";
    public static int globalUpdatePeriod = 120;
    public static int checkWaitingPeriod = 30;

    public static String lastUpdateTime = "";

    public static String httpAddress = "http://localhost:8088";
    public static String httpUpdate = httpAddress + "/receiveupdate";
    public static String httpGetProjectID = httpAddress + "/projectid";
    public static String httpGetUserID = httpAddress + "/userid";
    public static String httpGlobalUpdate = httpAddress + "/globalupdate";
    public static String httpSendBaseToServer = httpAddress + "/sendbasetoserver";
    public static String httpSetProjectID = httpAddress + "/setprojectid";
    public static String httpSetUserID = httpAddress + "/setuserid";
    public static String httpReadBaseOnServer = httpAddress + "/readbase";
    public static String httpSaveBaseOnServer = httpAddress + "/savebase";







    /** Поле нужно, чтобы передавать его отдельным нитям (см. класс TestBackgroundUpdate01)
     * */

    /** !!!!!! В чистовой версии загнать сюда ВСЕ статические экземпляры контролеров и нодов!
     * А из MainApp убрать их !!!!!
     * И решить по всем единообразно: либо через геттеры-сеттеры доступ, либо public сделать (второе экономней по коду)
     * !!!!!!!!*/

    public static Stage primaryStage;
    public static BorderPane rootLayout;
    public static MainApp mainApp;

    public static TableProjectsDesignerController tableProjectsDesignerController;
    public static TableProjectsManagerController tableProjectsManagerController;

    public static Stage statisticStage;
    public static StatisticWindowController statisticWindowController;

    public static Stage statisticManagerStage;
    public static StatisticManagerWindowController statisticManagerWindowController;
    public static Map<Integer, Stage> openEditProjectStages = new ConcurrentHashMap<>();
    public static Map<Integer, EditProjectWindowController> editProjectWindowControllers = new ConcurrentHashMap<>();
    public static volatile int IDnumberForEdit = 0;
    public static Stage staffWindowStage;
    public static StaffWindowController staffWindowController;
    public static Stage countSalaryWindow;
    public static CountSalaryWindowController countSalaryWindowController;
    public static AdminWindowController adminWindowController;
    public static Stage createUserWindow;
    public static CreateUserWindowController createUserWindowController;
    public static Stage createProjectWindow;
    public static CreateProjectWindowController createProjectWindowController;
    public static Stage deleteLoggedUserStage;
    public static DeleteLoggedUsersWindowController deleteLoggedUsersWindowController;
    public static Task taskForProgressBar;
    public static Stage progressBarStage;
    public static ProgressBarWindowController progressBarWindowController;
    public static boolean result = false;

    public static Map<Integer, Stage> openInfoProjectStages = new ConcurrentHashMap<>();
    public static Map<Integer, InfoProjectWindowController> infoProjectWindowControllers = new ConcurrentHashMap<>();

    // TODO для начала будем хранить в списке не абстрактные Task, а наши SerializeWrapper'ы
    public static BlockingQueue<SerializeWrapper> waitingTasks = new LinkedBlockingQueue<>();



    private static volatile double limitTimeForStaffWindow = 6;
    private static volatile int limitMoneyForStaffWindow = 6000;
    public static final String toLoginWindow = "Выйти в окно логина";
    public static String timeStamp = formatDateTime(LocalDateTime.now()) + " - ";
    public static String status = "Все нормально";


    public static void rebuildHTTPAddresses() {
        httpUpdate = httpAddress + "/receiveupdate";
        httpGetProjectID = httpAddress + "/projectid";
        httpGetUserID = httpAddress + "/userid";
        httpGlobalUpdate = httpAddress + "/globalupdate";
        httpSendBaseToServer = httpAddress + "/sendbasetoserver";
        httpSetProjectID = httpAddress + "/setprojectid";
        httpSetUserID = httpAddress + "/setuserid";
        httpReadBaseOnServer = httpAddress + "/readbase";
        httpSaveBaseOnServer = httpAddress + "/savebase";
    }

    /** Стандартные геттеры и сеттеры */




    /*public static int incrementIdNumberAndGet() {
        return idNumber.incrementAndGet();

    }

    public static int getIdNumber() {
        return idNumber.get();
    }

    public static synchronized void setIdNumber(int newIdNumber) {
        idNumber.set(newIdNumber);
        idNumberProperty.set(newIdNumber);
    }

    public static int getIdNumberProperty() {
        return idNumberProperty.get();
    }

    public static IntegerProperty idNumberProperty() {
        return idNumberProperty;
    }

    private static void setIdNumberProperty(int newIdNumberProperty) {
        AllData.idNumberProperty.set(newIdNumberProperty);
    }*/


    public static ObservableMap<Integer, Project> getAllProjects() {
        return allProjects;
    }

    public static synchronized void setAllProjects(Map<Integer, Project> newAllProjects) {
        AllData.allProjects.clear();
        AllData.allProjects.putAll(newAllProjects);
        rebuildActiveProjects();

    }

    public static Map<Integer, Project> getActiveProjects() {
        return activeProjects;
    }


    public static int getWorkSumProjects() {
        return workSumProjects.get();
    }

    public static void setWorkSumProjects(int newWorkSum) {
        AllData.workSumProjects.set(newWorkSum);
    }

    private static synchronized void addWorkSumProjects(int addTime) {
        AllData.workSumProjects.addAndGet(addTime);
        AllData.workSumProjectsProperty.set(AllData.intToDouble(workSumProjects.get()));
    }


    public static double getWorkSumProjectsProperty() {
        return workSumProjectsProperty.get();
    }

    public static DoubleProperty workSumProjectsProperty() {
        return workSumProjectsProperty;
    }

    public static synchronized void rebuildWorkSum() {
        int counter = 0;
        for (Project p : allProjects.values()) {
            counter += p.getWorkSum();
        }
        AllData.workSumProjects.set(counter);
        AllData.workSumProjectsProperty.set(AllData.intToDouble(counter));
    }


    public static double getLimitTimeForStaffWindow() {
        return limitTimeForStaffWindow;
    }

    public static void setLimitTimeForStaffWindow(double limitTimeForStaffWindow) {
        AllData.limitTimeForStaffWindow = limitTimeForStaffWindow;
    }

    public static int getLimitMoneyForStaffWindow() {
        return limitMoneyForStaffWindow;
    }

    public static void setLimitMoneyForStaffWindow(int limitMoneyForStaffWindow) {
        AllData.limitMoneyForStaffWindow = limitMoneyForStaffWindow;
    }



    public static DoubleProperty todayWorkSumProperty() {
        return todayWorkSumProperty;
    }

    public static synchronized void rebuildTodayWorkSumProperty() {

        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(LocalDate.now())) {
                counter += p.getWorkSumForDate(LocalDate.now());
            }
        }
        AllData.todayWorkSumProperty.set(AllData.intToDouble(counter));

    }


    public static DoubleProperty weekWorkSumPropertyProperty() {
        return weekWorkSumProperty;
    }

    public static synchronized void rebuildWeekWorkSumProperty(int year, int week) {
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForWeek(year, week)) {
                counter += p.getWorkSumForWeek(year, week);
            }
        }
        AllData.weekWorkSumProperty.set(AllData.intToDouble(counter));
    }


    public static DoubleProperty monthWorkSumPropertyProperty() {
        return monthWorkSumProperty;
    }

    public static synchronized void rebuildMonthWorkSumProperty(int year, int month) {
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForMonth(year, month)) {
                counter += p.getWorkSumForMonth(year, month);
            }
        }
        AllData.monthWorkSumProperty.set(AllData.intToDouble(counter));
    }


    public static DoubleProperty yearWorkSumPropertyProperty() {
        return yearWorkSumProperty;
    }

    public static synchronized void rebuildYearWorkSumProperty(int year) {
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForYear(year)) {
                counter += p.getWorkSumForYear(year);
            }
        }
        AllData.yearWorkSumProperty.set(AllData.intToDouble(counter));
    }



    public static DoubleProperty designerDayWorkSumProperty() {
        return designerDayWorkSumProperty;
    }

    public static synchronized void rebuildDesignerDayWorkSumProperty() {
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(AllUsers.getCurrentUser(), LocalDate.now())) {
                counter += p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), LocalDate.now());
            }
        }
        AllData.designerDayWorkSumProperty.set(AllData.intToDouble(counter));
    }


    public static DoubleProperty designerWeekWorkSumProperty() {
        return designerWeekWorkSumProperty;
    }

    public static synchronized void rebuildDesignerWeekWorkSumProperty(int year, int week) {
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForDesignerAndWeek(AllUsers.getCurrentUser(), year, week)) {
                counter += p.getWorkSumForDesignerAndWeek(AllUsers.getCurrentUser(), year, week);
            }
        }
        AllData.designerWeekWorkSumProperty.set(AllData.intToDouble(counter));
    }

    public static DoubleProperty designerMonthWorkSumProperty() {
        return designerMonthWorkSumProperty;
    }

    public static synchronized void rebuildDesignerMonthWorkSumProperty(int year, int month) {
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForDesignerAndMonth(AllUsers.getCurrentUser(), year, month)) {
                counter += p.getWorkSumForDesignerAndMonth(AllUsers.getCurrentUser(), year, month);
            }
        }
        AllData.designerMonthWorkSumProperty.set(AllData.intToDouble(counter));
    }


    public static DoubleProperty designerYearWorkSumProperty() {
        return designerYearWorkSumProperty;
    }

    public static synchronized void rebuildDesignerYearWorkSumProperty(int year) {
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForDesignerAndYear(AllUsers.getCurrentUser(), year)) {
                counter += p.getWorkSumForDesignerAndYear(AllUsers.getCurrentUser(), year);
            }
        }
        AllData.designerYearWorkSumProperty.set(AllData.intToDouble(counter));
    }

    public static int getWorkSumForDesignerAndPeriod(int designerID, LocalDate from, LocalDate till) {
        int workTime = 0;
        for (Project p : allProjects.values()) {
            workTime += p.getWorkSumForDesignerAndPeriod(designerID, from, till);
        }
        return workTime;
    }



    /** Метод добавления и корректировки рабочего времени в проектах */


    public static synchronized boolean addWorkTime(int projectIDnumber, LocalDate correctDate, int idUser, double newTime) {

        if (!AllUsers.isUserExist(idUser) || AllUsers.isUserDeleted(idUser) || !AllUsers.getOneUser(idUser).getRole().equals(Role.DESIGNER)) {
            return false;
        }

        /*if (correctDate.compareTo(AllData.parseDate(AllData.getAnyProject(projectIDnumber).getDateCreationString())) < 0) {
            return false;
        }*/

        if (isProjectExist(projectIDnumber) && (!isProjectArchive(projectIDnumber))) {

            Project project = getAnyProject(projectIDnumber);
            int difference = project.addWorkTime(correctDate, idUser, newTime);

            addWorkSumProjects(difference);

            LocalDate today = LocalDate.now();

            rebuildTodayWorkSumProperty();
            rebuildWeekWorkSumProperty(today.getYear(), today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
            rebuildMonthWorkSumProperty(today.getYear(), today.getMonthValue());
            rebuildYearWorkSumProperty(today.getYear());

            if (AllData.tableProjectsDesignerController != null) {
                rebuildDesignerDayWorkSumProperty();
                rebuildDesignerWeekWorkSumProperty(today.getYear(), today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                rebuildDesignerMonthWorkSumProperty(today.getYear(), today.getMonthValue());
                rebuildDesignerYearWorkSumProperty(today.getYear());
            }


            AllData.status = "AllData.addWorkTime - Локально добавлено/изменено рабочее время в проекте id-" + projectIDnumber;
            AllData.updateAllStatus();

            WorkTime workTime = AllData.getAnyProject(projectIDnumber).getWorkTimeForDesignerAndDate(idUser, correctDate);

            Updater.update(UpdateType.UPDATE_TIME, workTime);

            return true;
        }
        return false;
    }


    public static synchronized boolean addWorkTimeForImport(int projectIDnumber, LocalDate correctDate, int idUser, double newTime) {

        if (!AllUsers.isUserExist(idUser) || !AllUsers.getOneUser(idUser).getRole().equals(Role.DESIGNER)) {
            return false;
        }


        if (isProjectExist(projectIDnumber)) {

            Project project = getAnyProject(projectIDnumber);
            int difference = project.addWorkTime(correctDate, idUser, newTime);

            addWorkSumProjects(difference);

            WorkTime workTime = AllData.getAnyProject(projectIDnumber).getWorkTimeForDesignerAndDate(idUser, correctDate);

            Updater.update(UpdateType.UPDATE_TIME, workTime);

            return true;
        }
        return false;
    }


    public static boolean containsWorkTime(int projectID, int designerID, LocalDate date) {
        if (allProjects.containsKey(projectID)) {
            return allProjects.get(projectID).containsWorkTime(designerID, date);
        }
        return false;
    }

    public static IntegerProperty designerRatingPositionProperty() {
        return designerRatingPosition;
    }

    public static void rebuildDesignerRatingPosition() {

        Map<Integer, Integer> places = new TreeMap<>(Collections.reverseOrder());

        for (User u : AllUsers.getUsers().values()) {
            int sum = 0;
            if (u.getRole().equals(Role.DESIGNER)) {
                for (Project p : allProjects.values()) {
                    sum += p.getWorkSumForDesignerAndPeriod(u.getIDNumber(), LocalDate.now().minusDays(31), LocalDate.now().minusDays(1));
                }
                places.put(sum, u.getIDNumber());
            }

        }

        List<Integer> listPlaces = new ArrayList<>(places.values());
        int result = listPlaces.indexOf(AllUsers.getCurrentUser());
        
        designerRatingPosition.set(result + 1);
    }



    /** Геттеры активного, неактивного и любого проекта из мапы
     * @return null
     * */

    public static Project getOneActiveProject(int idActiveProject) {
        if (isProjectExist(idActiveProject)) {
            return activeProjects.get(idActiveProject);
        }
        return null;
    }

    public static Project getAnyProject(int idProject) {
        if (isProjectExist(idProject)) {
            return allProjects.get(idProject);
        }
        return null;
    }

    public static Project getOneArchiveProject(int idArchiveProject) {
        if (isProjectArchive(idArchiveProject)) {
            return allProjects.get(idArchiveProject);
        }
        return null;
    }

    public static List<Project> getActiveProjectsForPeriodCreation(LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            LocalDate date = AllData.parseDate(p.getDateCreationString());
            if ((date.compareTo(fromDate) >= 0) && (date.compareTo(tillDate) <= 0)) {
                result.add(p);
            }
        }
        return result;
    }


    public static List<Project> getActiveProjectsForDesignerAndDate(int designerIDnumber, LocalDate workDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, workDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForPeriod(LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForDate(LocalDate argDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(argDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForDesignerAndPeriod(int designerIDnumber, LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForDesignerAndMonth(int designerIDnumber, Year year, Month month) {
        List<Project> result = new ArrayList<>();
        LocalDate fromDate = LocalDate.of(year.getValue(), month.getValue(), 1);
        LocalDate tillDate = LocalDate.of(year.getValue(), month.getValue(), month.length(year.isLeap()));
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForMonth(Year year, Month month) {
        List<Project> result = new ArrayList<>();
        LocalDate fromDate = LocalDate.of(year.getValue(), month.getValue(), 1);
        LocalDate tillDate = LocalDate.of(year.getValue(), month.getValue(), month.length(year.isLeap()));
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }


    public static List<Project> getAllProjectsForMonth(Year year, Month month) {
        List<Project> result = new ArrayList<>();
        LocalDate fromDate = LocalDate.of(year.getValue(), month.getValue(), 1);
        LocalDate tillDate = LocalDate.of(year.getValue(), month.getValue(), month.length(year.isLeap()));
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }




    public static List<Project> getAllProjectsForPeriodCreation(LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : allProjects.values()) {
            LocalDate date = AllData.parseDate(p.getDateCreationString());
            if ((date.compareTo(fromDate) >= 0) && (date.compareTo(tillDate) <= 0)) {
                result.add(p);
            }
        }
        return result;
    }


    public static List<Project> getAllProjectsForDesignerAndDate(int designerIDnumber, LocalDate workDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, workDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getAllProjectsForPeriod(LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getAllProjectsForDate(LocalDate argDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(argDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getAllProjectsForDesignerAndPeriod(int designerIDnumber, LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getAllProjectsForDesignerAndWeek(int designerIDnumber, int year, int week) {
        List<Project> result = new ArrayList<>();
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForDesignerAndWeek(designerIDnumber, year, week)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getAllProjectsForDesignerAndMonth(int designerIDnumber, int year, int month) {
        List<Project> result = new ArrayList<>();
        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate tillDate = LocalDate.of(year, month, fromDate.getMonth().length(Year.from(fromDate).isLeap()));
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getAllProjectsForDesignerAndYear(int designerIDnumber, int year) {
        List<Project> result = new ArrayList<>();
        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDate tillDate = LocalDate.of(year, 12, 31);
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }




    /** Методы добавления, удаления проектов */

    public synchronized static Project createProject(int projectID, String company, String manager, String description, LocalDate newDate) {

        if (company == null || company.isEmpty()) {
            return null;
        }
        if (manager == null || manager.isEmpty()) {
            return null;
        }
        if (description == null || description.isEmpty()) {
            return null;
        }

        if (newDate == null) {
            newDate = LocalDate.now();
        }

        Project project = new Project(projectID, company, manager, description, newDate);

        allProjects.put(project.getIdNumber(), project);
        activeProjects.put(project.getIdNumber(), project);

        AllData.status = "Локально создан новый проект id-" + project.getIdNumber();
        AllData.updateAllStatus();
        AllData.logger.info(AllData.status);

        Updater.update(UpdateType.CREATE_PROJECT, project);

        return project;
    }




    public synchronized static boolean deleteProject(int deadProject) {
        if (isProjectExist(deadProject)) {
            int deleteWorkTime = allProjects.get(deadProject).getWorkSum();
            allProjects.remove(deadProject);
            activeProjects.remove(deadProject);

            // Удаляем время из общего суммарного
            int tmp = workSumProjects.get();
            tmp -= deleteWorkTime;
            if (tmp < 0) {
                tmp = 0;
            }
            workSumProjects.set(tmp);


            AllData.status = "Локально проект id-" + deadProject + " удален безвозвратно.";
            updateAllStatus();
            AllData.logger.info(AllData.status);

            Updater.update(UpdateType.DELETE_PROJECT, deadProject);

            return true;
        }
        return false;
    }

    public static synchronized void changeProjectArchiveStatus(int changedProject, boolean projectIsArchive) {
        if (isProjectExist(changedProject)) {
            Project chProject = allProjects.get(changedProject);

            if (chProject.isArchive() != projectIsArchive) {
                chProject.setArchive(projectIsArchive);

                if (projectIsArchive) {
                    activeProjects.remove(changedProject);
                    AllData.status = "Локально проект id-" + changedProject + " переведен в архив.";
                }
                else {
                    activeProjects.put(changedProject, chProject);
                    AllData.status = "Локально проект id-" + changedProject + " возвращен из архива.";
                }

                if (editProjectWindowControllers.containsKey(changedProject)) {
                    editProjectWindowControllers.get(changedProject).initializeArchiveCheckBox();
                }

                updateAllStatus();
                AllData.logger.info(AllData.status);
                Updater.update(UpdateType.UPDATE_PROJECT, chProject);
            }
        }
    }

    public static synchronized void updateAllWindows() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (AllData.tableProjectsManagerController != null) {
                    AllData.tableProjectsManagerController.initialize();
                }
                if (AllData.tableProjectsDesignerController != null) {
                    AllData.tableProjectsDesignerController.initialize();
                }
                if (AllData.adminWindowController != null) {
                    AllData.adminWindowController.updateAdmin();
                }
                // TODO сюда добавить сюрвеора, когда будет написан
                if (AllData.statisticManagerWindowController != null) {
                    AllData.statisticManagerWindowController.initialize();
                }
                if (AllData.statisticWindowController != null) {
                    AllData.statisticWindowController.initialize();
                }
                if (AllData.staffWindowController != null) {
                    AllData.staffWindowController.initializeTable();
                }
                if (!AllData.editProjectWindowControllers.isEmpty()) {
                    for (EditProjectWindowController controller : AllData.editProjectWindowControllers.values()) {
                        if (controller != null) {
                            controller.updateProject();
                        }
                    }
                }
                if (!AllData.infoProjectWindowControllers.isEmpty()) {
                    for (InfoProjectWindowController controller : AllData.infoProjectWindowControllers.values()) {
                        if (controller != null) {
                            controller.updateProject();
                        }
                    }
                }
            }
        });
    }

    public static synchronized void updateAllStatus(String stat) {
        updateTimeStamp();
        AllData.status = timeStamp + stat;
        updateAllStatus();
    }


    private static void updateAllStatus() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (AllData.adminWindowController != null) {
                    AllData.adminWindowController.updateStatus();
                }
                if (AllData.tableProjectsManagerController != null) {
                    AllData.tableProjectsManagerController.updateStatus();
                }
                if (AllData.tableProjectsDesignerController != null) {
                    AllData.tableProjectsDesignerController.updateStatus();
                }
                // TODO сюда добавить сюрвеора, когда будет написан
            }
        });
    }



    public static void updateTimeStamp() {
        timeStamp = formatDateTime(LocalDateTime.now()) + " - ";
    }

    public static void resetStatus() {
        updateTimeStamp();
        AllData.status = "Все нормально";
    }



    /** Методы проверки существования проекта в списке
     * и его проверки на архивное состояние */

    public static boolean isProjectExist(int idProject) {
        if (idProject <= 0) {
            return false;
        }
        return allProjects.containsKey(idProject);
    }

    // Перед применением данного метода ВСЕГДА сначала вызывать isProjectExist(int idProject),
    // то есть проверять проект на существование
    // Проверку сюда вставлять нельзя, т.к. при отсутствии непонятно, что возвращать
    public static boolean isProjectArchive(int idProject) {
        return allProjects.get(idProject).isArchive();
    }



    /** Метод сверки и синхронизации списков и поля суммарного времени */
    public static synchronized void rebuildActiveProjects() {
        Map<Integer, Project> newActiveProjects = new HashMap<>();
        allProjects.forEach((k,v)-> {
            if (!v.isArchive()) {
                newActiveProjects.put(k, v);
            }
        });
        activeProjects.clear();
        activeProjects.putAll(newActiveProjects);
    }


    public static synchronized void deleteZeroTime() {
        for (Project p : activeProjects.values()) {
            Iterator<WorkTime> iter = p.getWork().iterator();
            while (iter.hasNext()) {
                WorkTime wt = iter.next();
                if (wt.getTime() == 0) {
                    iter.remove();
                }
            }
        }
    }

    public static synchronized void deleteZeroTime(int projectIDnumber) {
        Iterator<WorkTime> iter = AllData.getAnyProject(projectIDnumber).getWork().iterator();
        while (iter.hasNext()) {
            WorkTime wt = iter.next();
            if (wt.getTime() == 0) {
                iter.remove();
            }
        }
    }

    public static void rebuildEditProjectsControllers() {
        Iterator<Map.Entry<Integer, EditProjectWindowController>> iter = editProjectWindowControllers.entrySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().getValue() == null) {
                iter.remove();
            }
        }
    }



    /** методы-утилиты */

    public static int doubleToInt(double argument) {
        double tmp = argument * 10;
        return (int) formatDouble(tmp, 0);
    }

    public static double intToDouble(int argument) {
        double tmp = (double) argument;
        double tmp2 = tmp / 10;
        BigDecimal result = new BigDecimal(Double.toString(tmp2));
        result = result.setScale(1, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    public static double formatDouble(double argDouble, int scale) {
        BigDecimal result = new BigDecimal(Double.toString(argDouble));
        result = result.setScale(scale, RoundingMode.HALF_UP);
        return result.doubleValue();
    }


    public static String formatWorkTime(Double timeDouble) {

        if (timeDouble == 0d) {
            return "-";
        }

        String result = String.valueOf(timeDouble);
        result = result.replaceAll("\\.", ",");
        result = result.replaceAll(",0", "");
        return result;
    }

    public static String formatHours(String input) {

        if (input.endsWith(".0")) {
            input = input.replaceAll("\\.0", "");
        }

        if (input.endsWith("0") || input.endsWith("11") || input.endsWith("12") || input.endsWith("13") || input.endsWith("14")) {
            return "часов";
        }

        if (input.endsWith(".1") || input.endsWith(".2") || input.endsWith(".3") || input.endsWith(".4") || input.endsWith(".5")
                || input.endsWith(".6") || input.endsWith(".7") || input.endsWith(".8") || input.endsWith(".9")) {
            return "часа";
        }
        if (input.endsWith("1")) {
            return "час";
        }
        else if (input.endsWith("2") || input.endsWith("3") || input.endsWith("4")) {
            return "часа";
        }

        return "часов";
    }

    public static String formatStringInputDouble(String oldText, String input, int scale) {

        if (input == null || input.isEmpty() || input.equals("0") || input.equals("-")) {
            return "-";
        }

        String newText = input.replaceAll(" ", ".");
        newText = newText.replaceAll("-", ".");
        newText = newText.replaceAll(",", ".");
        newText = newText.replaceAll("=", ".");

        Double newTimeDouble = null;
        try {
            newTimeDouble = Double.parseDouble(newText);
        } catch (NumberFormatException e) {
            return oldText;
        }
        return String.valueOf(AllData.formatDouble(newTimeDouble, scale));
    }

    public static Double getDoubleFromText(double current, String input, int scale) {

        if (input == null || input.isEmpty() || input.equals("0") || input.equals("-")) {
            return 0d;
        }

        String newText = input.replaceAll(" ", ".");
        newText = newText.replaceAll("-", ".");
        newText = newText.replaceAll(",", ".");
        newText = newText.replaceAll("=", ".");

        Double newTimeDouble = null;
        try {
            newTimeDouble = Double.parseDouble(newText);
        } catch (NumberFormatException e) {
            return current;
        }
        return AllData.formatDouble(newTimeDouble, scale);
    }



    public static int getIntFromText(int current, String input) {

        if (input == null || input.isEmpty() || input.equals("0") || input.equals("-")) {
            return 0;
        }

        String corrected = input.replaceAll(",", "");
        corrected = corrected.replaceAll("\\.", "");
        corrected = corrected.replaceAll(" ", "");
        corrected = corrected.replaceAll("-", "");

        Integer result = null;
        try {
            result = Integer.parseInt(corrected);
        } catch (NumberFormatException e) {
            return current;
        }

        return result;
    }



    public static String formatStringInputInteger(String oldText, String input) {

        if (input == null || input.isEmpty() || input.equals("0") || input.equals("-")) {
            return "-";
        }

        String corrected = input.replaceAll(",", "");
        corrected = corrected.replaceAll("\\.", "");
        corrected = corrected.replaceAll(" ", "");
        corrected = corrected.replaceAll("-", "");

        Integer newInt = null;
        try {
            newInt = Integer.parseInt(corrected);
        } catch (NumberFormatException e) {
            return oldText;
        }
        List<Character> listChars = new ArrayList<>();
        char[] inp = String.valueOf(newInt).toCharArray();

        for (char c : inp) {
            listChars.add(c);
        }

        for (int i = listChars.size(); i > 0; i -= 3) {
            if (i != listChars.size()) {
                listChars.add(i, '.');
            }
        }
        char[] res = new char[listChars.size()];
        for (int j = 0; j < listChars.size(); j++) {
            res[j] = listChars.get(j);
        }
        return String.valueOf(res);
    }

    public static String formatInputInteger(Integer input) {

        if (input == null || input == 0) {
            return "-";
        }

        String inputString = String.valueOf(input);

        List<Character> listChars = new ArrayList<>();
        char[] inp = inputString.toCharArray();

        for (char c : inp) {
            listChars.add(c);
        }

        for (int i = listChars.size(); i > 0; i -= 3) {
            if (i != listChars.size()) {
                listChars.add(i, '.');
            }
        }
        char[] res = new char[listChars.size()];
        for (int j = 0; j < listChars.size(); j++) {
            res[j] = listChars.get(j);
        }
        return String.valueOf(res);
    }


    public static Integer parseMoney(Integer oldValue, String input) {

        if (input == null || input.isEmpty() || input.equals("0") || input.equals("-")) {
            return 0;
        }

        String corrected = input.replaceAll(",", "");
        corrected = corrected.replaceAll("\\.", "");
        corrected = corrected.replaceAll(" ", "");
        corrected = corrected.replaceAll("-", "");
        if (corrected.equals("0")) {
            return 0;
        }

        Integer newValue = null;
        try {
            newValue = Integer.parseInt(corrected);
        } catch (NumberFormatException e) {
            return oldValue;
        }
        return newValue;
    }


    public static List<WorkDay> convertWorkTimesToWorkDays(List<WorkTime> workTimeList) {
        List<WorkDay> result = new ArrayList<>();
        if (workTimeList == null || workTimeList.isEmpty()) {
            return result;
        }

        Map<LocalDate, WorkDay> sortedMap = new TreeMap<>(Collections.reverseOrder());
        for (WorkTime wt : workTimeList) {
            if (sortedMap.containsKey(wt.getDate())) {
                WorkDay workDay = sortedMap.get(wt.getDate());
                workDay.addWorkTime(wt.getDesignerID(), wt.getTimeDouble());
                sortedMap.put(wt.getDate(), workDay);
            }
            else {
                WorkDay workDay = new WorkDay(wt.getDate());
                workDay.addWorkTime(wt.getDesignerID(), wt.getTimeDouble());
                sortedMap.put(wt.getDate(), workDay);
            }
        }
        if (!sortedMap.isEmpty()) {
            result.addAll(sortedMap.values());
        }
        return result;
    }



    /** Форматировщик даты.
     * @return null
     * */
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    //private static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSS");




    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    public static LocalDate parseDate(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (TIME_FORMATTER == null) {
            TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSS");
        }

        if (dateTime == null) {
            return TIME_FORMATTER.format(LocalDateTime.now());
        }
        return TIME_FORMATTER.format(dateTime);
        //return time.toString();
    }

    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (TIME_FORMATTER == null) {
            TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSS");
        }
        try {
            return TIME_FORMATTER.parse(dateTimeString, LocalDateTime::from);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validDate(String dateString) {
        return parseDate(dateString) != null;
    }

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM", Locale.getDefault());

}
