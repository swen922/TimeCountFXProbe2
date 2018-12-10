package com.horovod.timecountfxprobe.project;

import com.horovod.timecountfxprobe.view.EditProjectWindowController;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import com.horovod.timecountfxprobe.view.EditProjectWindowController;
import com.horovod.timecountfxprobe.view.StatisticWindowController;
import com.horovod.timecountfxprobe.view.TableProjectsDesignerController;
import com.horovod.timecountfxprobe.view.TableProjectsManagerController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AllData {

    private static volatile AtomicInteger idNumber = new AtomicInteger(0);
    private static volatile IntegerProperty idNumberProperty = new SimpleIntegerProperty(idNumber.get());

    private static ObservableMap<Integer, Project> allProjects = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());
    private static ObservableMap<Integer, Project> activeProjects = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());

    private static volatile AtomicInteger workSumProjects = new AtomicInteger(0);
    private static volatile IntegerProperty workSumProjectsProperty = new SimpleIntegerProperty(workSumProjects.get());

    private static DoubleProperty todayWorkSumProperty = new SimpleDoubleProperty(0);
    private static DoubleProperty yesterdayWorkSumProperty = new SimpleDoubleProperty(0);
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



    /** Поле нужно, чтобы передавать его отдельным нитям (см. класс TestBackgroundUpdate01)
     * */

    /** !!!!!! В чистовой версии загнать сюда ВСЕ статические экземпляры контролеров и нодов!
     * А из MainApp убрать их !!!!!
     * И решить по всем единообразно: либо через геттеры-сеттеры доступ, либо public сделать (второе экономней по коду)
     * !!!!!!!!*/

    private static TableProjectsDesignerController tableProjectsDesignerController;
    private static StatisticWindowController statisticWindowController;
    private static BorderPane rootLayout;
    private static Stage statStage;

    public static TableProjectsManagerController tableProjectsManagerController;
    public static EditProjectWindowController editProjectWindowController;



    /** Стандартные геттеры и сеттеры */

    public static int incrementIdNumberAndGet() {
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
    }


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

    public static synchronized void setActiveProjects(ObservableMap<Integer, Project> newActiveProjects) {
        AllData.activeProjects = newActiveProjects;
    }



    public static int getWorkSumProjects() {
        return workSumProjects.get();
    }

    public static synchronized void setWorkSumProjects(int newWorkSumProjects) {
        AllData.workSumProjects.set(newWorkSumProjects);
        AllData.workSumProjectsProperty().set(newWorkSumProjects);
    }

    public static synchronized void addWorkSumProjects(int addTime) {
        AllData.workSumProjects.addAndGet(addTime);
        AllData.workSumProjectsProperty().set(workSumProjects.get());
    }

    public static int getWorkSumProjectsProperty() {
        return workSumProjectsProperty.get();
    }

    public static IntegerProperty workSumProjectsProperty() {
        return workSumProjectsProperty;
    }

    public static void setWorkSumProjectsProperty(int newWorkSumProjectsProperty) {
        AllData.workSumProjectsProperty.set(newWorkSumProjectsProperty);
    }


    public static double getTodayWorkSumProperty() {
        return todayWorkSumProperty.get();
    }

    public static DoubleProperty todayWorkSumProperty() {
        return todayWorkSumProperty;
    }

    public static void setTodayWorkSumProperty(double todayWorkSumProperty) {
        AllData.todayWorkSumProperty.set(todayWorkSumProperty);
    }


    public static synchronized void rebuildTodayWorkSumProperty() {
        AllData.todayWorkSumProperty.set(0);
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(LocalDate.now())) {
                counter += p.getWorkSumForDate(LocalDate.now());
            }
        }
        AllData.todayWorkSumProperty.set(AllData.intToDouble(counter));
    }


    public static double getYesterdayWorkSumProperty() {
        return yesterdayWorkSumProperty.get();
    }

    public static DoubleProperty yesterdayWorkSumProperty() {
        return yesterdayWorkSumProperty;
    }

    public static void setYesterdayWorkSumProperty(double yesterdayWorkSumProperty) {
        AllData.yesterdayWorkSumProperty.set(yesterdayWorkSumProperty);
    }

    public static synchronized void rebuildYesterdayWorkSumProperty() {
        AllData.yesterdayWorkSumProperty.set(0);
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(LocalDate.now().minusDays(1))) {
                counter += p.getWorkSumForDate(LocalDate.now().minusDays(1));
            }
        }
        AllData.yesterdayWorkSumProperty.set(AllData.intToDouble(counter));
    }



    public static double getWeekWorkSumProperty() {
        return weekWorkSumProperty.get();
    }

    public static DoubleProperty weekWorkSumPropertyProperty() {
        return weekWorkSumProperty;
    }

    public static void setWeekWorkSumProperty(double weekWorkSumProperty) {
        AllData.weekWorkSumProperty.set(weekWorkSumProperty);
    }

    public static double getMonthWorkSumProperty() {
        return monthWorkSumProperty.get();
    }

    public static DoubleProperty monthWorkSumPropertyProperty() {
        return monthWorkSumProperty;
    }

    public static void setMonthWorkSumProperty(double monthWorkSumProperty) {
        AllData.monthWorkSumProperty.set(monthWorkSumProperty);
    }

    public static double getYearWorkSumProperty() {
        return yearWorkSumProperty.get();
    }

    public static DoubleProperty yearWorkSumPropertyProperty() {
        return yearWorkSumProperty;
    }

    public static void setYearWorkSumProperty(double yearWorkSumProperty) {
        AllData.yearWorkSumProperty.set(yearWorkSumProperty);
    }




    public static double getDesignerDayWorkSumProperty() {
        return designerDayWorkSumProperty.get();
    }

    public static DoubleProperty designerDayWorkSumProperty() {
        return designerDayWorkSumProperty;
    }

    public static synchronized void setDesignerDayWorkSumProperty(double day) {
        AllData.designerDayWorkSumProperty.set(day);
    }

    public static synchronized void rebuildDesignerDayWorkSumProperty() {
        AllData.designerDayWorkSumProperty.set(0);
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(AllUsers.getCurrentUser(), LocalDate.now())) {
                counter += p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), LocalDate.now());
            }
        }
        AllData.designerDayWorkSumProperty.set(AllData.intToDouble(counter));
    }


    public static double getDesignerWeekWorkSumProperty() {
        return designerWeekWorkSumProperty.get();
    }

    public static DoubleProperty designerWeekWorkSumProperty() {
        return designerWeekWorkSumProperty;
    }

    public static synchronized void setDesignerWeekWorkSumProperty(double newDesignerWeekWorkSumProperty) {
        AllData.designerWeekWorkSumProperty.set(newDesignerWeekWorkSumProperty);
    }

    public static synchronized void rebuildDesignerWeekWorkSumProperty(int year, int week) {
        AllData.designerWeekWorkSumProperty.set(0);
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForDesignerAndWeek(AllUsers.getCurrentUser(), year, week)) {
                counter += p.getWorkSumForDesignerAndWeek(AllUsers.getCurrentUser(), year, week);
            }
        }
        AllData.designerWeekWorkSumProperty.set(AllData.intToDouble(counter));
    }


    public static double getDesignerMonthWorkSumProperty() {
        return designerMonthWorkSumProperty.get();
    }

    public static DoubleProperty designerMonthWorkSumProperty() {
        return designerMonthWorkSumProperty;
    }

    public static synchronized void setDesignerMonthWorkSumProperty(double newDesignerMonthWorkSumProperty) {
        AllData.designerMonthWorkSumProperty.set(newDesignerMonthWorkSumProperty);
    }

    public static synchronized void rebuildDesignerMonthWorkSumProperty(int year, int month) {
        AllData.designerMonthWorkSumProperty().set(0);
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForDesignerAndMonth(AllUsers.getCurrentUser(), year, month)) {
                counter += p.getWorkSumForDesignerAndMonth(AllUsers.getCurrentUser(), year, month);
            }
        }
        AllData.designerMonthWorkSumProperty.set(AllData.intToDouble(counter));
    }

    public static double getDesignerYearWorkSumProperty() {
        return designerYearWorkSumProperty.get();
    }

    public static DoubleProperty designerYearWorkSumProperty() {
        return designerYearWorkSumProperty;
    }

    public static void setDesignerYearWorkSumProperty(double newDesignerYearWorkSumProperty) {
        AllData.designerYearWorkSumProperty.set(newDesignerYearWorkSumProperty);
    }

    public static synchronized void rebuildDesignerYearWorkSumProperty(int year) {
        AllData.designerYearWorkSumProperty().set(0);
        int counter = 0;
        for (Project p : allProjects.values()) {
            if (p.containsWorkTimeForDesignerAndYear(AllUsers.getCurrentUser(), year)) {
                counter += p.getWorkSumForDesignerAndYear(AllUsers.getCurrentUser(), year);
            }
        }
        AllData.designerYearWorkSumProperty.set(AllData.intToDouble(counter));
    }

    public static int getDesignerRatingPosition() {
        return designerRatingPosition.get();
    }

    public static IntegerProperty designerRatingPositionProperty() {
        return designerRatingPosition;
    }

    public static synchronized void setDesignerRatingPosition(int newDesignerRatingPosition) {
        AllData.designerRatingPosition.set(newDesignerRatingPosition);
    }

    public static void rebuildDesignerRatingPosition() {

        designerRatingPosition.set(0);
        Map<Integer, Integer> places = new TreeMap<>(Collections.reverseOrder());

        for (User u : AllUsers.getUsers().values()) {
            int sum = 0;
            if(u.getRole().equals(Role.DESIGNER)) {
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






    public static TableProjectsDesignerController getTableProjectsDesignerController() {
        return tableProjectsDesignerController;
    }

    public static synchronized void setTableProjectsDesignerController(TableProjectsDesignerController newTableProjectsDesignerController) {
        AllData.tableProjectsDesignerController = newTableProjectsDesignerController;
    }

    public static StatisticWindowController getStatisticWindowController() {
        return statisticWindowController;
    }

    public static void setStatisticWindowController(StatisticWindowController statisticWindowController) {
        AllData.statisticWindowController = statisticWindowController;
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    public static synchronized void setRootLayout(BorderPane newRootLayout) {
        AllData.rootLayout = newRootLayout;
    }

    public static Stage getStatStage() {
        return statStage;
    }

    public static void setStatStage(Stage statStage) {
        AllData.statStage = statStage;
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

    public static List<Project> getActiveProjectsForPeriodWorking(LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForDesignerAndPeriodWorking(int designerIDnumber, LocalDate fromDate, LocalDate tillDate) {
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

    public static List<Project> getAllProjectsForPeriodWorking(LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getAllProjectsForDesignerAndPeriodWorking(int designerIDnumber, LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getAllProjectsForDesignerAndMonth(int designerIDnumber, Year year, Month month) {
        List<Project> result = new ArrayList<>();
        LocalDate fromDate = LocalDate.of(year.getValue(), month.getValue(), 1);
        LocalDate tillDate = LocalDate.of(year.getValue(), month.getValue(), month.length(year.isLeap()));
        for (Project p : allProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }








    /** Методы добавления, удаления проектов */

    public synchronized static boolean addNewProject(Project newProject) {
        if (!isProjectExist(newProject.getIdNumber())) {
            allProjects.put(newProject.getIdNumber(), newProject);
            activeProjects.put(newProject.getIdNumber(), newProject);

            // Добавляем время в общее суммарное
            int tmp = workSumProjects.get();
            tmp += newProject.getWorkSum();
            workSumProjects.set(tmp);

            return true;
        }
        return false;
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

            return true;
        }
        return false;
    }

    public static synchronized void changeProjectArchiveStatus(int changedProject, boolean projectIsArchive) {
        if (isProjectExist(changedProject)) {
            Project chProject = allProjects.get(changedProject);
            chProject.setArchive(projectIsArchive);

            if (projectIsArchive) {
                activeProjects.remove(changedProject);
            }
            else if (!projectIsArchive) {
                activeProjects.put(changedProject, chProject);
            }
        }
    }


    /** Методы проверки существования проекта в списке
     * и его проверки на архивное состояние */

    public static boolean isProjectExist(int idProject) {
        if (idProject <= 0 || idProject > getIdNumber()) {
            return false;
        }
        return allProjects.containsKey(idProject);
    }

    // Перед применением данного метода ВСЕГДА сначала вызывать isProjectExist(int idProject),
    // то есть проверять проект на существование
    public static boolean isProjectArchive(int idProject) {
        return allProjects.get(idProject).isArchive();
    }


    /** Метод добавления и корректировки рабочего времени в проектах */

    public static synchronized boolean addWorkTime(int projectIDnumber, LocalDate correctDate, int idUser, double newTime) {

        if (isProjectExist(projectIDnumber) && (!isProjectArchive(projectIDnumber))) {
            Project project = getOneActiveProject(projectIDnumber);

            int difference = project.addWorkTime(correctDate, idUser, newTime);
            addWorkSumProjects(difference);

            if (idUser == AllUsers.getCurrentUser() && correctDate.equals(LocalDate.now())) {

                int old = AllData.doubleToInt(designerDayWorkSumProperty.get());

                designerDayWorkSumProperty.set(AllData.intToDouble(old + difference));
            }

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

    public static synchronized int computeWorkSum() {
        int result = 0;

        for (Project p : allProjects.values()) {
            result += p.getWorkSum();
        }
        workSumProjects.set(result);
        return result;
    }

    public static synchronized void computeProjectsProperties() {
        Collection<Project> coll = activeProjects.values();
        for (Project p : coll) {
            p.setIdNumberProperty(p.getIdNumber());
            p.setCompanyProperty(p.getCompany());
            p.setInitiatorProperty(p.getInitiator());
            p.setDescriptionProperty(p.getDescription());
            p.setWorkSumProperty(String.valueOf(p.getWorkSumDouble()));
        }
    }


    public static void deleteZeroTime() {
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



    /** методы-утилиты */

    public static int doubleToInt(double argument) {
        return (int) (argument * 10);
    }

    public static double intToDouble(int argument) {
        double tmp = (double) argument / 10;
        BigDecimal result = new BigDecimal(Double.toString(tmp));
        result = result.setScale(1, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    public static double formatDouble(double argDouble) {
        BigDecimal result = new BigDecimal(Double.toString(argDouble));
        result = result.setScale(1, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    public static String formatWorkTime(Double timeDouble) {
        String result = String.valueOf(timeDouble);
        result = result.replaceAll("\\.", ",");
        result = result.replaceAll(",0", "");
        return result;
    }

    public static String formatHours(String input) {
        if (input.endsWith("11") || input.endsWith("12") || input.endsWith("13") || input.endsWith("14")) {
            return "часов";
        }
        if (input.endsWith(".0")) {
            return "часов";
        }
        if (input.endsWith(".1") || input.endsWith(".2") || input.endsWith(".3") || input.endsWith(".4")) {
            return "часа";
        }
        if (input.endsWith("1")) {
            return "час";
        }
        else if (input.endsWith("2") || input.endsWith("3") || input.endsWith("4")) {
            return "часа";
        }
        else if (!input.contains(",") && !input.contains(".")) {
            return "часов";
        }
        return "часа";
    }



    /** Форматировщик даты.
     * @return null
     * */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

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

    public static boolean validDate(String dateString) {
        return parseDate(dateString) != null;
    }

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM", Locale.getDefault());


}
