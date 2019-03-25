package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkDay;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.test.TestBackgroundUpdate01;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import java.awt.Desktop;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableProjectsManagerController {

    private ObservableList<Map.Entry<Integer, Project>> tableProjects;
    private FilteredList<Map.Entry<Integer, Project>> filterData;
    private Predicate<Map.Entry<Integer, Project>> filterPredicate;
    private FilteredList<Map.Entry<Integer, Project>> filterDataWrapper;
    private SortedList<Map.Entry<Integer, Project>> sortedList;

    private ObservableList<String> datesForChart;
    private ObservableList<XYChart.Data<String, Integer>> workTimeForChart;
    private XYChart.Series<String, Integer> series;

    private int myIdNumber;


    @FXML
    private AnchorPane topColoredPane;

    @FXML
    private TextField filterField;

    @FXML
    private Button deleteSearchTextButton;

    @FXML
    private CheckBox showArchiveProjectsCheckBox;

    @FXML
    private Label projectsCountLabel;

    @FXML
    private Label projectsProjectsLabel;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker tillDatePicker;

    @FXML
    private Button clearDatePicker;

    @FXML
    private Button usersButton;

    @FXML
    private Button calculateSumButton;

    @FXML
    private Button newProjectButton;

    @FXML
    private Button batchArchiveButton;

    @FXML
    private Button reloadButton;

    @FXML
    private LineChart<String, Integer> decadeLineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private Label todayWorkSumLabel;

    @FXML
    private Label workSumLabel;

    @FXML
    private Button statisticButton;

    @FXML
    private Label aboutProgramLabel;

    @FXML
    private Button exportButton;

    @FXML
    private ChoiceBox<String> exportChoiceBox;

    @FXML
    private Button clearLoggedUsers;

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


    @FXML
    public void initialize() {

        myIdNumber = AllUsers.getCurrentUser();
        AllData.primaryStage.setTitle(AllUsers.getOneUser(myIdNumber).getFullName());

        initTextFields();
        initObservableLists();
        initializeTable();
        initializeChart();
        initLoggedUsersChoiceBox();
        initExportChoiceBox();

    }


    public void updateManagerWindow() {

        initTextFields();
        handleFilters();
        sortTableProjects();
        initializeChart();
        projectsCountLabel.setText(String.valueOf(sortedList.size()));
        projectsProjectsLabel.setText(AllData.formatProjects(sortedList.size()));


    }


    private void initObservableLists() {
        // Отработка методов данных

        if (tableProjects == null && showArchiveProjectsCheckBox.isSelected()) {
            tableProjects = FXCollections.observableArrayList(AllData.getAllProjects().entrySet());
        }
        else if (tableProjects == null && !showArchiveProjectsCheckBox.isSelected()) {
            tableProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
        }

        if (filterData == null) {
            filterData = new FilteredList<>(tableProjects, p -> true);
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
    }

    private void initTextFields() {

        if (AllUsers.getOneUser(myIdNumber).isRetired()) {
            todayWorkSumLabel.setText("-");
            workSumLabel.setText("-");
            topColoredPane.setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
            statisticButton.setDisable(true);
            usersButton.setDisable(true);
            calculateSumButton.setDisable(true);
            newProjectButton.setDisable(true);
        }
        else {
            todayWorkSumLabel.textProperty().bind(AllData.todayWorkSumProperty().asString());
            workSumLabel.textProperty().bind(AllData.workSumProjectsProperty().asString());
            AllData.rebuildDesignerRatingPosition();
            statisticButton.setDisable(false);
            usersButton.setDisable(false);
            calculateSumButton.setDisable(false);
            newProjectButton.setDisable(false);

            AllData.deleteZeroTime();
            AllData.deleteZeroTime();
            AllData.rebuildWorkSum();
            AllData.rebuildTodayWorkSumProperty();
            topColoredPane.setStyle(null);
        }
    }



    public void initializeTable() {
        sortTableProjects();
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
                String budgetString = null;
                if (param.getValue().getValue().budgetProperty() == null || param.getValue().getValue().budgetProperty().get().isEmpty()) {
                    budgetString = "0";
                }
                else {
                    budgetString = param.getValue().getValue().budgetProperty().get();
                }
                String budget = AllData.formatStringInputInteger("0", budgetString);
                if (budget.equals("0")) {
                    return new SimpleStringProperty("");
                }

                return new SimpleStringProperty(budget);
                //return param.getValue().getValue().budgetProperty();
            }
        });

        Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>> cellFactory =
                (TableColumn<Map.Entry<Integer, Project>, String> p) -> new EditingCell(FillChartMode.MONEY);

        columnBudget.setCellFactory(cellFactory);

        columnBudget.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String> event) {

                Project project = (Project) event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue();
                if (!project.isArchive()) {
                    int oldValue = 0;
                    if (event.getOldValue() != null && !event.getOldValue().isEmpty()) {
                        oldValue = AllData.parseMoney(0, event.getOldValue());
                    }
                    int budget = AllData.parseMoney(oldValue, event.getNewValue());
                    project.setBudget(budget);

                /*Integer budget = null;
                try {
                    budget = Integer.parseInt(event.getNewValue());
                } catch (NumberFormatException e) {
                    return;
                }
                Project project = (Project) event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue();
                project.setBudget(budget);*/

                    // код для мгновенного обновления страниц у менеджера
                    if (AllData.editProjectWindowControllers.containsKey(project.getIdNumber())) {
                        AllData.editProjectWindowControllers.get(project.getIdNumber()).updateEditProjectWindow();
                    }
                    if (AllData.staffWindowController != null && AllData.staffWindowStage.isShowing()) {
                        AllData.staffWindowController.initializeTable();
                    }

                    filterField.setText("-");
                    filterField.clear();

                    Updater.update(UpdateType.UPDATE_PROJECT, project);
                }
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
                Updater.update(UpdateType.UPDATE_PROJECT, project);
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

        //filterDataWrapper.setPredicate(filterPredicate);
        projectsTable.setItems(sortedList);
        sortedList.comparatorProperty().bind(projectsTable.comparatorProperty());
        projectsCountLabel.setText(String.valueOf(sortedList.size()));
        projectsProjectsLabel.setText(AllData.formatProjects(sortedList.size()));

    }


    private synchronized void initClosing() {

        if (AllData.primaryStage != null) {

            AllData.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {

                    saveBase();

                    closeAllWindows();

                    if (!AllData.editProjectWindowControllers.isEmpty()) {
                        event.consume();
                    }
                    else {
                        Platform.exit();
                        System.exit(0);
                    }

                }
            });
        }
    }



    public void sortTableProjects() {

        tableProjects.sort(new Comparator<Map.Entry<Integer, Project>>() {
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

        filterDataWrapper.setPredicate(filterPredicate);
        projectsCountLabel.setText(String.valueOf(sortedList.size()));
        projectsProjectsLabel.setText(AllData.formatProjects(sortedList.size()));

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


        usersLoggedChoiceBox.setItems(AllUsers.getUsersLogged());

        if (!usersLoggedChoiceBox.getItems().contains(AllData.toLoginWindow)) {
            usersLoggedChoiceBox.getItems().add(AllData.toLoginWindow);
        }

        usersLoggedChoiceBox.setValue(AllUsers.getOneUser(myIdNumber).getFullName());

        usersLoggedChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String selectUser = usersLoggedChoiceBox.getValue();

                if (selectUser != null && !selectUser.isEmpty()) {
                    if (selectUser.equalsIgnoreCase(AllData.toLoginWindow)) {

                        closeAllWindows();

                        AllData.rootLayout.setCenter(null);
                        AllData.mainApp.showLoginWindow();
                    }
                    else if (!selectUser.equalsIgnoreCase(AllUsers.getOneUser(myIdNumber).getFullName())) {

                        closeAllWindows();

                        User user = AllUsers.getOneUserForFullName(selectUser);

                        Role role = user.getRole();

                        if (role.equals(Role.DESIGNER)) {
                            AllData.rootLayout.setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            AllData.mainApp.showTableProjectsDesigner();
                        }
                        else if (role.equals(Role.MANAGER)) {
                            AllData.rootLayout.setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            AllData.mainApp.showTableProjectsManager();
                        }
                        else if (role.equals(Role.ADMIN)) {
                            AllData.rootLayout.setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            AllData.mainApp.showAdminWindow();
                        }
                    }
                }
            }
        });
    }

    private void closeAllWindows() {

        AllData.rebuildEditProjectsControllers();

        if (AllData.statisticManagerStage != null && AllData.statisticManagerStage.isShowing()) {
            AllData.statisticManagerStage.close();
        }

        if (AllData.createUserWindow != null && AllData.createUserWindow.isShowing()) {
            AllData.createUserWindowController.handleCancelButton();
            AllData.staffWindowController.handleCloseButton();
        }
        if (AllData.staffWindowStage != null && AllData.staffWindowStage.isShowing()) {
            AllData.staffWindowStage.close();
        }
        if (AllData.countSalaryWindow != null && AllData.countSalaryWindow.isShowing()) {
            AllData.countSalaryWindow.close();
        }

        if (AllData.countSalaryWindow != null && AllData.countSalaryWindow.isShowing()) {
            AllData.countSalaryWindowController.handleCloseButton();
        }
        if (AllData.createProjectWindow != null && AllData.createProjectWindow.isShowing()) {
            AllData.createProjectWindowController.handleCancelButton();
        }
        if (!AllData.editProjectWindowControllers.isEmpty()) {
            for (EditProjectWindowController controller : AllData.editProjectWindowControllers.values()) {
                controller.handleRevertButton();
                controller.handleCloseButton();
            }
        }
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

        List<Project> decadeProjects = AllData.getAllProjectsForPeriod(from, till);

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

        List<Project> myProjects = AllData.getAllProjectsForPeriod(from, till);
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
        initializeTable();
    }



    /** Три метода для чекбокса и двух дейтпикеров
     * сначала запрашивают проверку дат на валидность,
     * затем передают дальнейшее действие методу handleFilters()
     * который фильтрует filterData в зависимости от состояния
     * чекбокса и дейтпикеров.
     * */


    public void handleShowArchiveProjectsCheckBox() {
        handleFilters();
        initializeTable();
    }

    public void handleFromDatePicker() {
        checkDatePicker(fromDatePicker);
        handleFilters();
        initializeTable();
    }

    public void handleTillDatePicker() {
        checkDatePicker(tillDatePicker);
        handleFilters();
        initializeTable();
    }

    private void checkDatePicker(Node node) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (fromDate.isAfter(LocalDate.now())) {
                fromDatePicker.setValue(LocalDate.now());
                fromDate = LocalDate.now();
            }
            if (tillDate.isAfter(LocalDate.now())) {
                tillDatePicker.setValue(LocalDate.now());
                tillDate = LocalDate.now();
            }

            if (fromDate.compareTo(tillDate) > 0) {
                if (node == fromDatePicker) {
                    fromDatePicker.setValue(tillDate);
                }
                else {
                    tillDatePicker.setValue(fromDate);
                }
            }
        }
        else if (fromDate != null) {
            if (fromDate.isAfter(LocalDate.now())) {
                fromDatePicker.setValue(LocalDate.now());
            }
        }
        else if (tillDate != null) {
            if (tillDate.isAfter(LocalDate.now())) {
                tillDatePicker.setValue(LocalDate.now());
            }
        }
    }


    public void handleFilters() {

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (showArchiveProjectsCheckBox.isSelected()) {
            //tableProjects = FXCollections.observableArrayList(AllData.getAllProjects().entrySet());
            tableProjects.clear();
            tableProjects.addAll(AllData.getAllProjects().entrySet());
        }
        else {
            //tableProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
            tableProjects.clear();
            tableProjects.addAll(AllData.getActiveProjects().entrySet());
        }

        /*filterData = new FilteredList<>(tableProjects, p -> true);
        filterDataWrapper = new FilteredList<>(filterData, filterPredicate);
        sortedList = new SortedList<>(filterDataWrapper);*/

        if (fromDate != null && tillDate != null) {
            filterData.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
                @Override
                public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {

                    Project pr = integerProjectEntry.getValue();

                    if (pr.getWork().isEmpty()) {
                        LocalDate dateCreated = AllData.parseDate(pr.getDateCreationString());
                        if (dateCreated.compareTo(fromDate) >= 0 && dateCreated.compareTo(tillDate) <= 0) {
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                    else {
                        if (pr.containsWorkTime(fromDate, tillDate)) {
                            return true;
                        }
                    }

                    return false;
                }
            });
        }
        else {
            filterData.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
                @Override
                public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                    return true;
                }
            });

        }

        sortTableProjects();
    }



    public void handleDeleteDatePicker() {
        fromDatePicker.setValue(null);
        tillDatePicker.setValue(null);
        handleFilters();
    }

    public void handleUsersButton() {
        initClosing();

        if (AllData.staffWindowStage == null) {
            AllData.mainApp.showStaffWindow();
        }
        else {
            AllData.staffWindowStage.hide();
            AllData.staffWindowStage.show();
        }


    }

    public void handleSumButton() {
        int counter = 0;
        int notBudgeted = 0;
        for (Map.Entry<Integer, Project> entry : sortedList) {
            counter += entry.getValue().getBudget();
            if (entry.getValue().getBudget() == 0) {
                notBudgeted++;
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Сумма по указанным проектам");
        StringBuilder sb = new StringBuilder("Сумма по приведенным в таблице проектам = ");
        sb.append(AllData.formatInputInteger(counter)).append(" руб.");
        alert.setHeaderText(sb.toString());
        if (notBudgeted != 0) {
            StringBuilder sb0 = new StringBuilder();
            if (notBudgeted == sortedList.size()) {
                sb0.append("Примечание: в данной выборке\n");
                sb0.append("отсутствует внесенная сметная стоимость у всех проектов.\n");
                sb0.append("Это значит, что подсчитать сумму невозможно");
            }
            else {

                double percent = ((double) sortedList.size()) / 100;
                double part = ((double) notBudgeted) / percent;
                BigDecimal partDec = new BigDecimal(Double.toString(part));
                partDec = partDec.setScale(2, RoundingMode.HALF_UP);
                part = partDec.doubleValue();

                sb0.append("Примечание: в данной выборке\n");
                sb0.append("отсутствует внесенная сметная стоимость у ").append(part).append("% проектов.\n");
                sb0.append("Это значит, что подсчитанная сумма неточна");
            }
            alert.setContentText(sb0.toString());
        }
        alert.showAndWait();
    }


    public void handleNewProjectButton() {
        AllData.mainApp.showCreateProjectWindow();
    }

    public void handleBatchArchiveButton() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(AllData.pathToDownloads));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showOpenDialog(AllData.primaryStage);

        StringBuilder sb = new StringBuilder("");
        String input = "";

        if (file != null && file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
                while (reader.ready()) {
                    sb.append(reader.readLine()).append("\n");
                }
            } catch (FileNotFoundException e) {
                AllData.updateAllStatus("TableProjectsManagerController - ошибка чтения файла со списком папок в архив: FileNotFoundException");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            } catch (IOException e) {
                AllData.updateAllStatus("TableProjectsManagerController - ошибка чтения файла со списком папок в архив: IOException");
                AllData.logger.error(AllData.status);
                AllData.logger.error(e.getMessage(), e);
            }
        }

        if (sb.length() > 0) {
            input = sb.toString();
        }

        int counter = 0;

        if (!input.isEmpty()) {
            String[] intputParts = input.split("\n");
            for (String line : intputParts) {
                Pattern p = Pattern.compile("id-\\d+");
                Matcher m = p.matcher(line);
                int idProjectParsed = 0;
                if (m.find()) {
                    String n = m.group();
                    n = n.replaceAll("id-", "");
                    try {
                        idProjectParsed = Integer.parseInt(n);
                    } catch (NumberFormatException e) {
                    }
                    if (idProjectParsed != 0) {
                        counter++;
                        AllData.changeProjectArchiveStatus(idProjectParsed, true);
                    }
                }
            }
        }
        if (counter > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Перевод в архив успешен");
            alert.setHeaderText("Переведено в архив " + counter + " " + AllData.formatProjects(counter) + ".");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Перевод в архив не удался");
            alert.setHeaderText("Ни один проект не переведен в архив.");
            alert.showAndWait();
        }
    }

    public void handleReloadButton() {
        //initialize();
        updateManagerWindow();
    }

    public void handleStatisticButton() {

        //AllData.mainApp.showStatisticManagerWindow();

        if (AllData.statisticManagerStage != null) {
            AllData.statisticManagerStage.hide();
            AllData.statisticManagerStage.show();
        }
        else {
            AllData.mainApp.showStatisticManagerWindow();
        }
    }

    public void handleAbout() {
        AllData.mainApp.showAboutWindow();
    }

    public void updateStatus() {
        statusLabel.setText(AllData.status);
    }


    /** Вытащенный в отдельный метод кусок кода из метода initializeTable()
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

    public void handleClearLoggedUsersButton() {
        AllData.mainApp.showDeleteLoggedUserWindow();
    }

    public void handleExitButton() {

        saveBase();

        closeAllWindows();

        if (AllData.editProjectWindowControllers.isEmpty()) {
            Platform.exit();
            System.exit(0);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Не удалось выйти из программы");
            alert.setHeaderText("Ошибка выхода из программы.\nПопробуйте еще раз.");
            alert.showAndWait();
        }
    }

    // А здесть проверяется булин, и если false, то останавливается закрытие программы
    private void saveBase() {
        Loader loader = new Loader();
        boolean writed = loader.save();
        if (!writed) {
            alertSerialize();
        }
    }

    private void alertSerialize() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка записи базы");
        alert.setHeaderText("Не удалось записать базу в файл");
        alert.showAndWait();
    }


    private void writeCSV() {

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV file", "*.csv");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(AllData.pathToDownloads));


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

        File file = chooser.showSaveDialog(AllData.primaryStage);

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
                AllData.logger.error(ex.getMessage(), ex);

            }
        }
    }



    private void writeText() {

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(AllData.pathToDownloads));

        StringBuilder fileName = new StringBuilder("Время за ");

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

        fileName.append(" по состоянию на ").append(AllData.formatDate(LocalDate.now()));

        chooser.setInitialFileName(fileName.toString());

        File file = chooser.showSaveDialog(AllData.primaryStage);

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

                                String name = AllUsers.getOneUser(e.getKey()).getNameLogin();
                                if (AllUsers.getOneUser(e.getKey()).getFullName() != null && !AllUsers.getOneUser(e.getKey()).getFullName().isEmpty()) {
                                    name = AllUsers.getOneUser(e.getKey()).getFullName();
                                }
                                sb.append(name).append(" = ");
                                sb.append(AllData.formatWorkTime(e.getValue())).append(" ");
                                sb.append(AllData.formatHours(String.valueOf(e.getValue()))).append("\n");
                                counter += AllData.doubleToInt(e.getValue());
                                projectCounter += AllData.doubleToInt(e.getValue());
                                workdayCounter += AllData.doubleToInt(e.getValue());
                            }
                            if (wd.getWorkTimeMap().size() > 1) {
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
                AllData.logger.error(ex.getMessage(), ex);

            }
        }

    }


    public void testDelete() {
        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundDeleteTime();

        //AllData.addWorkTime(12, LocalDate.now(),5, 20);
    }


    class ManagerCell extends TableCell<Map.Entry<Integer, Project>, Boolean> {
        private Button openFolderButton = new Button("Туда");
        private Button manageButton = new Button("Инфо");
        private CheckBox archiveCheckBox = new CheckBox("Архивный");
        private Button deleteButton = new Button("X");

        String startPath = "/Volumes/design/";

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            if (empty) {
                setGraphic(null);
            }
            else {

                Map.Entry<Integer, Project> entry = getTableView().getItems().get(getIndex());

                if (entry.getValue().isArchive()) {
                    openFolderButton.setDisable(true);
                    archiveCheckBox.setSelected(true);
                    setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");

                    if (AllUsers.getOneUser(myIdNumber).isRetired()) {
                        manageButton.setDisable(true);
                        archiveCheckBox.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                    else {
                        manageButton.setDisable(false);
                        archiveCheckBox.setDisable(false);
                        deleteButton.setDisable(false);
                    }
                }
                else {
                    openFolderButton.setDisable(false);
                    archiveCheckBox.setSelected(false);
                    setStyle(null);

                    if (AllUsers.getOneUser(myIdNumber).isRetired()) {
                        openFolderButton.setDisable(true);
                        manageButton.setDisable(true);
                        archiveCheckBox.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                    else {
                        openFolderButton.setDisable(false);
                        manageButton.setDisable(false);
                        archiveCheckBox.setDisable(false);
                        deleteButton.setDisable(false);
                    }
                }


                openFolderButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        String projectName = entry.getValue().getDescription().split(" +- +")[0].trim() + " id-" + entry.getKey();
                        String path = startPath + entry.getValue().getCompany() + "/" + projectName;

                        if (entry.getValue().getFolderPath() != null) {

                            try {
                                browsDir(entry.getValue().getFolderPath());
                            } catch (Exception e) {
                                try {
                                    browsDir(path);
                                } catch (Exception e1) {
                                    showAlertOpenFolder(entry.getKey());
                                }
                            }
                        }
                        else {
                            try {
                                browsDir(path);
                            } catch (Exception e) {
                                showAlertOpenFolder(entry.getKey());
                            }
                        }
                    }
                });

                manageButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        initClosing();
                        AllData.mainApp.showEditProjectWindow(entry.getKey());
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
                                setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                            }
                        }
                        handleFilters();
                        initializeTable();
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
                            if (AllData.staffWindowController != null && AllData.staffWindowStage.isShowing()) {
                                AllData.staffWindowController.initializeTable();
                            }
                            initializeTable();
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

            archiveCheckBox.setCursor(Cursor.HAND);

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

        private void browsDir(String path) throws Exception {
            Desktop.getDesktop().browseFileDirectory(new File(path));
        }

        private void showAlertOpenFolder(int projectID) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Не удалось открыть папку");
            alert.setHeaderText("Не удалось открыть папку");
            alert.setContentText("Не удалось найти и открыть\nпапку проекта id-" + projectID);
            alert.showAndWait();
        }

    } // Конец класса ManagrtCell



    class EditingCellString extends TableCell<Map.Entry<Integer, Project>, String> {

        private TextField textField;

        public EditingCellString() {
        }

        @Override
        public void startEdit() {

            Integer num = (Integer) this.getTableView().getColumns().get(1).getCellObservableValue(this.getIndex()).getValue();
            Project p = AllData.getAnyProject(num);
            if (!p.isArchive()) {
                if (!isEmpty()) {
                    super.startEdit();
                    createTextField();
                    setText(null);
                    setGraphic(textField);
                    textField.selectAll();
                }
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