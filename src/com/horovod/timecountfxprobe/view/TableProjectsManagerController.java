package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkDay;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.test.TestBackgroundUpdate01;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import java.awt.Desktop;

public class TableProjectsManagerController {

    private MainApp mainApp;
    private Stage stage;

    private ObservableList<Map.Entry<Integer, Project>> showProjects;
    private FilteredList<Map.Entry<Integer, Project>> filterData;
    private Predicate<Map.Entry<Integer, Project>> filterPredicate;
    private FilteredList<Map.Entry<Integer, Project>> filterDataWrapper;
    private SortedList<Map.Entry<Integer, Project>> sortedList;

    private ObservableList<String> datesForChart;
    private ObservableList<XYChart.Data<String, Integer>> workTimeForChart;
    private XYChart.Series<String, Integer> series;



    @FXML
    private TextField filterField;

    @FXML
    private Button deleteSearchTextButton;

    @FXML
    private CheckBox showArchiveProjectsCheckBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker tillDatePicker;

    @FXML
    private Button clearDatePicker;

    @FXML
    private Button newProjectButton;

    @FXML
    private Button selectTMPButton;

    @FXML
    private LineChart<String, Integer> decadeLineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private Label todayWorkSumLabel;

    @FXML
    private Label workSumLabel;

    @FXML
    private Button buttonReload;

    @FXML
    private Button buttonStatistic;

    @FXML
    private Label aboutProgramLabel;

    @FXML
    private Button exportButton;

    @FXML
    private ChoiceBox<String> exportChoiceBox;

    @FXML
    private ChoiceBox<String> usersLoggedChoiceBox;

    @FXML
    private Label statusLabel;



    /** Таблица и ее колонки */

    @FXML
    private TableView<Map.Entry<Integer, Project>> projectsTable;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, Boolean> columnAction;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, Integer> columnID;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, Double> columnTime;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnCompany;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnManager;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnBudget;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnPOnumber;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnDescription;


    /** Временные кнопки под тестирование */

    @FXML
    private Button testAddButton;

    @FXML
    private Button testDeleteButton;



    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp newMainApp) {
        this.mainApp = newMainApp;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage newStage) {
        this.stage = newStage;
    }

    public TextField getFilterField() {
        return filterField;
    }


    @FXML
    public void initialize() {

        /** TODO сюда или в mainApp добавить setStageTitle("Имя пользователя")
         * */

        // Отработка методов данных
        AllData.deleteZeroTime();
        AllData.rebuildWorkSum();
        AllData.rebuildTodayWorkSumProperty();
        //AllData.rebuildDesignerWeekWorkSumProperty(today.getYear(), today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        //AllData.rebuildDesignerMonthWorkSumProperty(today.getYear(), today.getMonth().getValue());
        //AllData.rebuildDesignerYearWorkSumProperty(today.getYear());

        if (showProjects == null) {
            showProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
            sortTableProjects();
        }

        if (filterData == null) {
            filterData = new FilteredList<>(showProjects, p -> true);
        }
        if (filterPredicate == null) {
            filterPredicate = p -> true;

        }
        if (filterDataWrapper == null) {
            filterDataWrapper = new FilteredList<>(filterData, filterPredicate);
        }
        if (sortedList == null) {
            sortedList = new SortedList<>(filterDataWrapper);
        }

        handleFilters();

        columnAction.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Boolean> param) {
                ObservableValue<Boolean> result = new SimpleBooleanProperty(param.getValue().getValue().isArchive());
                return result;
            }
        });

        columnAction.setCellFactory(new Callback<TableColumn<Map.Entry<Integer, Project>, Boolean>, TableCell<Map.Entry<Integer, Project>, Boolean>>() {
            @Override
            public TableCell<Map.Entry<Integer, Project>, Boolean> call(TableColumn<Map.Entry<Integer, Project>, Boolean> param) {
                return new ManagerCell();
            }
        });


        columnID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Integer> param) {
                return param.getValue().getValue().idNumberProperty().asObject();
            }
        });

        columnID.setStyle("-fx-alignment: CENTER;");

        columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Double> param) {
                return new SimpleDoubleProperty(param.getValue().getValue().getWorkSumDouble()).asObject();
            }
        });

        columnTime.setStyle("-fx-alignment: CENTER;");

        columnCompany.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().companyProperty();
            }
        });

        columnCompany.setCellFactory(new Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>>() {
            @Override
            public TableCell<Map.Entry<Integer, Project>, String> call(TableColumn<Map.Entry<Integer, Project>, String> param) {
                return getTableCell(columnCompany, TextAlignment.CENTER);
            }
        });

        columnCompany.setStyle("-fx-alignment: CENTER;");

        columnManager.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().managerProperty();
            }
        });

        columnManager.setCellFactory(new Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>>() {
            @Override
            public TableCell<Map.Entry<Integer, Project>, String> call(TableColumn<Map.Entry<Integer, Project>, String> param) {
                return getTableCell(columnManager, TextAlignment.CENTER);
            }
        });

        columnManager.setStyle("-fx-alignment: CENTER;");



        columnBudget.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().budgetProperty();
            }
        });

        Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>> cellFactory =
                (TableColumn<Map.Entry<Integer, Project>, String> p) -> new TableProjectsManagerController.EditingCell();

        columnBudget.setCellFactory(cellFactory);

        columnBudget.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String> event) {

                Integer budget = null;
                try {
                    budget = Integer.parseInt(event.getNewValue());
                } catch (NumberFormatException e) {
                    return;
                }
                Project project = (Project) event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue();
                project.setBudget(budget);

                // код для мгновенного обновления страниц у менеджера
                if (AllData.editProjectWindowControllers.containsKey(project.getIdNumber())) {
                    AllData.editProjectWindowControllers.get(project.getIdNumber()).initializeTable();
                }

                filterField.setText("-");
                filterField.clear();
            }
        });

        columnBudget.setStyle("-fx-alignment: CENTER;");



        columnPOnumber.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().PONumberProperty();
            }
        });

        Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>> cellFactoryString =
                (TableColumn<Map.Entry<Integer, Project>, String> p) -> new TableProjectsManagerController.EditingCellString();

        columnPOnumber.setCellFactory(cellFactoryString);

        columnPOnumber.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String> event) {
                Project project = (Project) event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue();
                project.setPONumber(event.getNewValue());
            }
        });

        columnPOnumber.setStyle("-fx-alignment: CENTER;");





        columnDescription.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().descriptionProperty();
            }
        });

        columnDescription.setCellFactory(new Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>>() {
            @Override
            public TableCell<Map.Entry<Integer, Project>, String> call(TableColumn<Map.Entry<Integer, Project>, String> param) {
                return getTableCell(columnDescription, TextAlignment.LEFT);
            }
        });

        filterDataWrapper.setPredicate(filterPredicate);
        projectsTable.setItems(sortedList);
        sortedList.comparatorProperty().bind(projectsTable.comparatorProperty());

        todayWorkSumLabel.textProperty().bind(AllData.todayWorkSumProperty().asString());
        workSumLabel.textProperty().bind(AllData.workSumProjectsProperty().asString());

        initializeChart();
        initLoggedUsersChoiceBox();
        initExportChoiceBox();

    }



    public void sortTableProjects() {

        showProjects.sort(new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                return Integer.compare(o2.getKey(), o1.getKey());
            }
        });
    }


    public void addPredicateToFilter() {

        filterPredicate = new Predicate<Map.Entry<Integer, Project>>() {
            @Override
            public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {

                String newValue = filterField.getText();

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();
                boolean result = true;

                if (lowerCaseFilter.contains(" ")) {
                    String[] allParts = lowerCaseFilter.split(" ");
                    List<Boolean> resultList = new ArrayList<>();

                    for (String s : allParts) {
                        boolean res = containsString(integerProjectEntry.getValue(), s);
                        resultList.add(res);
                    }
                    for (boolean b : resultList) {
                        result = b;
                        if (!b) {break;}
                    }
                    return result;
                }
                else {
                    result = containsString(integerProjectEntry.getValue(), lowerCaseFilter);
                }

                return result;
            }
        };

        initialize();
    }


    private boolean containsString(Project project, String input) {
        String workTimeInTable = "0.0";
        if (project.containsWorkTime()) {
            workTimeInTable = String.valueOf(AllData.intToDouble(project.getWorkSum()));
        }
        if (String.valueOf(project.getIdNumber()).contains(input)) {
            return true;
        }
        else if (workTimeInTable.contains(input)) {
            return true;
        }
        else if (project.getCompany().toLowerCase().contains(input)) {
            return true;
        }
        else if (project.getManager().toLowerCase().contains(input)) {
            return true;
        }
        else if (project.getDescription().toLowerCase().contains(input)) {
            return true;
        }
        else if (project.getPONumber() != null) {
            if (project.getPONumber().toLowerCase().contains(input)) {
                return true;
            }
        }
        return false;
    }


    // Может, перенести весь метод в AllData или в другой общий класс?
    public void initLoggedUsersChoiceBox() {

        String toLoginWindow = "Выйти в окно логина";

        usersLoggedChoiceBox.setItems(AllUsers.getUsersLogged());

        if (!usersLoggedChoiceBox.getItems().contains(toLoginWindow)) {
            usersLoggedChoiceBox.getItems().add(toLoginWindow);
        }

        usersLoggedChoiceBox.setValue(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName());

        usersLoggedChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String selectUser = usersLoggedChoiceBox.getValue();

                if (selectUser != null) {
                    if (selectUser.equalsIgnoreCase(toLoginWindow)) {

                        AllData.getRootLayout().setCenter(null);
                        mainApp.showLoginWindow();
                    }
                    else if (!selectUser.equalsIgnoreCase(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName())) {
                        User user = AllUsers.getOneUserForFullName(selectUser);

                        Role role = user.getRole();
                        if (role.equals(Role.DESIGNER)) {
                            AllData.getRootLayout().setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            //initialize();
                            mainApp.showTableProjectsDesigner();
                            if (AllData.getStatStage() != null) {
                                if (AllData.getStatStage().isShowing()) {
                                    mainApp.showStatisticWindow();
                                }
                            }
                        }
                        else if (role.equals(Role.MANAGER)) {
                            AllData.getRootLayout().setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            //initialize();
                            mainApp.showTableProjectsManager();

                            /** TODO Переписать для окна статистики менеджера */
                            if (AllData.getStatStage() != null) {
                                if (AllData.getStatStage().isShowing()) {
                                    //mainApp.showStatisticWindow();
                                }
                            }
                        }
                    }
                }
            }
        });
    }



    /** Три метода для инициализации / обновления лайнчарта
     * Один метод – диспетчер, два других служебные для него */

    private void initializeChart() {

        decadeLineChart.setStyle("-fx-font-size: " + 1 + "px;");

        xAxis.tickLabelFontProperty().set(Font.font(5.0));
        xAxis.setLabel("");

        LocalDate from = LocalDate.now().minusDays(15);
        LocalDate till = LocalDate.now().minusDays(1);

        if (datesForChart == null) {
            datesForChart = FXCollections.observableArrayList();
            xAxis.setCategories(datesForChart);
        }

        fillDatesChart(from, till);

        if (workTimeForChart == null) {
            workTimeForChart = FXCollections.observableArrayList();
        }

        if (series == null) {
            series = new XYChart.Series<>();
            series.setData(workTimeForChart);
            series.setName(null);
            decadeLineChart.getData().add(series);
        }

        fillXYChartSeries(from, till);

    }

    private void fillDatesChart(LocalDate from, LocalDate till) {

        List<Project> decadeProjects = AllData.getAllProjectsForPeriodWorking(from, till);

        TreeSet<String> setForSorting = new TreeSet<>();
        for (Project p : decadeProjects) {
            for (WorkTime wt : p.getWork()) {
                if (wt.getDate().compareTo(from) >= 0 && wt.getDate().compareTo(till) <= 0) {
                    setForSorting.add(wt.getDateString());
                }
            }
        }

        datesForChart.clear();
        datesForChart.addAll(setForSorting);

    }


    private void fillXYChartSeries(LocalDate from, LocalDate till) {

        List<Project> myProjects = AllData.getAllProjectsForPeriodWorking(from, till);
        Map<String, Integer> decadeWorkSums = new TreeMap<>();

        for (Project p : myProjects) {

            for (WorkTime wt : p.getWork()) {
                String dateWork = wt.getDateString();
                if (decadeWorkSums.containsKey(dateWork)) {
                    int currentSum = decadeWorkSums.get(dateWork);
                    decadeWorkSums.put(dateWork, (currentSum + wt.getTime()));
                }
                else {
                    decadeWorkSums.put(dateWork, wt.getTime());
                }
            }
        }

        workTimeForChart.clear();

        for (String s : datesForChart) {
            workTimeForChart.add(new XYChart.Data<>(s, decadeWorkSums.get(s)));
        }
    }



    public void handleDeleteSearch() {
        filterField.setText("");
        filterDataWrapper.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
            @Override
            public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                return true;
            }
        });
    }



    /** Три метода для чекбокса и двух дейтпикеров
     * сначала запрашивают проверку дат на валидность,
     * затем передают дальнейшее действие методу handleFilters()
     * который фильтрует filterData в зависимости от состояния
     * чекбокса и дейтпикеров.
     * */


    public void handleShowArchiveProjectsCheckBox() {
        handleFilters();
        initialize();
    }

    public void handleFromDatePicker() {
        checkDatePicker(fromDatePicker);
        handleFilters();
        initialize();
    }

    public void handleTillDatePicker() {
        checkDatePicker(tillDatePicker);
        handleFilters();
        initialize();
    }

    private void checkDatePicker(Node node) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (fromDate.compareTo(tillDate) > 0) {
                if (node == fromDatePicker) {
                    fromDatePicker.setValue(null);
                }
                else {
                    tillDatePicker.setValue(null);
                }
            }
        }
    }


    public void handleFilters() {

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (showArchiveProjectsCheckBox.isSelected()) {
                showProjects = FXCollections.observableArrayList(AllData.getAllProjects().entrySet());
            }
            else {
                showProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
            }

            filterData = new FilteredList<>(showProjects, p -> true);
            filterDataWrapper = new FilteredList<>(filterData, filterPredicate);
            sortedList = new SortedList<>(filterDataWrapper);

            filterData.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
                @Override
                public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                    if (integerProjectEntry.getValue().containsWorkTime(fromDate, tillDate)) {
                        return true;
                    }
                    return false;
                }
            });
        }
        else {
            if (showArchiveProjectsCheckBox.isSelected()) {
                showProjects = FXCollections.observableArrayList(AllData.getAllProjects().entrySet());
            }
            else {
                showProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
            }
            filterData = new FilteredList<>(showProjects, p -> true);
            filterDataWrapper = new FilteredList<>(filterData, filterPredicate);
            sortedList = new SortedList<>(filterDataWrapper);

            filterData.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
                @Override
                public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                    return true;
                }
            });

        }

        sortTableProjects();
        projectsTable.refresh();
    }



    public void handleDeleteDatePicker() {
        fromDatePicker.setValue(null);
        tillDatePicker.setValue(null);
        handleFilters();
    }

    public void handleReloadButton() {
        initialize();
    }

    public void handleStatisticButton() {
        mainApp.showStatisticWindow();
    }

    public void handleAbout() {
        this.mainApp.showAboutWindow();
    }

    public void updateStatus(String message) {
        statusLabel.setText("Статус: " + message);
    }

    public void resetStatus() {
        statusLabel.setText("Статус: все системы работают нормально");
    }


    /** Вытащенный в отдельный метод кусок кода из метода initialize()
     * чтобы не повторять один и тот же код несколько раз
     * */

    private TableCell<Map.Entry<Integer, Project>, String> getTableCell(TableColumn column, TextAlignment textAlignment) {
        TableCell<Map.Entry<Integer, Project>, String> cell = new TableCell<>();
        Text text = new Text();
        text.setTextAlignment(textAlignment);
        text.setLineSpacing(5.0);
        cell.setGraphic(text);
        //cell.setPadding(new Insets(10, 10, 10, 10));
        cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
        text.wrappingWidthProperty().bind(column.widthProperty());
        text.textProperty().bind(cell.itemProperty());
        return cell;

        // Это тоже работающий вариант
                /*return new TableCell<Map.Entry<Integer, Project>, String>() {
                    private Text text;
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item.toString());
                            text.setWrappingWidth(columnDescription.getWidth());
                            this.setWrapText(true);
                            setGraphic(text);
                        }
                    }
                };*/
    }

    public void initExportChoiceBox() {
        if (exportChoiceBox.getItems().isEmpty()) {
            exportChoiceBox.getItems().add("Время в TXT");
            exportChoiceBox.getItems().add("Таблицу в CSV");
            exportChoiceBox.setValue("Время в TXT");
        }
    }


    public void handleExportButton() {
        if (exportChoiceBox.getValue().equals("Время в TXT")) {
            writeText();
        }
        else {
            writeCSV();
        }
    }


    private void writeCSV() {

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV file", "*.csv");
        chooser.getExtensionFilters().add(extFilter);

        String path = new File(System.getProperty("user.home")).getPath() + "/Documents";
        chooser.setInitialDirectory(new File(path));


        StringBuilder fileName = new StringBuilder("Рабочее время за ");

        if (fromDatePicker.getValue() == null || tillDatePicker.getValue() == null) {
            fileName.append("весь период");
        }
        else if (fromDatePicker.getValue().equals(tillDatePicker.getValue())) {
            fileName.append(AllData.formatDate(fromDatePicker.getValue()));
        }
        else {
            fileName.append("период c ").append(AllData.formatDate(fromDatePicker.getValue())).append(" по ").
                    append(AllData.formatDate(tillDatePicker.getValue()));
        }


        chooser.setInitialFileName(fileName.toString());

        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            if (!file.getPath().endsWith(".csv")) {
                file = new File(file.getPath() + ".csv");
            }
        }

        if (file != null) {
            int counter = 0;
            try (Writer writer = new BufferedWriter(new FileWriter(file))) {

                writer.write("ID-номер" + "\t" + "Время" + "\t" + "Компания" + "\t" + "Менеджер" + "\t" + "Номер PO" + "\t" + "Описание" + "\n");

                for (Map.Entry<Integer, Project> entry : filterDataWrapper) {
                    String POnumber = entry.getValue().getPONumber() == null ? "нет" : entry.getValue().getPONumber();
                    String s = entry.getKey() + "\t"
                            + entry.getValue().getWorkSumDouble() + "\t"
                            + entry.getValue().getCompany() + "\t"
                            + entry.getValue().getManager() + "\t"
                            + POnumber + "\t"
                            + entry.getValue().getDescription() + "\n";
                    writer.write(s);
                    counter += entry.getValue().getWorkSum();
                }
                writer.write("\nИтого:" + "\t" + AllData.intToDouble(counter) + "\n");
                writer.flush();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



    private void writeText() {

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
        chooser.getExtensionFilters().add(extFilter);

        String path = new File(System.getProperty("user.home")).getPath() + "/Documents";
        chooser.setInitialDirectory(new File(path));



        StringBuilder fileName = new StringBuilder("Рабочее время за ");

        if (fromDatePicker.getValue() == null || tillDatePicker.getValue() == null) {
            fileName.append("весь период");
        }
        else if (fromDatePicker.getValue().equals(tillDatePicker.getValue())) {
            fileName.append(AllData.formatDate(fromDatePicker.getValue()));
        }
        else {
            fileName.append("период c ").append(AllData.formatDate(fromDatePicker.getValue())).append(" по ").
                    append(AllData.formatDate(tillDatePicker.getValue()));
        }

        if (filterField.getText() != null && !filterField.getText().isEmpty()) {
            fileName.append(" согласно выборке");
        }

        chooser.setInitialFileName(fileName.toString());

        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            if (!file.getPath().endsWith(".txt")) {
                file = new File(file.getPath() + ".txt");
            }
        }

        if (file != null) {
            try (Writer writer = new BufferedWriter(new FileWriter(file))) {

                StringBuilder sb = new StringBuilder(fileName).append("\n\n\n");
                int counter = 0;

                for (Map.Entry<Integer, Project> entry : sortedList) {
                    if (entry.getValue().containsWorkTime()) {
                        sb.append(entry.getValue().getDescription().split(" - ")[0].trim());
                        sb.append(" id-").append(entry.getKey()).append("\n\n");
                        int projectCounter = 0;

                        List<WorkTime> listWorks;

                        if (fromDatePicker.getValue() == null || tillDatePicker.getValue() == null) {
                            listWorks = entry.getValue().getWork();
                        }
                        else if (fromDatePicker.getValue().equals(tillDatePicker.getValue())) {
                            listWorks = entry.getValue().getWorkTimeForDate(fromDatePicker.getValue());
                        }
                        else {
                            listWorks = entry.getValue().getWorkTimeForPeriod(fromDatePicker.getValue(), tillDatePicker.getValue());
                        }

                        List<WorkDay> workDays = AllData.convertWorkTimesToWorkDays(listWorks);

                        for (WorkDay wd : workDays) {

                            int workdayCounter = 0;
                            sb.append(wd.getDateString()).append("\n");

                            for (Map.Entry<Integer, Double> e : wd.getWorkTimeMap().entrySet()) {
                                sb.append(AllUsers.getOneUser(e.getKey()).getFullName()).append(" = ");
                                sb.append(AllData.formatWorkTime(e.getValue())).append(" ");
                                sb.append(AllData.formatHours(String.valueOf(e.getValue()))).append("\n");
                                counter += AllData.doubleToInt(e.getValue());
                                projectCounter += AllData.doubleToInt(e.getValue());
                                workdayCounter += AllData.doubleToInt(e.getValue());
                            }

                            if (wd.getWorkTimeMap().entrySet().size() > 1) {
                                sb.append("Итого за ").append(wd.getDateString()).append(" = ");
                                sb.append(AllData.formatWorkTime(AllData.intToDouble(workdayCounter))).append(" ");
                                sb.append(AllData.formatHours(String.valueOf(AllData.intToDouble(workdayCounter)))).append("\n");
                            }

                        }
                        sb.append("\n");
                        sb.append("Итого в данном проекте = ").append(AllData.formatWorkTime(AllData.intToDouble(projectCounter)));
                        sb.append(" ").append(AllData.formatHours(String.valueOf(AllData.intToDouble(projectCounter)))).append("\n\n");
                        sb.append("\n\n");
                    }
                }
                sb.append("\n");
                sb.append("Итого в данной выборке = ").append(AllData.formatWorkTime(AllData.intToDouble(counter)));
                sb.append(" ").append(AllData.formatHours(String.valueOf(AllData.intToDouble(counter)))).append("\n\n");
                sb.append("файл сохранен ").append(AllData.formatDate(LocalDate.now())).append("\n\n");

                writer.write(sb.toString());
                writer.flush();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }



    public void testAdd() {

        /*Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("inside call()");
                AllData.addWorkTime(12, LocalDate.now(),5, 60);
                return null;
            }
        };

        *//** ОБЯЗАТЕЛЬНО к использованию! *//*
        Platform.runLater(task);*/

        /*Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();*/


        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundAddTime();

    }

    public void testDelete() {
        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundDeleteTime();

        //AllData.addWorkTime(12, LocalDate.now(),5, 20);
    }


    class ManagerCell extends TableCell<Map.Entry<Integer, Project>, Boolean> {
        private final Button openFolderButton = new Button("Туда");
        private final Button manageButton = new Button("Инфо");
        private final CheckBox archiveCheckBox = new CheckBox("Архивный");
        private final Button deleteButton = new Button("X");

        String startPath = "/Volumes/design/";

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            if (empty) {
                setGraphic(null);
            }
            else {

                Map.Entry<Integer, Project> entry = getTableView().getItems().get(getIndex());

                if (entry.getValue().isArchive()) {
                    archiveCheckBox.setSelected(true);
                    setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                }
                else {
                    archiveCheckBox.setSelected(false);
                    setStyle(null);
                }


                openFolderButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        String path;
                        if (entry.getValue().getFolderPath() != null) {
                            path = entry.getValue().getFolderPath();

                            try {
                                Desktop.getDesktop().browseFileDirectory(new File(path));
                            } catch (Exception e) {
                                String projectName = entry.getValue().getDescription().split(" - ")[0].trim() + " id-" + entry.getKey();
                                path = startPath + entry.getValue().getCompany() + "/" + projectName;
                                try {
                                    Desktop.getDesktop().browseFileDirectory(new File(path));
                                } catch (Exception e1) {
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Не удалось открыть папку");
                                    alert.setHeaderText("Не удалось открыть папку");
                                    alert.setContentText("Не удалось найти и открыть\nпапку проекта id-" + entry.getKey());
                                    alert.showAndWait();
                                }
                            }

                        }
                        else {
                            String projectName = entry.getValue().getDescription().split(" - ")[0].trim() + " id-" + entry.getKey();
                            path = startPath + entry.getValue().getCompany() + "/" + projectName;
                            try {
                                Desktop.getDesktop().browseFileDirectory(new File(path));
                            } catch (Exception e1) {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Не удалось открыть папку");
                                alert.setHeaderText("Не удалось открыть папку");
                                alert.setContentText("Не удалось найти и открыть\nпапку проекта id-" + entry.getKey());
                                alert.showAndWait();
                            }
                        }
                    }
                });

                manageButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        if (!AllData.openEditProjectStages.containsKey(entry.getKey())) {
                            mainApp.showEditProjectWindow(entry.getKey());

                        }
                        else {
                            AllData.openEditProjectStages.get(entry.getKey()).close();
                            AllData.openEditProjectStages.get(entry.getKey()).show();
                        }
                    }
                });


                archiveCheckBox.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        if (archiveCheckBox.isSelected()) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Подтверждение перевода в архив");
                            alert.setHeaderText("Перевести проект id-" + entry.getKey() + " в архив?");

                            Optional<ButtonType> option = alert.showAndWait();

                            if (option.get() == ButtonType.OK) {
                                AllData.changeProjectArchiveStatus(entry.getKey(), true);
                                setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                            }
                            else {
                                AllData.changeProjectArchiveStatus(entry.getKey(), false);
                                setStyle(null);
                            }
                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Подтверждение вывода из архива");
                            alert.setHeaderText("Вывести проект id-" + entry.getKey() + " из архива?");

                            Optional<ButtonType> option = alert.showAndWait();

                            if (option.get() == ButtonType.OK) {
                                AllData.changeProjectArchiveStatus(entry.getKey(), false);
                                setStyle(null);
                            }
                            else if (option.get() == ButtonType.CANCEL) {
                                AllData.changeProjectArchiveStatus(entry.getKey(), true);
                                setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                            }
                        }
                        handleFilters();
                        initialize();
                    }
                });

                deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Подтверждение удаления");
                        alert.setHeaderText("Удалить проект id-" + entry.getKey() + "?");
                        alert.setContentText("Проект и все рабочее время по нему\nбудут удалены из системы.\nЭто действие нельзя отменить.");

                        Optional<ButtonType> option = alert.showAndWait();

                        if (option.get() == ButtonType.OK) {
                            AllData.deleteProject(entry.getKey());
                            handleFilters();
                            initialize();
                        }
                    }
                });

                HBox hbox = new HBox();
                hbox.getChildren().addAll(openFolderButton, manageButton, archiveCheckBox, deleteButton);
                hbox.setAlignment(Pos.CENTER);
                hbox.setSpacing(15);
                setGraphic(hbox);
            }
        }


        {

            openFolderButton.setMinHeight(20);
            openFolderButton.setMaxHeight(20);
            openFolderButton.setStyle("-fx-font-size:10");

            manageButton.setMinHeight(20);
            manageButton.setMaxHeight(20);
            manageButton.setStyle("-fx-font-size:10");

            archiveCheckBox.setStyle("-fx-font-size:10");

            deleteButton.setMinHeight(20);
            deleteButton.setMaxHeight(20);
            deleteButton.setStyle("-fx-font-size:10");
        }

    }


    /*class ArchiveRow extends TableRow<Map.Entry<Integer, Project>> {

        @Override
        protected void updateItem(Map.Entry<Integer, Project> item, boolean empty) {
            if (item == null) {
                //setStyle("-fx-background-color: transparent;");
                setStyle(null);
                return;
            }
            if (item.getValue().isArchive()) {
                setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
            }
            else {
                //setStyle("-fx-background-color: transparent;");
                setStyle(null);
            }
        }
    }*/

    class EditingCell extends TableCell<Map.Entry<Integer, Project>, String> {

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }

        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        protected void updateItem(String item, boolean empty) {

            super.updateItem(item, empty);
            if (empty) {

                setText(null);
                setGraphic(null);
            }
            else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            String oldText = getString();
            textField = new TextField(oldText);
            textField.setAlignment(Pos.CENTER);
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    KeyCode keyCode = event.getCode();
                    if (keyCode == KeyCode.ENTER) {
                        commitEdit(AllData.formatStringInputInteger(oldText, textField.getText()));
                        TableProjectsManagerController.EditingCell.this.getTableView().requestFocus();
                        TableProjectsManagerController.EditingCell.this.getTableView().getSelectionModel().selectAll();
                        //initialize();
                        //projectsTable.refresh();
                    }
                }
            });
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!newValue) {
                        commitEdit(AllData.formatStringInputInteger(oldText, textField.getText()));
                        TableProjectsManagerController.EditingCell.this.getTableView().requestFocus();
                        TableProjectsManagerController.EditingCell.this.getTableView().getSelectionModel().selectAll();
                        //initialize();
                        //projectsTable.refresh();
                    }
                }
            });
            TableProjectsManagerController.EditingCell.this.textField.selectAll();

        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    } // Конец класса EditingCell



    class EditingCellString extends TableCell<Map.Entry<Integer, Project>, String> {

        private TextField textField;

        public EditingCellString() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }

        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        protected void updateItem(String item, boolean empty) {

            super.updateItem(item, empty);
            if (empty) {

                setText(null);
                setGraphic(null);
            }
            else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            String oldText = getString();
            textField = new TextField(oldText);
            textField.setAlignment(Pos.CENTER);
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    KeyCode keyCode = event.getCode();
                    if (keyCode == KeyCode.ENTER) {
                        commitEdit(textField.getText());
                        TableProjectsManagerController.EditingCellString.this.getTableView().requestFocus();
                        TableProjectsManagerController.EditingCellString.this.getTableView().getSelectionModel().selectAll();
                        //initialize();
                        //projectsTable.refresh();
                    }
                }
            });
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!newValue) {
                        commitEdit(textField.getText());
                        TableProjectsManagerController.EditingCellString.this.getTableView().requestFocus();
                        TableProjectsManagerController.EditingCellString.this.getTableView().getSelectionModel().selectAll();
                        //initialize();
                        //projectsTable.refresh();
                    }
                }
            });
            TableProjectsManagerController.EditingCellString.this.textField.selectAll();

        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    } // Конец класса EditingCellString

}
