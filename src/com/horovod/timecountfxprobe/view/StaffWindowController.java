package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkDay;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import com.sun.jdi.IntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.temporal.WeekFields;
import java.util.*;

public class StaffWindowController {

    @FXML
    private ChoiceBox<String> workersChoiceBox;

    @FXML
    private CheckBox designersOnlyCheckBox;

    @FXML
    private CheckBox retiredCheckBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker tillDatePicker;

    @FXML
    private Button clearDatePickerButton;


    @FXML
    private TableView<DesignerBase> tableDesigners;

    @FXML
    private TableColumn<DesignerBase, String> designersColumn;

    ObservableList<DesignerBase> designerBaseList;

    @FXML
    public void initialize() {

        LocalDate today = LocalDate.now();
        Year year = Year.from(today);
        LocalDate fromDate = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
        LocalDate tillDate = LocalDate.of(today.getYear(), today.getMonthValue(), today.getMonth().length(year.isLeap()));

        initDatePickers(fromDate, tillDate);

    }


    public void initializeTable(LocalDate from, LocalDate till) {
        if (designerBaseList == null) {
            designerBaseList = FXCollections.observableArrayList();
        }
        designerBaseList.clear();

        List<Project> projectList = AllData.getAllProjectsForPeriodWorking(from, till);
        List<WorkTime> workTimeList = new ArrayList<>();
        for (Project p : projectList) {
            workTimeList.addAll(p.getWorkTimeForPeriod(from, till));
        }

        List<User> listUsers = new ArrayList<>();

        for (User u : AllUsers.getUsers().values()) {
            if (!AllUsers.isUserDeleted(u.getIDNumber())) {
                if (u.getRole().equals(Role.DESIGNER)) {
                    listUsers.add(u);
                }
            }
        }

        for (User u : listUsers) {
            for (WorkTime wt : workTimeList) {
                DesignerBase db = new DesignerBase(u.getIDNumber(), from, till);
                db.addWorkTime(wt.getDate(), wt.getTimeDouble());
                designerBaseList.add(db);
            }
        }



    }

    public void initDatePickers(LocalDate from, LocalDate till) {
        if (fromDatePicker == null && tillDatePicker == null) {
            fromDatePicker.setValue(from);
            tillDatePicker.setValue(till);
        }
    }


    class DesignerBase {
        private int designerID;
        private LocalDate fromDate;
        private LocalDate tillDate;
        private Map<LocalDate, Double> workSumMap = new HashMap<>();

        public DesignerBase(int designerID, LocalDate fromDate, LocalDate tillDate) {
            this.designerID = designerID;
            this.fromDate = fromDate;
            this.tillDate = tillDate;
        }

        public int getDesignerID() {
            return designerID;
        }

        public synchronized void setDesignerID(int designerID) {
            this.designerID = designerID;
        }

        public LocalDate getFromDate() {
            return fromDate;
        }

        public synchronized void setFromDate(LocalDate fromDate) {
            this.fromDate = fromDate;
        }

        public LocalDate getTillDate() {
            return tillDate;
        }

        public synchronized void setTillDate(LocalDate tillDate) {
            this.tillDate = tillDate;
        }

        public Map<LocalDate, Double> getWorkSumMap() {
            return workSumMap;
        }

        public synchronized void setWorkSumMap(Map<LocalDate, Double> workSumMap) {
            this.workSumMap = workSumMap;
        }



        public void addWorkTime(LocalDate day, double workSum) {
            if (day.compareTo(fromDate) >= 0 && day.compareTo(tillDate) <= 0) {
                if (workSumMap.containsKey(day)) {
                    double current = workSumMap.get(day);
                    workSumMap.put(day, (current + workSum));
                }
                else {
                    workSumMap.put(day, workSum);
                }
            }
        }

        public double getWorkSumForDay(LocalDate date) {
            if (workSumMap.get(date) == null) {
                return 0;
            }
            return workSumMap.get(date);
        }

    } // конец класса DesignerBase




}
