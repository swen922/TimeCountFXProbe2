package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkDay;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import com.sun.jdi.IntegerValue;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;

public class StaffWindowController {

    private Stage myStage;
    private MainApp mainApp;

    private ObservableList<String> listUsers;
    private ObservableList<Integer> yearsValues;
    private ObservableList<Month> monthsValues;
    private ObservableList<UserBase> userBaseList;
    private ObservableList<TableColumn<UserBase, String>> columns;

    private String allWorkers = "Все работники";


    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    @FXML
    private ChoiceBox<String> usersChoiceBox;

    @FXML
    private ChoiceBox<Month> monthChoiceBox;

    @FXML
    private ChoiceBox<Integer> yearsChoiceBox;

    @FXML
    private CheckBox designersOnlyCheckBox;

    @FXML
    private CheckBox includeRetiredsCheckBox;

    @FXML
    private RadioButton daysRadioButton;

    @FXML
    private RadioButton monthsRadioButton;

    @FXML
    private RadioButton timeRadioButton;

    @FXML
    private RadioButton moneyRadioButton;

    @FXML
    private TableView<UserBase> tableUsers;

    /*@FXML
    private TableColumn<UserBase, String> designersColumn;*/



    @FXML
    public void initialize() {

        LocalDate today = LocalDate.now();
        Year year = Year.from(today);
        LocalDate fromDate = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
        LocalDate tillDate = LocalDate.of(today.getYear(), today.getMonthValue(), today.getMonth().length(year.isLeap()));

        // Этот пункт надо отрабатывать только один раз, при запуске программы,
        // поэтому во время работы initialize() запускать нельзя
        // вместо него – initializeStaff()
        designersOnlyCheckBox.setSelected(true);

        initUsersChoiceBox();
        initYearChoiceBox();
        initMonthChoiceBox();

        ToggleGroup daysMonthsGroup = new ToggleGroup();
        daysRadioButton.setToggleGroup(daysMonthsGroup);
        monthsRadioButton.setToggleGroup(daysMonthsGroup);
        daysRadioButton.setSelected(true);
        ToggleGroup timeMoneyGroup = new ToggleGroup();
        timeRadioButton.setToggleGroup(timeMoneyGroup);
        moneyRadioButton.setToggleGroup(timeMoneyGroup);
        timeRadioButton.setSelected(true);

        initializeTable();

    }


    /** Этот метод – чтобы запускать его вместо initialize() во время работы системы
     * не затрагивая полей, которые не надо трогать */
    public void initializeStaff() {


    }

    private void initUsersChoiceBox() {
        if (listUsers == null) {
            listUsers = FXCollections.observableArrayList();
        }
        listUsers.clear();

        if (designersOnlyCheckBox.isSelected() && !includeRetiredsCheckBox.isSelected()) {
            for (User u : AllUsers.getUsers().values()) {
                if (u.getRole().equals(Role.DESIGNER)) {
                    listUsers.add(u.getFullName());
                }
            }
        }
        else if (designersOnlyCheckBox.isSelected() && includeRetiredsCheckBox.isSelected()) {
            for (User u : AllUsers.getUsers().values()) {
                if (u.getRole().equals(Role.DESIGNER)) {
                    listUsers.add(u.getFullName());
                }
            }
            for (User u : AllUsers.getDeletedUsers().values()) {
                if (u.getRole().equals(Role.DESIGNER)) {
                    listUsers.add(u.getFullName());
                }
            }
        }
        else if (!designersOnlyCheckBox.isSelected() && !includeRetiredsCheckBox.isSelected()) {
            for (User u : AllUsers.getUsers().values()) {
                listUsers.add(u.getFullName());
            }
        }
        else if (!designersOnlyCheckBox.isSelected() && includeRetiredsCheckBox.isSelected()) {
            for (User u : AllUsers.getUsers().values()) {
                listUsers.add(u.getFullName());
            }
            for (User u : AllUsers.getDeletedUsers().values()) {
                listUsers.add(u.getFullName());
            }
        }

        listUsers.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        usersChoiceBox.setItems(listUsers);
        usersChoiceBox.getItems().add(0, allWorkers);
        if (usersChoiceBox.getValue() == null) {
            usersChoiceBox.setValue(allWorkers);
        }

        usersChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initializeTable();
            }
        });
    }






    public void initYearChoiceBox() {
        if (yearsValues == null) {
            yearsValues = FXCollections.observableArrayList();
        }
        yearsValues.clear();
        for (int i = 2013; i <= LocalDate.now().getYear(); i++) {
            yearsValues.add(i);
        }
        yearsValues.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o2, o1);
            }
        });
        yearsChoiceBox.setItems(yearsValues);
        if (yearsChoiceBox.getValue() == null) {
            yearsChoiceBox.setValue(LocalDate.now().getYear());
        }
    }

    public void initMonthChoiceBox() {
        if (monthsValues == null) {
            monthsValues = FXCollections.observableArrayList();
        }
        monthChoiceBox.setConverter(new StringConverter<Month>() {
            @Override
            public String toString(Month object) {
                return object.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
            }

            @Override
            public Month fromString(String string) {
                return null;
            }
        });
        monthsValues.addAll(Arrays.asList(
                Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE,
                Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER
        ));
        monthChoiceBox.setItems(monthsValues);
        monthChoiceBox.setValue(LocalDate.now().getMonth());
    }





    public void initializeTable() {
        if (daysRadioButton.isSelected() && timeRadioButton.isSelected()) {
            initializeTableDailyTime();
        }
    }

    private Map<Integer, User> getUsers() {
        Map<Integer, User> result = new HashMap<>();

        if (designersOnlyCheckBox.isSelected() && !includeRetiredsCheckBox.isSelected()) {
            result.putAll(AllUsers.getDesigners());
        }
        else if (designersOnlyCheckBox.isSelected() && includeRetiredsCheckBox.isSelected()) {
            result.putAll(AllUsers.getDesignersPlusDeleted());
        }
        else if (!designersOnlyCheckBox.isSelected() && !includeRetiredsCheckBox.isSelected()) {
            result.putAll(AllUsers.getUsers());
        }
        else if (!designersOnlyCheckBox.isSelected() && includeRetiredsCheckBox.isSelected()) {
            result.putAll(AllUsers.getUsers());
            result.putAll(AllUsers.getDeletedUsers());
        }
        return result;
    }


    public void initializeTableDailyTime() {
        if (userBaseList == null) {
            userBaseList = FXCollections.observableArrayList();
        }
        userBaseList.clear();
        if (columns == null) {
            columns = FXCollections.observableArrayList();
        }
        columns.clear();

        LocalDate fromDate = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), 1);
        Year y = Year.from(fromDate);
        int monthLemgth = monthChoiceBox.getValue().length(y.isLeap());
        LocalDate tillDate = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), monthLemgth);

        for (User u : getUsers().values()) {
            UserBase ub = new UserBase(u.getIDNumber(), fromDate, tillDate);
            userBaseList.add(ub);
        }

        TableColumn<UserBase, String> usersColumn = new TableColumn<>("Работник");
        usersColumn.setStyle("-fx-alignment: CENTER;");

        usersColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                String s = AllUsers.getOneUser(param.getValue().getUserID()).getFullName();
                return new SimpleStringProperty(s);
            }
        });

        usersColumn.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
            @Override
            public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                return getTableCell(usersColumn, TextAlignment.LEFT);
            }
        });

        columns.add(usersColumn);



        for (int i = 1; i <= monthLemgth; i++) {
            TableColumn<UserBase, String> columnTime = new TableColumn<>(String.valueOf(i));
            final int j = i;
            columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                    double time = param.getValue().getWorkSumForDay(LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), j));
                    return new SimpleStringProperty(AllData.formatWorkTime(time));
                }
            });
            columnTime.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
                @Override
                public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                    /*TableCell<UserBase, String> result = getTableCell(usersColumn, TextAlignment.CENTER);
                    UserBase ub = param.getTableView().getSelectionModel().getSelectedItem();
                    UserBase ub = param.getTableView().getItems().get(getIndex());
                    double dayTime = ub.getWorkSumForDay(LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), j));
                    if (dayTime < 6) {
                        result.setStyle("-fx-background-color: #f2d8c9;");
                    }
                    return result;*/
                    return new TimeCell(j);
                }
            });
            //columnTime.setStyle("-fx-alignment: CENTER;");
            columns.add(columnTime);
        }

        TableColumn<UserBase, String> workSumColumn = new TableColumn<>("Всего");
        workSumColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                double time = param.getValue().getWorkSumForMonth(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue());
                return new SimpleStringProperty(AllData.formatWorkTime(time));
            }
        });
        workSumColumn.setStyle("-fx-alignment: CENTER; -fx-background-color: #fffbdc;");
        //workSumColumn.setStyle("-fx-background-color: linear(#fffbdc 100%);");
        columns.add(1, workSumColumn);


        for (TableColumn<UserBase, String> tc : columns) {
            tc.setMinWidth(25);
            tc.setPrefWidth(30);
            tc.setMaxWidth(35);
        }
        usersColumn.setMaxWidth(100);
        usersColumn.setPrefWidth(80);
        usersColumn.setMinWidth(50);
        workSumColumn.setMinWidth(40);
        workSumColumn.setPrefWidth(45);
        workSumColumn.setMaxWidth(50);



        tableUsers.getColumns().setAll(columns);

        SortedList<UserBase> sortedList = new SortedList<>(userBaseList, new Comparator<UserBase>() {
            @Override
            public int compare(UserBase o1, UserBase o2) {
                return AllUsers.getOneUser(o1.userID).getFullName().compareTo(AllUsers.getOneUser(o2.userID).getFullName());
            }
        });

        tableUsers.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableUsers.comparatorProperty());

        userBaseList.sort(new Comparator<UserBase>() {
            @Override
            public int compare(UserBase o1, UserBase o2) {
                return AllUsers.getOneUser(o1.userID).getFullName().compareTo(AllUsers.getOneUser(o2.userID).getFullName());
            }
        });

    }


    public void initializeTableTimeMonthly() {
        if (userBaseList == null) {
            userBaseList = FXCollections.observableArrayList();
        }
        userBaseList.clear();

        /*List<Project> projectList = AllData.getAllProjectsForPeriodWorking(from, till);
        List<WorkTime> workTimeList = new ArrayList<>();
        for (Project p : projectList) {
            workTimeList.addAll(p.getWorkTimeForPeriod(from, till));
        }*/

        List<User> listUsers = new ArrayList<>();

        for (User u : AllUsers.getUsers().values()) {
            if (!AllUsers.isUserDeleted(u.getIDNumber())) {
                if (u.getRole().equals(Role.DESIGNER)) {
                    listUsers.add(u);
                }
            }
        }

        /*for (User u : listUsers) {
            for (WorkTime wt : workTimeList) {
                if (wt.getDesignerID() == u.getIDNumber()) {
                    UserBase db = new UserBase(u.getIDNumber(), from, till);
                    db.addWorkTime(wt.getDate(), wt.getTimeDouble());
                    designerBaseList.add(db);
                }
            }
        }*/



    }

    private TableCell<UserBase, String> getTableCell(TableColumn column, TextAlignment textAlignment) {
        TableCell<UserBase, String> cell = new TableCell<>();
        Text text = new Text();
        text.setTextAlignment(textAlignment);
        text.setLineSpacing(1.0);
        cell.setGraphic(text);
        cell.setPadding(new Insets(8, 5, 6, 5));
        cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
        text.wrappingWidthProperty().bind(column.widthProperty());
        text.textProperty().bind(cell.itemProperty());
        return cell;
    }




    class UserBase {
        private int userID;
        private LocalDate fromDate;
        private LocalDate tillDate;
        private Map<LocalDate, Double> workSumMap = new HashMap<>();
        private double userWorkSum = 0;

        public UserBase(int ID, LocalDate fromDate, LocalDate tillDate) {
            this.userID = ID;
            this.fromDate = fromDate;
            this.tillDate = tillDate;
            fillWorkSumForPeriod(fromDate, tillDate);
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
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

        public double getUserWorkSum() {
            return userWorkSum;
        }

        public void setUserWorkSum(double userWorkSum) {
            this.userWorkSum = userWorkSum;
        }

        private void fillWorkSumForPeriod(LocalDate fromDate, LocalDate tillDate) {
            for (Project project : AllData.getAllProjectsForDesignerAndPeriodWorking(this.userID, fromDate, tillDate)) {
                for (WorkTime wt : project.getWorkTimeForDesignerAndPeriod(this.userID, fromDate, tillDate)) {
                    if (workSumMap.containsKey(wt.getDate())) {
                        double current = workSumMap.get(wt.getDate());
                        double added = AllData.formatDouble(current + wt.getTimeDouble());
                        workSumMap.put(wt.getDate(), added);
                    }
                    else {
                        workSumMap.put(wt.getDate(), wt.getTimeDouble());
                    }
                }
            }
        }

        public void addWorkSum(LocalDate day, double workSum) {
            if (day.compareTo(fromDate) >= 0 && day.compareTo(tillDate) <= 0) {
                if (workSumMap.containsKey(day)) {
                    double current = workSumMap.get(day);
                    double added = AllData.formatDouble(current + workSum);
                    workSumMap.put(day, added);
                }
                else {
                    workSumMap.put(day, AllData.formatDouble(workSum));
                }
            }
        }

        private double getWorkSumForDay(LocalDate date) {
            if (workSumMap.get(date) == null) {
                return 0;
            }
            return workSumMap.get(date);
        }

        public double getWorkSumForMonth(int year, int month) {
            double result = 0;
            LocalDate date = LocalDate.of(year, month, 1);
            Year y = Year.from(date);
            Month m = date.getMonth();
            for (int i = 1; i <= m.length(y.isLeap()); i++) {
                LocalDate d = LocalDate.of(year, month, i);
                result += getWorkSumForDay(d);
            }

            return AllData.formatDouble(result);
        }

    } // конец класса UserBase


    class TimeCell extends TableCell<UserBase, String> {
        private Integer day;

        public TimeCell(Integer day) {
            this.day = day;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            //super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            }
            else {
                UserBase ub = getTableView().getItems().get(getIndex());
                double dayTime = ub.getWorkSumForDay(LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), day));
                if (dayTime < 1) {
                    setStyle("-fx-alignment: CENTER; -fx-background-color: #f2d8c9;");
                }
                else {
                    setStyle("-fx-alignment: CENTER");
                }
                setGraphic(new Text(String.valueOf(dayTime)));
            }
        }
    }



}
