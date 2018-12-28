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

            boolean dayIsWorking = false;
            for (User usr : getUsers().values()) {
                LocalDate workDate = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), i);
                List<Project> projectsForDate = AllData.getAllProjectsForDesignerAndDate(usr.getIDNumber(), workDate);
                if (!projectsForDate.isEmpty()) {
                    dayIsWorking = true;
                    break;
                }
            }

            if (dayIsWorking) {
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
                        return new TimeCell(j);
                    }
                });

                columnTime.setEditable(true);
                columnTime.setSortable(true);
                columnTime.setResizable(true);

                columnTime.setMinWidth(25);
                //columnTime.setPrefWidth(30);
                columnTime.setMaxWidth(50);

                columns.add(columnTime);
            }
        }

        TableColumn<UserBase, String> workSumColumn = new TableColumn<>("Всего");
        workSumColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                double time = param.getValue().getWorkSumForMonth(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue());
                return new SimpleStringProperty(AllData.formatWorkTime(time));
            }
        });
        //workSumColumn.setStyle("-fx-alignment: CENTER; -fx-background-color: #fffbdc;");

        workSumColumn.setEditable(true);
        workSumColumn.setSortable(true);
        workSumColumn.setResizable(true);

        workSumColumn.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
            @Override
            public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                return new SumCell();
            }
        });
        columns.add(1, workSumColumn);


        TableColumn<UserBase, String> hourPayColumn = new TableColumn<>("З/п в час");
        hourPayColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                return new SimpleStringProperty(AllData.formatWorkTime(AllUsers.getOneUser(param.getValue().userID).getWorkHourValue()));
            }
        });

        hourPayColumn.setEditable(true);
        hourPayColumn.setSortable(true);
        hourPayColumn.setResizable(true);

        Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>> cellFactory =
                (TableColumn<UserBase, String> p) -> new EditingCellHourPay<>();
        hourPayColumn.setCellFactory(cellFactory);

        hourPayColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<UserBase, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<UserBase, String> event) {
                UserBase ub = (UserBase) event.getTableView().getItems().get(event.getTablePosition().getRow());
                AllUsers.getOneUser(ub.getUserID()).setWorkHourValue(Double.parseDouble(event.getNewValue()));
            }
        });

        hourPayColumn.setStyle("-fx-alignment: CENTER;");
        columns.add(0, hourPayColumn);


        TableColumn<UserBase, String> actionColumn = new TableColumn<>("Инфо");




        usersColumn.setMinWidth(90);
        //usersColumn.setPrefWidth(120);
        usersColumn.setMaxWidth(120);

        workSumColumn.setMinWidth(40);
        //workSumColumn.setPrefWidth(45);
        workSumColumn.setMaxWidth(50);

        hourPayColumn.setMinWidth(60);
        //hourPayColumn.setPrefWidth(50);
        hourPayColumn.setMaxWidth(80);



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
        cell.setPadding(new Insets(8, 5, 6, 10));
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
                if (dayTime < 0.5) {
                    setStyle("-fx-alignment: CENTER; -fx-background-color: #f2d8c9;");
                }
                else {
                    setStyle("-fx-alignment: CENTER");
                }
                setGraphic(new Text(String.valueOf(dayTime)));
            }
        }
    }

    class SumCell extends TableCell<UserBase, String> {

        public SumCell() {
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            //super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            }
            else {
                UserBase ub = getTableView().getItems().get(getIndex());
                double monthTime = ub.getWorkSumForMonth(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue());
                setStyle("-fx-alignment: CENTER; -fx-background-color: #fffbdc;");
                setGraphic(new Text(String.valueOf(monthTime)));
                setEditable(true);
            }
        }
    }


    class UserCell extends TableCell<UserBase, Boolean> {

        private final Button manageButton = new Button("Инфо");
        private final CheckBox retiredCheckBox = new CheckBox("Уволенный");
        private final Button deleteButton = new Button("X");

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            //super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            }
            else {
                UserBase ub = getTableView().getItems().get(getIndex());

                if (AllUsers.isUserDeleted(ub.getUserID())) {
                    retiredCheckBox.setSelected(true);
                    setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                }
                else {
                    retiredCheckBox.setSelected(false);
                    setStyle(null);
                }


                manageButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        //initClosing();

                        if (!AllData.openEditUserStages.containsKey(ub.getUserID())) {
                            //AllData.mainApp.
                        }
                        else {
                            AllData.openEditUserStages.get(ub.getUserID()).close();
                            AllData.openEditUserStages.get(ub.getUserID()).show();
                        }
                    }
                });

                retiredCheckBox.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        if (retiredCheckBox.isSelected()) {

                        }
                        else {

                        }
                    }
                });

                deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                    }
                });

            }

        }



        {
            manageButton.setMinHeight(20);
            manageButton.setMaxHeight(20);
            manageButton.setStyle("-fx-font-size:10");

            retiredCheckBox.setStyle("-fx-font-size:10");

            deleteButton.setMinHeight(20);
            deleteButton.setMaxHeight(20);
            deleteButton.setStyle("-fx-font-size:10");
        }

    } // конец класса UserCell

}






/*private void initUsersChoiceBox() {
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
    }*/