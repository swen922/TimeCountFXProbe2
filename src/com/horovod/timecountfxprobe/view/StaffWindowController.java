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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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

    private MainApp mainApp;

    private ObservableList<Integer> yearsValues;
    private ObservableList<Month> monthsValues;
    private ObservableList<UserBase> userBaseList;
    private ObservableList<TableColumn<?, String>> columns;

    private String allWorkers = "Все работники";



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
    private TableView tableUsers;




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



    // initclosing надо отрабатывать где-то уже после запуска
    // когда стейдж и контроллер полностью инициализированы
    private void initClosing() {

        if (AllData.staffWindowStage != null) {

            AllData.staffWindowStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {

                    for (EditUserWindowController eu : AllData.editUserWindowControllers.values()) {
                        eu.closing();
                    }

                    if (!AllData.editUserStages.isEmpty()) {
                        event.consume();
                    }
                    else {
                        AllData.staffWindowStage.close();
                    }
                }
            });
        }
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
                String name = AllUsers.getOneUser(ub.getUserID()).getFullName();
                return new SimpleStringProperty(name);
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
                int sum = AllUsers.getOneUser(ub.getUserID()).getWorkHourValue();
                return new SimpleStringProperty(AllData.formatInputInteger(sum));
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
        Map<Integer, Double> sumMap = new HashMap<>();


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
                Map<Integer, Double> valuesMap = new HashMap<>();
                for (UserBase ub : userBaseList) {
                    double value = ub.getWorkSumForDay(LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), j));
                    valuesMap.put(ub.getUserID(), value);
                    if (sumMap.containsKey(ub.getUserID())) {
                        double current = sumMap.get(ub.getUserID());
                        sumMap.put(ub.getUserID(), AllData.formatDouble(current + value, 1));
                    }
                    else {
                        sumMap.put(ub.getUserID(), AllData.formatDouble(value, 1));
                    }
                }

                TableColumn<UserBase, String> columnTime = new TableColumn<>(String.valueOf(i));

                columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                        double val = valuesMap.get(param.getValue().getUserID()) == null ? 0 : valuesMap.get(param.getValue().getUserID());
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
                double val = sumMap.get(param.getValue().getUserID()) == null ? 0 : sumMap.get(param.getValue().getUserID());
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

        LocalDate fromDate = LocalDate.of(yearsChoiceBox.getValue(), 1, 1);
        LocalDate tillDate = LocalDate.of(yearsChoiceBox.getValue(), 12, 31);

        for (User u : getUsers().values()) {
            UserBase ub = new UserBase(u.getIDNumber(), fromDate, tillDate);
            userBaseList.add(ub);
        }

        // Все вычисления по времени производим заранее, до выполнения методов
        // setCellValueFactory() и setCellFactory()
        Map<Integer, Double> sumMap = new HashMap<>();

        for (int i = 1; i <= 12; i++) {

            LocalDate date = LocalDate.of(yearsChoiceBox.getValue(), i, 1);
            Month month = date.getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

            TableColumn<UserBase, String> columnTime = new TableColumn<>(monthName);
            final int j = i;

            // Все вычисления по времени производим заранее, до выполнения методов
            // setCellValueFactory() и setCellFactory()
            Map<Integer, Double> valuesMap = new HashMap<>();
            for (UserBase ub : userBaseList) {
                double time = ub.getWorkSumForMonth(yearsChoiceBox.getValue(), j);
                valuesMap.put(ub.getUserID(), AllData.formatDouble(time, 1));
                if (sumMap.containsKey(ub.getUserID())) {
                    double current = sumMap.get(ub.getUserID());
                    sumMap.put(ub.getUserID(), AllData.formatDouble((current + time), 1));
                }
                else {
                    sumMap.put(ub.getUserID(), AllData.formatDouble(time, 1));
                }
            }



            columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                    return new SimpleStringProperty(AllData.formatWorkTime(valuesMap.get(param.getValue().getUserID())));
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
                return new SimpleStringProperty(AllData.formatWorkTime(sumMap.get(param.getValue().getUserID())));
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


    private void initializeTableDailyMoney() {

        LocalDate fromDate = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), 1);
        Year y = Year.from(fromDate);
        int monthLemgth = monthChoiceBox.getValue().length(y.isLeap());

        for (User u : getUsers().values()) {
            userBaseList.add(new UserBase(u.getIDNumber()));
        }

        Map<Integer, Integer> sumMap = new HashMap<>();

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
                Map<Integer, Integer> valuesMap = new HashMap<>();
                for (UserBase ub : userBaseList) {
                    LocalDate date = LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), j);
                    List<Project> projects = AllData.getAllProjectsForDesignerAndDate(ub.getUserID(), date);
                    if (projects.isEmpty()) {
                        valuesMap.put(ub.getUserID(), 0);
                    }
                    else {
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
                        }
                        int result = (int) money;
                        valuesMap.put(ub.getUserID(), result);
                        if (sumMap.containsKey(ub.getUserID())) {
                            int curr = sumMap.get(ub.getUserID());
                            sumMap.put(ub.getUserID(), (curr + result));
                        }
                        else {
                            sumMap.put(ub.getUserID(), result);
                        }
                    }
                }

                columnMoney.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                        return new SimpleStringProperty(AllData.formatInputInteger(valuesMap.get(param.getValue().getUserID())));
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
                return new SimpleStringProperty(AllData.formatInputInteger(sumMap.get(param.getValue().getUserID())));
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
    }


    private void initializeTableMonthlyMoney() {
        for (User u : getUsers().values()) {
            userBaseList.add(new UserBase(u.getIDNumber()));
        }

        // Все вычисления по времени производим заранее, до выполнения методов
        // setCellValueFactory() и setCellFactory()
        Map<Integer, Integer> sumMap = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            LocalDate date = LocalDate.of(yearsChoiceBox.getValue(), i, 1);
            Month month = date.getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

            TableColumn<UserBase, String> columnMoney = new TableColumn<>(monthName);
            final int j = i;

            // Все вычисления по времени производим заранее, до выполнения методов
            // setCellValueFactory() и setCellFactory()
            Map<Integer, Integer> valuesMap = new HashMap<>();
            for (UserBase ub : userBaseList) {
                List<Project> projects = AllData.getAllProjectsForDesignerAndMonth(ub.getUserID(), yearsChoiceBox.getValue(), j);
                if (projects.isEmpty()) {
                    valuesMap.put(ub.getUserID(), 0);
                }
                else {
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
                    }
                    int result = (int) money;
                    valuesMap.put(ub.getUserID(), result);
                    if (sumMap.containsKey(ub.getUserID())) {
                        int curr = sumMap.get(ub.getUserID());
                        sumMap.put(ub.getUserID(), (curr + result));
                    }
                    else {
                        sumMap.put(ub.getUserID(), result);
                    }
                }
            }

            columnMoney.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserBase, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<UserBase, String> param) {
                    return new SimpleStringProperty(AllData.formatInputInteger(valuesMap.get(param.getValue().getUserID())));
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
                return new SimpleStringProperty(AllData.formatInputInteger(sumMap.get(param.getValue().getUserID())));
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


    /*class TimeCell<T> extends TableCell<T, String> {
        private Integer day;
        private Class ParamClass;

        *//*public TimeCell(Integer day) {
            this.day = day;
        }*//*

        public TimeCell(Integer day, Class ParamClass) {
            this.day = day;
            this.ParamClass = ParamClass;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            //super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            }
            else {
                if (ParamClass == UserBase.class) {
                    UserBase ub = (UserBase) getTableView().getItems().get(getIndex());
                    double dayTime = ub.getWorkSumForDay(LocalDate.of(yearsChoiceBox.getValue(), monthChoiceBox.getValue().getValue(), day));
                    if (dayTime < AllData.getLimitTimeForStaffWindow()) {
                        setStyle("-fx-alignment: CENTER; -fx-background-color: #f2d8c9;");
                    }
                    else {
                        setStyle("-fx-alignment: CENTER");
                    }
                    setGraphic(new Text(String.valueOf(dayTime)));

                }

            }
        }
    }*/


    class ValueCell extends TableCell<UserBase, String> {
        private Map<Integer, ? extends Number> values;

        public ValueCell(Map<Integer, ? extends Number> values) {
            this.values = values;
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
        private Map<Integer, ? extends Number> values;

        public ValueCellMonth(Map<Integer, ? extends Number> values) {
            this.values = values;
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

                        AllData.rebuildEditUsersControllers();
                        initClosing();

                        if (!AllData.editUserStages.containsKey(userID)) {
                            AllData.mainApp.showEditUserWindow(userID);
                        }
                        else {
                            AllData.editUserStages.get(userID).close();
                            AllData.editUserStages.get(userID).show();
                        }
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
            manageButton.setMinHeight(20);
            manageButton.setMaxHeight(20);
            manageButton.setStyle("-fx-font-size:10");

            retiredCheckBox.setStyle("-fx-font-size:10");
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