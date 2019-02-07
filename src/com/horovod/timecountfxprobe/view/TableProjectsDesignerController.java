package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class TableProjectsDesignerController {

    private ObservableList<Map.Entry<Integer, Project>> tableProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
    private FilteredList<Map.Entry<Integer, Project>> filterData = new FilteredList<>(tableProjects, p -> true);
    private Predicate<Map.Entry<Integer, Project>> filterPredicate = new Predicate<Map.Entry<Integer, Project>>() {
        @Override
        public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
            return true;
        }
    };
    private FilteredList<Map.Entry<Integer, Project>> filterDataWrapper = new FilteredList<>(filterData, filterPredicate);


    private ObservableList<String> datesForChart;
    private ObservableList<XYChart.Data<String, Integer>> workTimeForChart;
    private XYChart.Series<String, Integer> series;

    //private DoubleProperty dayWorkSumProperty = new SimpleDoubleProperty(0);


    @FXML
    private TextField filterField;

    @FXML
    private Button deleteSearchTextButton;

    @FXML
    private CheckBox showMyProjectsCheckBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker tillDatePicker;

    @FXML
    private Button clearDatePicker;

    @FXML
    private LineChart<String, Integer> decadeLineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private Label dayWorkSumLabel;

    @FXML
    private Label ratingPositionLabel;

    @FXML
    private Button buttonReload;

    @FXML
    private Button buttonStatistic;

    @FXML
    private Label aboutProgramLabel;

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
    private TableColumn<Map.Entry<Integer, Project>, String> columnTime;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnCompany;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnManager;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnDescription;


    /** Временные кнопки под тестирование */

    /*@FXML
    private Button testAddButton;

    @FXML
    private Button testDeleteButton;*/


    public TextField getFilterField() {
        return filterField;
    }



    @FXML
    public void initialize() {

        /** TODO сюда или в mainApp добавить setStageTitle("Имя пользователя")
         * */

        /** TODO В чистовой версии перенести все поля по суммам рабочего времени за день, неделю, месяц и год
         * сюда внутрь класса, а в AllData оставить только глобальные суммы по всем дизайнерам */

        // Отработка методов данных
        AllData.deleteZeroTime();
        AllData.rebuildDesignerDayWorkSumProperty();
        LocalDate today = LocalDate.now();
        AllData.rebuildDesignerWeekWorkSumProperty(today.getYear(), today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        AllData.rebuildDesignerMonthWorkSumProperty(today.getYear(), today.getMonthValue());
        AllData.rebuildDesignerYearWorkSumProperty(today.getYear());

        handleFilters();

        sortTableProjects();

        initializeTable();

        dayWorkSumLabel.textProperty().bind(AllData.designerDayWorkSumProperty().asString());
        ratingPositionLabel.textProperty().bind(AllData.designerRatingPositionProperty().asString());
        AllData.rebuildDesignerRatingPosition();

        initializeChart();
        initLoggedUsersChoiceBox();
        initClosing();

    }

    public void initializeTable() {

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
                return new DesignerCell();
            }
        });

        columnID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Integer> param) {
                return param.getValue().getValue().idNumberProperty().asObject();
            }
        });

        columnID.setStyle("-fx-alignment: CENTER;");


        Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>> cellFactory =
                (TableColumn<Map.Entry<Integer, Project>, String> p) -> new EditingCell(FillChartMode.TIME);

        columnTime.setCellFactory(cellFactory);

        columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                // Для списка менеджера – просто все рабочее время
                //return param.getValue().getValue().workSumProperty();

                int time = param.getValue().getValue().getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), LocalDate.now());
                return new SimpleStringProperty(AllData.formatWorkTime(AllData.intToDouble(time)));
            }
        });

        columnTime.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String> event) {


                //double newTimeDouble = Double.parseDouble(event.getNewValue());

                Project project = (Project) event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue();
                double newTimeDouble = AllData.getDoubleFromText(AllData.intToDouble(project.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), LocalDate.now())), event.getNewValue(), 1);
                AllData.addWorkTime(project.getIdNumber(), LocalDate.now(), AllUsers.getCurrentUser(), newTimeDouble);

                // код для мгновенного обновления страниц у менеджера
                if (AllData.editProjectWindowControllers.containsKey(project.getIdNumber())) {
                    AllData.editProjectWindowControllers.get(project.getIdNumber()).initializeTable();
                }
                if (AllData.statisticWindowController != null) {
                    AllData.statisticWindowController.initialize();
                }
                initialize();

                /*filterField.setText("-");
                filterField.clear();*/
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




        SortedList<Map.Entry<Integer, Project>> sortedList = new SortedList<>(filterDataWrapper, new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                int compare = compareTime(o1, o2);
                if (compare == 0) {
                    compare = o2.getKey().compareTo(o1.getKey());
                }
                return compare;
            }
        });

        projectsTable.setItems(sortedList);
        sortedList.comparatorProperty().bind(projectsTable.comparatorProperty());

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
            workTimeInTable = String.valueOf(AllData.intToDouble(project.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), LocalDate.now())));
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
        return false;
    }




    public void initLoggedUsersChoiceBox() {


        usersLoggedChoiceBox.setItems(AllUsers.getUsersLogged());

        if (!usersLoggedChoiceBox.getItems().contains(AllData.toLoginWindow)) {
            usersLoggedChoiceBox.getItems().add(AllData.toLoginWindow);
        }

        usersLoggedChoiceBox.setValue(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName());

        usersLoggedChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String selectUser = usersLoggedChoiceBox.getValue();

                if (selectUser != null) {
                    if (selectUser.equalsIgnoreCase(AllData.toLoginWindow)) {

                        closeAllWindows();

                        AllData.rootLayout.setCenter(null);
                        AllData.mainApp.showLoginWindow();
                    }
                    else if (!selectUser.equalsIgnoreCase(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName())) {

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

        if (AllData.statisticStage != null && AllData.statisticStage.isShowing()) {
            AllData.statisticStage.close();
        }
        if (!AllData.infoProjectWindowControllers.isEmpty()) {
            for (InfoProjectWindowController controller : AllData.infoProjectWindowControllers.values()) {
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

        List<Project> myProjects = AllData.getAllProjectsForDesignerAndPeriod(AllUsers.getCurrentUser(), from, till);
        Map<String, Integer> decadeWorkSums = new TreeMap<>();

        for (Project p : myProjects) {
            for (WorkTime wt : p.getWork()) {
                if (wt.getDesignerID() == AllUsers.getCurrentUser()) {
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
        }

        workTimeForChart.clear();

        for (String s : datesForChart) {
            if (decadeWorkSums.containsKey(s)) {
                workTimeForChart.add(new XYChart.Data<>(s, decadeWorkSums.get(s)));
            }
            else {
                workTimeForChart.add(new XYChart.Data<>(s, 0));
            }
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

    public void sortTableProjects() {
        tableProjects.sort(new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                int compare = compareTime(o1, o2);
                if (compare == 0) {
                    compare = o2.getKey().compareTo(o1.getKey());
                }
                return compare;
            }
        });
    }



    /** Три метода для чекбокса и двух дейтпикеров
     * сначала запрашивают проверку дат на валидность,
     * затем передают дальнейшее действие методу handleFilters()
     * который фильтрует filterData в зависимости от состояния
     * чекбокса и дейтпикеров.
     * */


    public void handleShowMyProjectsCheckBox() {
        checkDatePicker(showMyProjectsCheckBox);
        handleFilters();
    }

    public void handleFromDatePicker() {
        checkDatePicker(fromDatePicker);
        handleFilters();
    }

    public void handleTillDatePicker() {
        checkDatePicker(tillDatePicker);
        handleFilters();
    }

    private void checkDatePicker(Node node) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (fromDate.compareTo(tillDate) > 0) {
                if (node == showMyProjectsCheckBox) {
                    fromDatePicker.setValue(null);
                    tillDatePicker.setValue(null);
                }
                else if (node == fromDatePicker) {
                    fromDatePicker.setValue(null);
                }
                else if (node == tillDatePicker) {
                    tillDatePicker.setValue(null);
                }
            }
        }
    }


    private void handleFilters() {

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (showMyProjectsCheckBox.isSelected()) {

                filterData.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
                    @Override
                    public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                        if (integerProjectEntry.getValue().containsWorkTime(AllUsers.getCurrentUser(), fromDate, tillDate)) {
                            return true;
                        }
                        return false;
                    }
                });

            }
            else {

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
        }
        else {
            if (showMyProjectsCheckBox.isSelected()) {

                filterData.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
                    @Override
                    public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                        if (integerProjectEntry.getValue().containsWorkTime(AllUsers.getCurrentUser())) {
                            return true;
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
        if (AllData.statisticStage == null) {
            AllData.mainApp.showStatisticWindow();
        }
        else {
            AllData.statisticStage.hide();
            AllData.statisticStage.show();
            AllData.statisticWindowController.initialize();
            LocalDate today = LocalDate.now();
            int y = today.getYear();
            int m = today.getMonthValue();
            AllData.statisticWindowController.initializeBarChart(FillChartMode.DAILY, LocalDate.of(y, m, 1) );
        }
    }

    public void handleAbout() {
        AllData.mainApp.showAboutWindow();
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

    private int compareTime(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {

        List<WorkTime> timeList1 = o1.getValue().getWork();
        List<WorkTime> timeList2 = o2.getValue().getWork();

        int time1 = 0;
        int time2 = 0;

        for (WorkTime wt1 : timeList1) {
            if ((wt1.getDesignerID() == AllUsers.getCurrentUser()) && (AllData.parseDate(wt1.getDateString()).equals(LocalDate.now()))) {
                time1 = wt1.getTime();
                break;
            }
        }

        for (WorkTime wt2 : timeList2) {
            if ((wt2.getDesignerID() == AllUsers.getCurrentUser()) && (AllData.parseDate(wt2.getDateString()).equals(LocalDate.now()))) {
                time2 = wt2.getTime();
                break;
            }
        }
        return Integer.compare(time2, time1);
    }


    public void handleClearLoggedUsersButton() {
        AllData.mainApp.showDeleteLoggedUserWindow();
    }


    public void handleExitButton() {

        saveBase();

        closeAllWindows();

        if (AllData.openInfoProjectStages.isEmpty()) {
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



    private synchronized void initClosing() {

        if (AllData.primaryStage != null) {

            AllData.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {

                    saveBase();

                    closeAllWindows();

                    if (!AllData.openInfoProjectStages.isEmpty()) {
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

    private void saveBase() {
        Loader loader = new Loader();
        try {
            loader.save();
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Не удалось записать базу в файл: IOException");
            alertSerialize(e.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
            updateStatus("Ошибка сериализации в XML: JAXBException");
            alertSerialize(e.toString());
        }
    }

    private void alertSerialize(String exceptionName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка записи в файл");
        alert.setHeaderText("Не удалось записать базу в файл: " + exceptionName);
        alert.showAndWait();
    }



    class DesignerCell extends TableCell<Map.Entry<Integer, Project>, Boolean> {

        private final Button openFolderButton = new Button("Туда");
        private final Button infoButton = new Button("Инфо");
        String startPath = "/Volumes/design/";

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            if (empty) {
                setGraphic(null);
            }
            else {

                Map.Entry<Integer, Project> entry = getTableView().getItems().get(getIndex());

                openFolderButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        String projectName = entry.getValue().getDescription().split(" - ")[0].trim() + " id-" + entry.getKey();
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

                infoButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //initClosing();
                        //AllData.mainApp.showEditProjectWindow(entry.getKey());
                        AllData.mainApp.showInfoProjectWindow(entry.getKey());
                    }
                });

                HBox hbox = new HBox();
                hbox.getChildren().addAll(openFolderButton, infoButton);
                hbox.setAlignment(Pos.CENTER);
                hbox.setSpacing(10);
                setGraphic(hbox);
            }
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

        {
            openFolderButton.setMinHeight(20);
            openFolderButton.setMaxHeight(20);
            openFolderButton.setStyle("-fx-font-size:10");

            infoButton.setMinHeight(20);
            infoButton.setMaxHeight(20);
            infoButton.setStyle("-fx-font-size:10");
        }

    } // Конец класса DesignerCell

}
