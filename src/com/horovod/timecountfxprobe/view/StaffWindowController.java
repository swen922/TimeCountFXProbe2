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
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;

public class StaffWindowController {

    private MainApp mainApp;

    private ObservableList<Integer> yearsValues;
    private ObservableList<Month> monthsValues;
    private ObservableList<UserBase> userBaseList;
    private ObservableList<TableColumn<?, String>> columns;
    private Map<Integer, Number> sumMap = new HashMap<>();
    private Map<Integer, Map<Integer, Number>> allValuesMaps = new HashMap<>();

    private final String exportToTXT = "в Текст";
    private final String exportToCSV = "в CSV";
    private String logString = "";




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
    private TextField limitTimeTextField;

    @FXML
    private TextField limitMoneyTextField;

    @FXML
    private Button countSalary;

    @FXML
    private Button reloadButton;

    @FXML
    private Button createUser;

    @FXML
    private TableView tableUsers;

    @FXML
    private Button exportButton;

    @FXML
    private ChoiceBox<String> exportChoiceBox;

    @FXML
    private Label logLabel;

    @FXML
    private Button closeButton;


    @FXML
    public void initialize() {

        // Эти пункты надо отрабатывать только один раз, при запуске программы,
        // поэтому во время работы initialize() снова запускать нельзя
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

        initTimeLimitTextField();
        initMoneyLimitTextField();
        initializeTable();
        initExportChoiceBox();

    }

    private void initExportChoiceBox() {
        if (exportChoiceBox.getItems().isEmpty()) {
            exportChoiceBox.getItems().add(exportToTXT);
            exportChoiceBox.getItems().add(exportToCSV);
            exportChoiceBox.setValue(exportToTXT);
        }
    }

    public void handleExportButton() {
        if (exportChoiceBox.getValue().equals(exportToTXT)) {
            writeText();
        }
        else {
            writeCSV();
        }
    }

    public void handleCountSalaryButton() {
        if (AllData.countSalaryWindow == null) {
            AllData.mainApp.showCountSalaryWindow();
        }
        else {
            AllData.countSalaryWindow.hide();
            AllData.countSalaryWindow.show();
        }
    }


    public void handleCreateUserButton() {
        AllData.mainApp.showCreateUserWindow();
    }


    public void handleDayMonthRadioButtons() {
        if (daysRadioButton.isSelected()) {
            monthChoiceBox.setDisable(false);
        }
        else {
            monthChoiceBox.setDisable(true);
        }
        initializeTable();
    }



    private void initTimeLimitTextField() {
        limitTimeTextField.setText(AllData.formatWorkTime(AllData.getLimitTimeForStaffWindow()));
        limitTimeTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                limitTimeTextField.setText("");
            }
        });
        limitTimeTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    double limit = AllData.getDoubleFromText(AllData.getLimitTimeForStaffWindow(), limitTimeTextField.getText(), 1);
                    AllData.setLimitTimeForStaffWindow(limit);
                    limitTimeTextField.setText(AllData.formatWorkTime(limit));
                    initializeTable();
                }
            }
        });
    }

    private void initMoneyLimitTextField() {
        limitMoneyTextField.setText(String.valueOf(AllData.formatInputInteger(AllData.getLimitMoneyForStaffWindow())));
        limitMoneyTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                limitMoneyTextField.setText("");
            }
        });
        limitMoneyTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    int limit = AllData.getIntFromText(AllData.getLimitMoneyForStaffWindow(), limitMoneyTextField.getText());
                    AllData.setLimitMoneyForStaffWindow(limit);
                    limitMoneyTextField.setText(AllData.formatInputInteger(limit));
                    initializeTable();
                }
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

        yearsChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initializeTable();
            }
        });
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

        monthChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LocalDate selected = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), 1);
                if (selected.compareTo(LocalDate.now()) > 0) {
                    monthChoiceBox.setValue(LocalDate.now().getMonth());
                }
                initializeTable();
            }
        });
    }



    public void handleCloseButton() {
        AllData.staffWindowStage.close();
    }



    public void initializeTable() {

        if (userBaseList == null) {
            userBaseList = FXCollections.observableArrayList();
        }
        userBaseList.clear();

        if (columns == null) {
            columns = FXCollections.observableArrayList();
        }
        columns.clear();

        if (daysRadioButton.isSelected() && timeRadioButton.isSelected()) {
            initializeTableDailyTime();
        }
        else if (monthsRadioButton.isSelected() && timeRadioButton.isSelected()) {
            initializeTableMonthlyTime();
        }
        else if (daysRadioButton.isSelected() && moneyRadioButton.isSelected()) {
            initializeTableDailyMoney();
        }
        else if (monthsRadioButton.isSelected() && moneyRadioButton.isSelected()) {
            initializeTableMonthlyMoney();
        }


    }

    private Map<Integer, User> getUsers() {
        Map<Integer, User> result = new HashMap<>();

        if (designersOnlyCheckBox.isSelected() && !includeRetiredsCheckBox.isSelected()) {
            result.putAll(AllUsers.getActiveDesigners());
        }
        else if (designersOnlyCheckBox.isSelected() && includeRetiredsCheckBox.isSelected()) {
            result.putAll(AllUsers.getDesignersPlusDeleted());
        }
        else if (!designersOnlyCheckBox.isSelected() && !includeRetiredsCheckBox.isSelected()) {
            result.putAll(AllUsers.getActiveUsers());
        }
        else if (!designersOnlyCheckBox.isSelected() && includeRetiredsCheckBox.isSelected()) {
            result.putAll(AllUsers.getUsers());
        }
        return result;
    }


    private void initializeTableBaseColumns() {

        TableColumn<UserBase, String> usersColumn = new TableColumn<>("Работник");
        usersColumn.setStyle("-fx-alignment: CENTER;");

        usersColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                UserBase ub = param.getValue();
                String fullName = AllUsers.getOneUser(ub.getUserID()).getFullName();

                return new SimpleStringProperty(fullName);
            }
        });

        usersColumn.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
            @Override
            public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                return getTableCell(usersColumn, TextAlignment.LEFT);
            }
        });


        TableColumn<UserBase, String> hourPayColumn = new TableColumn<>("Руб/ч");

        hourPayColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                UserBase ub = (UserBase) param.getValue();
                String hourPayCell = AllUsers.getOneUser(ub.getUserID()).getWorkHourValue() == 0 ? "-" : AllData.formatInputInteger(AllUsers.getOneUser(ub.getUserID()).getWorkHourValue());
                return new SimpleStringProperty(hourPayCell);
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
                UserBase ub = event.getTableView().getItems().get(event.getTablePosition().getRow());
                int hourPay = AllData.getIntFromText(AllUsers.getOneUser(ub.getUserID()).getWorkHourValue(), event.getNewValue());
                AllUsers.getOneUser(ub.getUserID()).setWorkHourValue(hourPay);
                initializeTable();
            }
        });

        hourPayColumn.setStyle("-fx-alignment: CENTER;");



        TableColumn<UserBase, Boolean> actionColumn = new TableColumn<>("Инфо");

        actionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<UserBase, Boolean> param) {
               return new SimpleBooleanProperty(AllUsers.getOneUser(param.getValue().getUserID()).isRetired());

            }
        });

        actionColumn.setCellFactory(new Callback<TableColumn<UserBase, Boolean>, TableCell<UserBase, Boolean>>() {
            @Override
            public TableCell<UserBase, Boolean> call(TableColumn<UserBase, Boolean> param) {
                return new UserCell();
            }
        });

        actionColumn.setStyle("-fx-alignment: CENTER;");

        usersColumn.setMinWidth(90);
        usersColumn.setMaxWidth(120);

        hourPayColumn.setMinWidth(40);
        hourPayColumn.setMaxWidth(60);

        actionColumn.setMinWidth(120);
        actionColumn.setMaxWidth(150);

        //columns.add(0, usersColumn);
        //columns.add(0, hourPayColumn);
        tableUsers.getColumns().add(0, usersColumn);
        tableUsers.getColumns().add(0, hourPayColumn);
        tableUsers.getColumns().add(0, actionColumn);

    }


    private void initializeTableDailyTime() {

        logLabel.setText("");

        LocalDate fromDate = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), 1);
        Year y = Year.from(fromDate);
        int monthLemgth = monthChoiceBox.getValue().length(y.isLeap());
        LocalDate tillDate = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), monthLemgth);

        for (User u : getUsers().values()) {
            UserBase ub = new UserBase(u.getIDNumber(), fromDate, tillDate);
            userBaseList.add(ub);
        }

        // Все вычисления по времени производим заранее, до выполнения методов
        // setCellValueFactory() и setCellFactory()

        sumMap.clear();
        allValuesMaps.clear();

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
                final int j = i;

                // Все вычисления по времени производим заранее, до выполнения методов
                // setCellValueFactory() и setCellFactory()
                Map<Integer, Number> valuesMap = new HashMap<>();

                for (UserBase ub : userBaseList) {
                    Double value = ub.getWorkSumForDay(LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), j));
                    valuesMap.put(ub.getUserID(), value);
                    if (sumMap.containsKey(ub.getUserID())) {
                        double current = (Double) sumMap.get(ub.getUserID());
                        sumMap.put(ub.getUserID(), AllData.formatDouble(current + value, 1));
                    }
                    else {
                        sumMap.put(ub.getUserID(), AllData.formatDouble(value, 1));
                    }
                }
                allValuesMaps.put(j, valuesMap);

                TableColumn<UserBase, String> columnTime = new TableColumn<>(String.valueOf(i));

                columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                        double val = valuesMap.get(param.getValue().getUserID()) == null ? 0d : (Double) valuesMap.get(param.getValue().getUserID());
                        return new SimpleStringProperty(AllData.formatWorkTime(val));
                    }
                });
                columnTime.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
                    @Override
                    public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                        return new ValueCell(valuesMap);
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
                double val = sumMap.get(param.getValue().getUserID()) == null ? 0 : (Double) sumMap.get(param.getValue().getUserID());
                return new SimpleStringProperty(AllData.formatWorkTime(val));
            }
        });
        //workSumColumn.setStyle("-fx-alignment: CENTER; -fx-background-color: #fffbdc;");

        workSumColumn.setEditable(true);
        workSumColumn.setSortable(true);
        workSumColumn.setResizable(true);

        workSumColumn.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
            @Override
            public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                return new SumCell(sumMap);
            }
        });
        columns.add(0, workSumColumn);

        workSumColumn.setMinWidth(40);
        workSumColumn.setMaxWidth(50);

        tableUsers.getColumns().setAll(columns);


        initializeTableBaseColumns();


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



    private void initializeTableMonthlyTime() {

        logLabel.setText("");

        LocalDate fromDate = LocalDate.of(yearsChoiceBox.getValue(), 1, 1);
        LocalDate tillDate = LocalDate.of(yearsChoiceBox.getValue(), 12, 31);

        for (User u : getUsers().values()) {
            UserBase ub = new UserBase(u.getIDNumber(), fromDate, tillDate);
            userBaseList.add(ub);
        }

        // Все вычисления по времени производим заранее, до выполнения методов
        // setCellValueFactory() и setCellFactory()

        sumMap.clear();
        allValuesMaps.clear();


        for (int i = 1; i <= 12; i++) {

            LocalDate date = LocalDate.of(yearsChoiceBox.getValue(), i, 1);
            Month month = date.getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

            TableColumn<UserBase, String> columnTime = new TableColumn<>(monthName);
            final int j = i;

            // Все вычисления по времени производим заранее, до выполнения методов
            // setCellValueFactory() и setCellFactory()

            Map<Integer, Number> valuesMap = new HashMap<>();

            for (UserBase ub : userBaseList) {
                double time = ub.getWorkSumForMonth(yearsChoiceBox.getValue(), j);

                valuesMap.put(ub.getUserID(), AllData.formatDouble(time, 1));
                if (sumMap.containsKey(ub.getUserID())) {
                    double current = (Double) sumMap.get(ub.getUserID());
                    sumMap.put(ub.getUserID(), AllData.formatDouble((current + time), 1));
                }
                else {
                    sumMap.put(ub.getUserID(), AllData.formatDouble(time, 1));
                }
            }
            allValuesMaps.put(j, valuesMap);


            columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                    return new SimpleStringProperty(AllData.formatWorkTime((Double) valuesMap.get(param.getValue().getUserID())));
                }
            });

            columnTime.setEditable(true);
            columnTime.setSortable(true);
            columnTime.setResizable(true);

            columnTime.setMinWidth(35);
            columnTime.setMaxWidth(70);
            columnTime.setStyle("-fx-alignment: CENTER;");

            columnTime.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
                @Override
                public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                    return new ValueCellMonth(valuesMap);
                }
            });

            columns.add(columnTime);
        }

        TableColumn<UserBase, String> workSumColumn = new TableColumn<>("Всего");
        workSumColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                return new SimpleStringProperty(AllData.formatWorkTime((Double) sumMap.get(param.getValue().getUserID())));
            }
        });
        workSumColumn.setStyle("-fx-alignment: CENTER;");

        workSumColumn.setEditable(true);
        workSumColumn.setSortable(true);
        workSumColumn.setResizable(true);

        workSumColumn.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
            @Override
            public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                return new SumCell(sumMap);
            }
        });
        columns.add(0, workSumColumn);

        workSumColumn.setMinWidth(50);
        workSumColumn.setMaxWidth(70);


        tableUsers.getColumns().setAll(columns);


        initializeTableBaseColumns();


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

    private void logNoBudget(int counterAllProjcts, int counterNoBudgets) {
        if (counterNoBudgets != 0) {
            double percent = ((double) counterAllProjcts) / 100;
            double part = ((double) counterNoBudgets) / percent;
            BigDecimal partDec = new BigDecimal(Double.toString(part));
            partDec = partDec.setScale(2, RoundingMode.HALF_UP);
            part = partDec.doubleValue();

            StringBuilder counterSB = new StringBuilder("Примечание: данный подсчет выработки в рублях не является точным, так как у ");
            counterSB.append(part).append("% проектов в данной выборке не внесена сумма итоговой сметы");
            logLabel.setText(counterSB.toString());
            logString = counterSB.toString();

        }
    }


    private void initializeTableDailyMoney() {

        logLabel.setText("");

        int counterAllProjcts = 0;
        int counterNoBudgets = 0;

        LocalDate fromDate = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), 1);
        Year y = Year.from(fromDate);
        int monthLemgth = monthChoiceBox.getValue().length(y.isLeap());

        for (User u : getUsers().values()) {
            userBaseList.add(new UserBase(u.getIDNumber()));
        }

        sumMap.clear();
        allValuesMaps.clear();

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
                TableColumn<UserBase, String> columnMoney = new TableColumn<>(String.valueOf(i));
                final int j = i;

                // Все вычисления по времени производим заранее, до выполнения методов
                // setCellValueFactory() и setCellFactory()

                Map<Integer, Number> valuesMap = new HashMap<>();

                for (UserBase ub : userBaseList) {
                    LocalDate date = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), j);
                    List<Project> projects = AllData.getAllProjectsForDesignerAndDate(ub.getUserID(), date);
                    if (projects.isEmpty()) {
                        valuesMap.put(ub.getUserID(), 0);
                    }
                    else {
                        counterAllProjcts += projects.size();
                        double money = 0;
                        for (Project p : projects) {
                            if (p.getBudget() != 0) {
                                int sumInt = p.getWorkSumForDesignerAndDate(ub.getUserID(), date);
                                int total = p.getWorkSum();
                                double totalDouble = AllData.intToDouble(total);
                                double percent = totalDouble / 100;
                                double sum = AllData.intToDouble(sumInt);
                                double part = sum / percent;
                                double totalBudget = (double) (p.getBudget());
                                double partBudget = (totalBudget / 100) * part;
                                money += partBudget;
                            }
                            else {
                                counterNoBudgets++;
                            }
                        }
                        int result = (int) money;
                        valuesMap.put(ub.getUserID(), result);
                        if (sumMap.containsKey(ub.getUserID())) {
                            int curr = (Integer) sumMap.get(ub.getUserID());
                            sumMap.put(ub.getUserID(), (curr + result));
                        }
                        else {
                            sumMap.put(ub.getUserID(), result);
                        }
                    }
                }
                allValuesMaps.put(j, valuesMap);

                columnMoney.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                        return new SimpleStringProperty(AllData.formatInputInteger((Integer) valuesMap.get(param.getValue().getUserID())));
                    }
                });

                columnMoney.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
                    @Override
                    public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                        return new ValueCell(valuesMap);
                    }
                });


                columnMoney.setEditable(true);
                columnMoney.setSortable(true);
                columnMoney.setResizable(true);

                columnMoney.setMinWidth(25);
                //columnTime.setPrefWidth(30);
                columnMoney.setMaxWidth(50);

                columns.add(columnMoney);
            }
        }

        TableColumn<UserBase, String> workSumColumn = new TableColumn<>("Всего");

        workSumColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                return new SimpleStringProperty(AllData.formatInputInteger((Integer) sumMap.get(param.getValue().getUserID())));
            }
        });

        workSumColumn.setEditable(true);
        workSumColumn.setSortable(true);
        workSumColumn.setResizable(true);

        workSumColumn.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
            @Override
            public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                return new SumCell(sumMap);
            }
        });


        columns.add(0, workSumColumn);

        workSumColumn.setMinWidth(50);
        workSumColumn.setMaxWidth(70);

        tableUsers.getColumns().setAll(columns);


        initializeTableBaseColumns();


        SortedList<UserBase> sortedList = new SortedList<>(userBaseList, new Comparator<UserBase>() {
            @Override
            public int compare(UserBase o1, UserBase o2) {
                return AllUsers.getOneUser(o1.getUserID()).getFullName().compareTo(AllUsers.getOneUser(o2.getUserID()).getFullName());
            }
        });

        tableUsers.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableUsers.comparatorProperty());

        userBaseList.sort(new Comparator<UserBase>() {
            @Override
            public int compare(UserBase o1, UserBase o2) {
                return AllUsers.getOneUser(o1.getUserID()).getFullName().compareTo(AllUsers.getOneUser(o2.getUserID()).getFullName());
            }
        });

        logNoBudget(counterAllProjcts, counterNoBudgets);
    }


    private void initializeTableMonthlyMoney() {

        logLabel.setText("");

        int counterAllProjcts = 0;
        int counterNoBudgets = 0;

        for (User u : getUsers().values()) {
            userBaseList.add(new UserBase(u.getIDNumber()));
        }

        // Все вычисления по времени производим заранее, до выполнения методов
        // setCellValueFactory() и setCellFactory()

        sumMap.clear();
        allValuesMaps.clear();

        for (int i = 1; i <= 12; i++) {
            LocalDate date = LocalDate.of(yearsChoiceBox.getValue(), i, 1);
            Month month = date.getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

            TableColumn<UserBase, String> columnMoney = new TableColumn<>(monthName);
            final int j = i;

            // Все вычисления по времени производим заранее, до выполнения методов
            // setCellValueFactory() и setCellFactory()

            Map<Integer, Number> valuesMap = new HashMap<>();

            for (UserBase ub : userBaseList) {
                List<Project> projects = AllData.getAllProjectsForDesignerAndMonth(ub.getUserID(), yearsChoiceBox.getValue(), j);
                if (projects.isEmpty()) {
                    valuesMap.put(ub.getUserID(), 0);
                }
                else {
                    counterAllProjcts += projects.size();
                    double money = 0;
                    for (Project p : projects) {
                        if (p.getBudget() != 0) {
                            int sumInt = p.getWorkSumForDesignerAndMonth(ub.getUserID(), yearsChoiceBox.getValue(), j);
                            int total = p.getWorkSum();
                            double totalDouble = AllData.intToDouble(total);
                            double percent = totalDouble / 100;
                            double sum = AllData.intToDouble(sumInt);
                            double part = sum / percent;
                            double totalBudget = (double) (p.getBudget());
                            double partBudget = (totalBudget / 100) * part;
                            money += partBudget;
                        }
                        else {
                            counterNoBudgets++;
                        }
                    }
                    int result = (int) money;
                    valuesMap.put(ub.getUserID(), result);
                    if (sumMap.containsKey(ub.getUserID())) {
                        int curr = (Integer) sumMap.get(ub.getUserID());
                        sumMap.put(ub.getUserID(), (curr + result));
                    }
                    else {
                        sumMap.put(ub.getUserID(), result);
                    }
                }
            }
            allValuesMaps.put(j, valuesMap);

            columnMoney.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                    return new SimpleStringProperty(AllData.formatInputInteger((Integer) valuesMap.get(param.getValue().getUserID())));
                }
            });

            columnMoney.setEditable(true);
            columnMoney.setSortable(true);
            columnMoney.setResizable(true);

            columnMoney.setMinWidth(35);
            columnMoney.setMaxWidth(70);
            columnMoney.setStyle("-fx-alignment: CENTER;");

            columnMoney.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
                @Override
                public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                    return new ValueCellMonth(valuesMap);
                }
            });

            columns.add(columnMoney);
        }

        TableColumn<UserBase, String> workSumColumn = new TableColumn<>("Всего");
        workSumColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                return new SimpleStringProperty(AllData.formatInputInteger((Integer) sumMap.get(param.getValue().getUserID())));
            }
        });
        workSumColumn.setStyle("-fx-alignment: CENTER;");

        workSumColumn.setEditable(true);
        workSumColumn.setSortable(true);
        workSumColumn.setResizable(true);

        workSumColumn.setCellFactory(new Callback<TableColumn<UserBase, String>, TableCell<UserBase, String>>() {
            @Override
            public TableCell<UserBase, String> call(TableColumn<UserBase, String> param) {
                return new SumCell(sumMap);
            }
        });
        columns.add(0, workSumColumn);

        workSumColumn.setMinWidth(50);
        workSumColumn.setMaxWidth(70);


        tableUsers.getColumns().setAll(columns);


        initializeTableBaseColumns();


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

        logNoBudget(counterAllProjcts, counterNoBudgets);

    }


    private TableCell<UserBase, String> getTableCell(TableColumn column, TextAlignment textAlignment) {
        TableCell<UserBase, String> cell = new TableCell<>();
        Text text = new Text();
        text.setTextAlignment(textAlignment);
        text.setLineSpacing(1.0);
        cell.setGraphic(text);
        cell.setCursor(Cursor.TEXT);
        cell.setPadding(new Insets(8, 30, 6, 30));
        cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
        text.wrappingWidthProperty().bind(column.widthProperty());
        text.textProperty().bind(cell.itemProperty());
        return cell;
    }



    private void writeText() {

        String period = "";

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(AllData.pathToDownloads));


        StringBuilder fileName = new StringBuilder("Статистика по персоналу за ");
        if (daysRadioButton.isSelected()) {
            period = monthChoiceBox.getValue().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " ";
        }
        period = period + yearsChoiceBox.getValue() + " г.";
        fileName.append(period);

        if (timeRadioButton.isSelected()) {
            fileName.append(" в часах");
        }
        else {
            fileName.append(" в рублях");
        }

        chooser.setInitialFileName(fileName.toString());

        File file = chooser.showSaveDialog(AllData.staffWindowStage);

        if (file != null) {
            if (!file.getPath().endsWith(".txt")) {
                file = new File(file.getPath() + ".txt");
            }
        }


        StringBuilder sb = new StringBuilder("\n").append(fileName).append("\n");

        for (UserBase ub : userBaseList) {
            sb.append("\n\n\n").append(AllUsers.getOneUser(ub.getUserID()).getFullName()).append(":\n\n");
            sb.append("Итого за ").append(period).append(" = ");
            if (timeRadioButton.isSelected()) {
                sb.append(AllData.formatWorkTime((Double) sumMap.get(ub.getUserID())));
                sb.append(" ").append(AllData.formatHours(String.valueOf(sumMap.get(ub.getUserID()))));
            }
            else {
                sb.append(AllData.formatInputInteger((Integer) sumMap.get(ub.getUserID())));
                sb.append(" руб.");
            }
            sb.append("\n");

            if (daysRadioButton.isSelected()) {

                for (Map.Entry<Integer, Map<Integer, Number>> entry : allValuesMaps.entrySet()) {
                    LocalDate date = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), entry.getKey());

                    if (timeRadioButton.isSelected()) {
                        double value = (Double) allValuesMaps.get(entry.getKey()).get(ub.getUserID());
                        String valuesString = AllData.formatWorkTime(value);
                        if (value != 0) {
                            sb.append(AllData.formatDate(date)).append(" = ");
                            sb.append(valuesString).append(" ").append(AllData.formatHours(String.valueOf(value)));
                            sb.append("\n");
                        }
                    }
                    else {
                        int value = (Integer) allValuesMaps.get(entry.getKey()).get(ub.getUserID());
                        String valuesString = AllData.formatInputInteger(value);
                        if (value != 0) {
                            sb.append(AllData.formatDate(date)).append(" = ");
                            sb.append(valuesString).append(" ").append("руб.");
                            sb.append("\n");
                        }
                    }

                }
            }
            else {
                for (Map.Entry<Integer, Map<Integer, Number>> entry : allValuesMaps.entrySet()) {
                    LocalDate date = LocalDate.of(yearsChoiceBox.getValue(), entry.getKey(), 1);
                    String monthName = date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

                    if (timeRadioButton.isSelected()) {
                        double value = (Double) allValuesMaps.get(entry.getKey()).get(ub.getUserID());
                        String valuesString = AllData.formatWorkTime(value);
                        if (value != 0) {
                            sb.append(monthName).append(" = ");
                            sb.append(valuesString).append(" ").append(AllData.formatHours(String.valueOf(value)));
                            sb.append("\n");
                        }
                    }
                    else {
                        int value = (Integer) allValuesMaps.get(entry.getKey()).get(ub.getUserID());
                        String valuesString = AllData.formatInputInteger(value);
                        if (value != 0) {
                            sb.append(monthName).append(" = ");
                            sb.append(valuesString).append(" руб.");
                            sb.append("\n");
                        }
                    }
                }
            }
        }



        if (file != null) {

            try (Writer writer = new BufferedWriter(new FileWriter(file))) {

                writer.write(sb.toString());
                writer.flush();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                AllData.logger.error(ex.getMessage(), ex);
            }
        }
    }



    private void writeCSV() {

        String period = "";

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV file", "*.csv");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(AllData.pathToDownloads));


        StringBuilder fileName = new StringBuilder("Статистика по персоналу за ");
        if (daysRadioButton.isSelected()) {
            period = monthChoiceBox.getValue().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " ";
        }
        period = period + yearsChoiceBox.getValue() + " г.";
        fileName.append(period);

        if (timeRadioButton.isSelected()) {
            fileName.append(" в часах");
        }
        else {
            fileName.append(" в рублях");
        }

        chooser.setInitialFileName(fileName.toString());

        File file = chooser.showSaveDialog(AllData.staffWindowStage);

        if (file != null) {
            if (!file.getPath().endsWith(".csv")) {
                file = new File(file.getPath() + ".csv");
            }
        }


        StringBuilder sb = new StringBuilder("\n").append(fileName).append("\n");

        sb.append("\n\n\n").append("Работник").append("\t").append("Всего").append("\t");

        if (daysRadioButton.isSelected()) {
            for (Map.Entry<Integer, Map<Integer, Number>> entry : allValuesMaps.entrySet()) {
                sb.append(entry.getKey()).append("\t");
            }
        }
        else {
            for (Map.Entry<Integer, Map<Integer, Number>> entry : allValuesMaps.entrySet()) {
                LocalDate dd = LocalDate.of(yearsChoiceBox.getValue(), entry.getKey(), 1);
                String monthString = dd.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
                sb.append(monthString).append("\t");
            }
        }


        sb.append("\n\n");

        for (UserBase ub : userBaseList) {

            sb.append(AllUsers.getOneUser(ub.getUserID()).getFullName()).append("\t");

            if (timeRadioButton.isSelected()) {
                sb.append(AllData.formatWorkTime((Double) sumMap.get(ub.getUserID()))).append("\t");
            }
            else {
                sb.append(AllData.formatInputInteger((Integer) sumMap.get(ub.getUserID()))).append("\t");
            }

            if (daysRadioButton.isSelected()) {

                for (Map.Entry<Integer, Map<Integer, Number>> entry : allValuesMaps.entrySet()) {

                    if (timeRadioButton.isSelected()) {
                        double value = (Double) allValuesMaps.get(entry.getKey()).get(ub.getUserID());
                        if (value == 0) {
                            sb.append("\t");
                        }
                        else {
                            String valuesString = AllData.formatWorkTime(value);
                            sb.append(valuesString).append("\t");
                        }
                    }
                    else {
                        int value = (Integer) allValuesMaps.get(entry.getKey()).get(ub.getUserID());
                        if (value == 0) {
                            sb.append("\t");
                        }
                        else {
                            String valuesString = AllData.formatInputInteger(value);
                            sb.append(valuesString).append("\t");
                        }
                    }
                }
                sb.append("\n");

            }
            else {
                for (Map.Entry<Integer, Map<Integer, Number>> entry : allValuesMaps.entrySet()) {

                    LocalDate dd = LocalDate.of(yearsChoiceBox.getValue(), entry.getKey(), 1);
                    int monthNumber = dd.getMonth().getValue();

                    if (monthNumber == entry.getKey()) {

                        if (timeRadioButton.isSelected()) {
                            double value = (Double) allValuesMaps.get(entry.getKey()).get(ub.getUserID());
                            String valuesString = AllData.formatWorkTime(value);
                            if (value == 0) {
                                sb.append("\t");
                            }
                            else {
                                sb.append(valuesString).append("\t");
                            }
                        }
                        else {
                            int value = (Integer) allValuesMaps.get(entry.getKey()).get(ub.getUserID());
                            String valuesString = AllData.formatInputInteger(value);
                            if (value == 0) {
                                sb.append("\t");
                            }
                            else {
                                sb.append(valuesString).append("\t");
                            }
                        }
                    }
                }
                sb.append("\n");
            }

        }

        if (file != null) {

            try (Writer writer = new BufferedWriter(new FileWriter(file))) {

                writer.write(sb.toString());
                writer.flush();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                AllData.logger.error(ex.getMessage(), ex);

            }
        }

    }




    class UserBase {
        private int userID;
        private LocalDate fromDate;
        private LocalDate tillDate;
        private Map<LocalDate, Double> workSumMap = new HashMap<>();
        private double userWorkSum = 0;
        private Map<Integer, Double> yearWorkSums = new HashMap<>();

        public UserBase(int ID, LocalDate fromDate, LocalDate tillDate) {
            this.userID = ID;
            this.fromDate = fromDate;
            this.tillDate = tillDate;
            fillWorkSumForPeriod(fromDate, tillDate);
        }

        public UserBase(int userID) {
            this.userID = userID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public synchronized void setFromDate(LocalDate fromDate) {
            this.fromDate = fromDate;
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
            for (Project project : AllData.getAllProjectsForDesignerAndPeriod(this.userID, fromDate, tillDate)) {
                for (WorkTime wt : project.getWorkTimeForDesignerAndPeriod(this.userID, fromDate, tillDate)) {
                    if (workSumMap.containsKey(wt.getDate())) {
                        double current = workSumMap.get(wt.getDate());
                        double added = AllData.formatDouble(current + wt.getTimeDouble(), 1);
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
                    double added = AllData.formatDouble(current + workSum, 1);
                    workSumMap.put(day, added);
                }
                else {
                    workSumMap.put(day, AllData.formatDouble(workSum, 1));
                }
            }
        }

        public double getWorkSumForDay(LocalDate date) {
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

            return AllData.formatDouble(result, 1);
        }

        public double getWorkSumForYear(int year) {
            if (yearWorkSums.containsKey(year)) {
                return yearWorkSums.get(year);
            }
            double result = 0;
            for (int i = 1; i <= 12; i++) {
                LocalDate d = LocalDate.of(year, i, 1);
                Month m = d.getMonth();
                result += getWorkSumForMonth(year, m.getValue());
            }
            yearWorkSums.put(year, AllData.formatDouble(result, 1));
            return AllData.formatDouble(result, 1);
        }

    } // конец класса UserBase


    class ValueCell extends TableCell<UserBase, String> {
        private Map<Integer, Number> values = new HashMap<>();

        public ValueCell(Map<Integer, Number> values) {
            this.values.putAll(values);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            //super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            }
            else {
                UserBase ub = getTableView().getItems().get(getIndex());
                if (timeRadioButton.isSelected()) {
                    Double time = (Double) values.get(ub.getUserID());

                    if (time < AllData.getLimitTimeForStaffWindow()) {
                        setStyle("-fx-alignment: CENTER; -fx-background-color: #f2d8c9;");
                    }
                    else {
                        setStyle("-fx-alignment: CENTER");
                    }
                    setGraphic(new Text(AllData.formatWorkTime(time)));
                }
                else if (moneyRadioButton.isSelected()) {
                    Integer money = (Integer) values.get((ub.getUserID()));
                    if (money < AllData.getLimitMoneyForStaffWindow()) {
                        setStyle("-fx-alignment: CENTER; -fx-background-color: #f2d8c9;");
                    }
                    else {
                        setStyle("-fx-alignment: CENTER");
                    }
                    setGraphic(new Text(AllData.formatInputInteger(money)));
                }
            }
        }
    }


    class ValueCellMonth extends TableCell<UserBase, String> {
        private Map<Integer, Number> values = new HashMap<>();

        public ValueCellMonth(Map<Integer, Number> values) {
            this.values.putAll(values);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            //super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            }
            else {
                if (values.isEmpty()) {
                    setGraphic(new Text("0"));
                }
                else {
                    UserBase ub = getTableView().getItems().get(getIndex());
                    Set<Number> sorted = new TreeSet<>();
                    sorted.addAll(values.values());

                    if (timeRadioButton.isSelected()) {
                        Double time = (Double) values.get(ub.getUserID()) == null ? 0d : (Double) values.get(ub.getUserID());
                        if (time.equals(((TreeSet<Number>) sorted).first()) && sorted.size() > 1) {
                            setStyle("-fx-alignment: CENTER; -fx-background-color: #f2d8c9;");
                        }
                        else if (time.equals(((TreeSet<Number>) sorted).last()) && sorted.size() > 1) {
                            setStyle("-fx-alignment: CENTER; -fx-background-color: #dbe9d8;");
                        }
                        else {
                            setStyle("-fx-alignment: CENTER");
                        }
                        setGraphic(new Text(AllData.formatWorkTime(time)));
                    }
                    else {
                        Integer money = (Integer) values.get((ub.getUserID())) == null ? 0 : (Integer) values.get(ub.getUserID());
                        if (money.equals(((TreeSet<Number>) sorted).first()) && sorted.size() > 1) {
                            setStyle("-fx-alignment: CENTER; -fx-background-color: #f2d8c9;");
                        }
                        else if (money.equals(((TreeSet<Number>) sorted).last()) && sorted.size() > 1) {
                            setStyle("-fx-alignment: CENTER; -fx-background-color: #dbe9d8;");
                        }
                        else {
                            setStyle("-fx-alignment: CENTER");
                        }
                        setGraphic(new Text(AllData.formatInputInteger(money)));
                    }
                }
            }
        }
    }


    class SumCell extends TableCell<UserBase, String> {

        private Map<Integer, ? extends Number> values;

        public SumCell(Map<Integer, ? extends Number> values) {
            this.values = values;
        }

        @Override
        protected void updateItem(String item, boolean empty) {

            if (empty) {
                setGraphic(null);
            }
            else {
                UserBase ub = getTableView().getItems().get(getIndex());
                if (timeRadioButton.isSelected()) {
                    Double time = (Double) values.get(ub.getUserID()) == null ? 0d : (Double) values.get(ub.getUserID());
                    if (time != 0) {
                        setGraphic(new Text(AllData.formatWorkTime(time)));
                    }
                    else {
                        setGraphic(new Text(""));
                    }

                }
                else {
                    Integer money = (Integer) values.get((ub.getUserID())) == null ? 0 : (Integer) values.get(ub.getUserID());
                    if (money != 0) {
                        setGraphic(new Text(AllData.formatInputInteger(money)));
                    }
                    else {
                        setGraphic(new Text(""));
                    }

                }
                setStyle("-fx-alignment: CENTER; -fx-background-color: #fffbdc;");
                setEditable(true);
            }
        }
    }


    class UserCell extends TableCell<UserBase, Boolean> {

        private final Button manageButton = new Button("Инфо");
        private final CheckBox retiredCheckBox = new CheckBox("Уволен");

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            //super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            }
            else {
                UserBase ub = getTableView().getItems().get(getIndex());
                final int userID = ub.getUserID();

                if (AllUsers.isUserDeleted(userID)) {
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
                        AllData.mainApp.showEditUserWindow(userID, AllData.staffWindowStage);
                    }
                });

                retiredCheckBox.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        if (retiredCheckBox.isSelected()) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Подтверждение увольнения");
                            alert.setHeaderText("Уволить работника\n" + AllUsers.getOneUser(userID).getFullName() + "?");

                            Optional<ButtonType> option = alert.showAndWait();

                            if (option.get() == ButtonType.OK) {
                                boolean retired = AllUsers.deleteUser(userID);
                                if (retired) {
                                    retiredCheckBox.setSelected(true);
                                    setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                                    if (AllData.countSalaryWindowController != null) {
                                        AllData.countSalaryWindowController.updateUsers();
                                    }
                                    initializeTable();
                                }
                                else {
                                    Alert notRetired = new Alert(Alert.AlertType.WARNING);
                                    notRetired.setTitle("Ошибка в программе");
                                    notRetired.setHeaderText("Не удалось уволить работника " + AllUsers.getOneUser(userID).getFullName() + ".\nОбратитесь за помощью к Анисимову");
                                }
                            }
                            else {
                                retiredCheckBox.setSelected(false);
                                setStyle(null);
                            }

                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Подтверждение возвращения");
                            alert.setHeaderText("Принять работника\n" + AllUsers.getOneUser(userID).getFullName() + "\nснова на работу?");

                            Optional<ButtonType> option = alert.showAndWait();

                            if (option.get() == ButtonType.OK) {
                                boolean returned = AllUsers.resurrectUser(userID);
                                if (returned) {
                                    retiredCheckBox.setSelected(false);
                                    setStyle(null);
                                    if (AllData.countSalaryWindowController != null) {
                                        AllData.countSalaryWindowController.updateUsers();
                                    }
                                    initializeTable();
                                }
                                else {
                                    Alert notReturned = new Alert(Alert.AlertType.WARNING);
                                    notReturned.setTitle("Ошибка в программе");
                                    notReturned.setHeaderText("Не удалось вернуть работника " + AllUsers.getOneUser(userID).getFullName() + ".\nОбратитесь за помощью к Анисимову");
                                }
                            }
                            else {
                                retiredCheckBox.setSelected(true);
                                setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                            }

                        }
                    }
                });

                HBox hbox = new HBox();
                hbox.getChildren().addAll(manageButton, retiredCheckBox);
                hbox.setAlignment(Pos.CENTER);
                hbox.setSpacing(10);
                setGraphic(hbox);
            }
        }


        {
            retiredCheckBox.setCursor(Cursor.HAND);
            manageButton.setMinHeight(20);
            manageButton.setMaxHeight(20);
            manageButton.setStyle("-fx-font-size:10");

            retiredCheckBox.setStyle("-fx-font-size:10");
        }

    } // конец класса UserCell

}



