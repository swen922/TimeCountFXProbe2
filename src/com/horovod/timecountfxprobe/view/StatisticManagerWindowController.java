package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;

public class StatisticManagerWindowController {

    private ObservableList<String> users;
    private ObservableList<String> datesForBarChart;
    private ObservableList<XYChart.Data<String, Double>> workTimeForBarChart;
    private XYChart.Series<String, Double> seriesBars;

    private static final String daily = "По дням";
    private static final String monthly = "По месяцам";
    private static final String allUsers = "Все дизайнеры";

    private ObservableList<String> listFillModes;
    private ObservableList<Integer> yearsValues;
    private ObservableList<Month> monthsValues;


    @FXML
    private ChoiceBox<String> userForDayTextArea;

    @FXML
    private DatePicker selectDayDatePicker;

    @FXML
    private Button clearSelectDayButton;

    @FXML
    private TextArea selectedDayTextArea;

    @FXML
    private ChoiceBox<String> userForProjectTextArea;

    @FXML
    private TextField projectNumberTextField;

    @FXML
    private Button clearProjectNumberButton;

    @FXML
    private TextArea projectNumberTextArea;

    @FXML
    private Label todayWorkSumLabel;

    @FXML
    private Label weekWorkSumLabel;

    @FXML
    private Label monthWorkSumLabel;

    @FXML
    private Label yearWorkSumLabel;


    @FXML
    private ChoiceBox<String> fillModeChoiceBox;

    @FXML
    private ChoiceBox<Integer> yearChoiceBox;

    @FXML
    private ChoiceBox<Month> monthChoiceBox;

    @FXML
    private Button buttonReloadBarChart;

    @FXML
    private BarChart<String, Double> workSumsBarChart;

    @FXML
    private CategoryAxis xAxis;


    @FXML
    public void initialize() {

        initializeChoiceBoxes();

        LocalDate now = LocalDate.now();
        Year year = Year.from(now);
        Month month = now.getMonth();
        AllData.rebuildTodayWorkSumProperty();
        AllData.rebuildWeekWorkSumProperty(year.getValue(), now.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        AllData.rebuildMonthWorkSumProperty(year.getValue(), month.getValue());
        AllData.rebuildYearWorkSumProperty(year.getValue());
        initializeBarChart(FillChartMode.DAILY, LocalDate.of(year.getValue(), month.getValue(), 1));

        initWorkSumLabels();
        initUsersChoiceBoxes();
        initUserDateText();
    }

    private void initWorkSumLabels() {
        todayWorkSumLabel.textProperty().bind(AllData.todayWorkSumProperty().asString());
        weekWorkSumLabel.textProperty().bind(AllData.weekWorkSumPropertyProperty().asString());
        monthWorkSumLabel.textProperty().bind(AllData.monthWorkSumPropertyProperty().asString());
        yearWorkSumLabel.textProperty().bind(AllData.yearWorkSumPropertyProperty().asString());
    }

    private void sortUsers() {
        users.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    private void initUsersChoiceBoxes() {
        if (users == null) {
            users = FXCollections.observableArrayList();
        }
        for (User u : AllUsers.getActiveDesigners().values()) {
            users.add(u.getFullName());
        }
        sortUsers();
        users.add(0, allUsers);
        userForDayTextArea.setItems(users);
        userForDayTextArea.setValue(users.get(0));
        userForProjectTextArea.setItems(users);
        userForProjectTextArea.setValue(users.get(0));
    }


    private void initializeChoiceBoxes() {

        if (listFillModes == null) {
            listFillModes = FXCollections.observableArrayList();
            listFillModes.addAll(Arrays.asList(daily, monthly));
            fillModeChoiceBox.setItems(listFillModes);
        }

        fillModeChoiceBox.setValue(listFillModes.get(0));

        initializePeriodChoiceBoxes();

        fillModeChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String selectMode = fillModeChoiceBox.getValue();
                if (selectMode != null) {
                    LocalDate now = LocalDate.now();
                    switch (selectMode) {
                        case daily :
                            monthChoiceBox.setDisable(false);
                            Year year = Year.from(LocalDate.of(yearChoiceBox.getValue(), 1, 1));

                            Month month = monthChoiceBox.getValue();
                            if (month == null) {
                                month = Month.JANUARY;
                            }
                            LocalDate fromDate1 = LocalDate.of(year.getValue(), month.getValue(), 1);
                            initializeBarChart(FillChartMode.DAILY, fromDate1);
                            initializePeriodChoiceBoxes();
                            break;
                        case monthly :
                            monthChoiceBox.setDisable(true);
                            LocalDate fromDate2 = LocalDate.of(now.getYear(), 1, 1);
                            initializeBarChart(FillChartMode.MONTHLY, fromDate2);
                            initializePeriodChoiceBoxes();
                            break;
                    }
                }
            }
        });
    }

    private void initializePeriodChoiceBoxes() {

        if (yearsValues == null) {
            yearsValues = FXCollections.observableArrayList();
            for (int i = 2013; i <= LocalDate.now().getYear(); i++) {
                yearsValues.add(i);
            }
            yearChoiceBox.setItems(yearsValues);
            yearChoiceBox.setValue(LocalDate.now().getYear());
        }

        if (monthsValues == null) {
            monthsValues = FXCollections.observableArrayList();
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


        if (fillModeChoiceBox.getValue().equals(monthly)) {
            monthChoiceBox.setDisable(true);
        }
        else {
            monthChoiceBox.setDisable(false);
        }


        yearChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Integer year = yearChoiceBox.getValue();
                if (year != null) {
                    String modeString = fillModeChoiceBox.getValue();
                    FillChartMode mode = FillChartMode.DAILY;
                    if (modeString.equals(daily)) {
                        mode = FillChartMode.DAILY;
                    }
                    else {
                        mode = FillChartMode.MONTHLY;
                    }
                    Month month = Month.JANUARY;
                    if (!monthChoiceBox.isDisabled() && monthChoiceBox.getValue() != null) {
                        month = monthChoiceBox.getValue();
                    }
                    LocalDate fromDate = LocalDate.of(year, month.getValue(), 1);
                    initializeBarChart(mode, fromDate);
                }
            }
        });

        monthChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Month month = monthChoiceBox.getValue();
                if (month != null) {
                    String modeString = fillModeChoiceBox.getValue();
                    FillChartMode mode = FillChartMode.DAILY;
                    if (modeString.equals(monthly)) {
                        mode = FillChartMode.MONTHLY;
                    }
                    Year year = Year.from(LocalDate.of(yearChoiceBox.getValue(), 1, 1));
                    LocalDate fromDate = LocalDate.of(year.getValue(), month.getValue(), 1);
                    initializeBarChart(mode, fromDate);
                }
            }
        });
    }


    public void initializeBarChart(FillChartMode mode, LocalDate from) {

        if (datesForBarChart == null) {
            datesForBarChart = FXCollections.observableArrayList();
            xAxis.setCategories(datesForBarChart);
        }

        fillDatesBarChart(mode, from);

        if (workTimeForBarChart == null) {
            workTimeForBarChart = FXCollections.observableArrayList();
        }

        if (seriesBars == null) {
            seriesBars = new XYChart.Series<>();
            seriesBars.setData(workTimeForBarChart);
            workSumsBarChart.getData().clear();
            workSumsBarChart.getData().add(seriesBars);
        }

        fillXYBarChartSeries(mode, from);
    }


    private void fillDatesBarChart(FillChartMode mode, LocalDate from) {

        datesForBarChart.clear();

        if (mode.equals(FillChartMode.DAILY)) {
            int max = from.getMonth().length(Year.from(from).isLeap());
            for (int i = 1; i <= max; i++) {
                datesForBarChart.add(String.valueOf(i));
            }
        }
        else if (mode.equals(FillChartMode.MONTHLY)) {
            datesForBarChart.addAll(Arrays.asList(
                    Month.JANUARY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.FEBRUARY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.MARCH.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.APRIL.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.MAY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.JUNE.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.JULY.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.AUGUST.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.SEPTEMBER.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.OCTOBER.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.NOVEMBER.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                    Month.DECEMBER.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            ));
        }
    }


    private void fillXYBarChartSeries(FillChartMode mode, LocalDate from) {

        Map<String, Double> workSums = new TreeMap<>();

        if (mode.equals(FillChartMode.DAILY)) {
            for (int i = 0; i < from.getMonth().length(Year.from(from).isLeap()); i++) {
                LocalDate oneDay = from.plusDays(i);
                List<Project> tmp = AllData.getAllProjectsForDate(oneDay);
                int sum = 0;
                for (Project p : tmp) {
                    sum += p.getWorkSumForDate(oneDay);
                }
                workSums.put(String.valueOf(i + 1), AllData.intToDouble(sum));
            }
        }
        else if (mode.equals(FillChartMode.MONTHLY)) {

            Year year = Year.from(from);

            for (int i = 0; i < 12; i++) {
                Month month = from.getMonth().plus(i);
                List<Project> monthlyProjects = AllData.getAllProjectsForMonth(year, month);
                int sum = 0;
                for (Project p : monthlyProjects) {
                    LocalDate fromdate = LocalDate.of(year.getValue(), month.getValue(), 1);
                    LocalDate tillDate = LocalDate.of(year.getValue(), month.getValue(), month.length(year.isLeap()));
                    sum += p.getWorkSumForPeriod(fromdate, tillDate);
                }
                workSums.put(month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()), AllData.intToDouble(sum));
            }
        }

        workTimeForBarChart.clear();

        for (Map.Entry<String, Double> entry : workSums.entrySet()) {
            workTimeForBarChart.add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
    }

    private void initUserDateText() {
        userForDayTextArea.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleSelectDayDatePicker();
            }
        });
    }



    public void handleSelectDayDatePicker() {

        LocalDate localDate = selectDayDatePicker.getValue();
        List<Project> myProjects = null;
        selectedDayTextArea.clear();
        int selectedUserID = 0;

        if (localDate != null) {
            if (userForDayTextArea.getValue().equalsIgnoreCase(allUsers)) {
                myProjects = AllData.getAllProjectsForDate(localDate);
            }
            else {
                selectedUserID = AllUsers.getOneUserForFullName(userForDayTextArea.getValue()).getIDNumber();
                myProjects = AllData.getAllProjectsForDesignerAndDate(selectedUserID, localDate);
            }
        }

        if (myProjects == null || myProjects.isEmpty()) {
            selectedDayTextArea.setText("В этот день нет рабочего времени");
        }
        else {
            myProjects.sort(new Comparator<Project>() {
                @Override
                public int compare(Project o1, Project o2) {
                    int o1WorkSum = o1.getWorkSumForDate(localDate);
                    int o2WorkSum = o2.getWorkSumForDate(localDate);
                    return Integer.compare(o2WorkSum, o1WorkSum);
                }
            });

            int sum = 0;

            StringBuilder sb = new StringBuilder("В этот день ");
            if (selectedUserID != 0) {
                sb.append("у дизайнера ").append(AllUsers.getOneUser(selectedUserID).getFullName()).append(" ");
            }
            sb.append("было время в проектах:\n");
            sb.append("-------------------\n");

            for (Project p : myProjects) {

                sb.append("id-").append(p.getIdNumber());

                if (selectedUserID == 0) {
                    sb.append(":\n\n");

                    for (User u : AllUsers.getDesignersPlusDeleted().values()) {
                        if (p.containsWorkTime(u.getIDNumber(), localDate)) {
                            int todayWorkSum = p.getWorkSumForDesignerAndDate(u.getIDNumber(), localDate);
                            sb.append(u.getFullName()).append(" = ").append(AllData.formatWorkTime(AllData.intToDouble(todayWorkSum)));
                            sb.append(" ").append(AllData.formatHours(String.valueOf(AllData.intToDouble(todayWorkSum)))).append("\n");
                            sum += todayWorkSum;
                        }
                    }
                    System.out.println("handleSelectDayDatePicker()");
                    System.out.println("sum = " + sum);
                    System.out.println(p.getWorkSumForDate(localDate));
                    System.out.println("");
                }
                else {
                    if (p.containsWorkTime(selectedUserID, localDate)) {
                        int todayWorkSum = p.getWorkSumForDesignerAndDate(selectedUserID, localDate);
                        sb.append(" = ");
                        sb.append(AllData.formatWorkTime(AllData.intToDouble(todayWorkSum)));
                        sb.append(" ").append(AllData.formatHours(String.valueOf(AllData.intToDouble(todayWorkSum)))).append("\n");
                        sum += todayWorkSum;
                    }
                }
            }
            sb.append("\n");

            sb.append("-------------------\n");
            String hourSum = AllData.formatWorkTime(AllData.intToDouble(sum));
            sb.append("Итого  –  ").append(hourSum).append(" ").append(AllData.formatHours(hourSum));
            selectedDayTextArea.setText(sb.toString());
        }
    }

    public void handleClearSelectDayButton() {
        selectDayDatePicker.setValue(null);
        selectedDayTextArea.clear();
    }

    public void handleProjectNumberTextField() {

        projectNumberTextArea.clear();
        String projectString = projectNumberTextField.getText();
        Integer projectIDnumber;
        if (projectString != null && !projectString.isEmpty()) {
            try {
                projectIDnumber = Integer.parseInt(projectString);
            } catch (NumberFormatException e) {
                projectNumberTextArea.setText("Введите корректный номер проекта");
                return;
            }

            if (!AllData.isProjectExist(projectIDnumber)) {
                projectNumberTextArea.setText("Такого проекта не существует, либо он удален в архив");
            }
            else {

                List<WorkTime> allWorks = new ArrayList<>();
                allWorks.addAll(AllData.getOneActiveProject(projectIDnumber).getWork());

                if (allWorks.isEmpty()) {
                    projectNumberTextArea.setText("В этом проекте пока нет рабочего времени");
                }
                else {

                    allWorks.sort(new Comparator<WorkTime>() {
                        @Override
                        public int compare(WorkTime o1, WorkTime o2) {
                            return o2.getDateString().compareTo(o1.getDateString());
                        }
                    });

                    int sum = 0;
                    StringBuilder sb = new StringBuilder("В этом проекте было следуюшее рабочее время:\n");
                    sb.append("-------------------\n");
                    for (WorkTime wt : allWorks) {
                        sb.append(wt.getDateString()).append("  —  ");
                        String hour = AllData.formatWorkTime(AllData.intToDouble(wt.getTime()));
                        sb.append(hour).append(" ").append(AllData.formatHours(hour)).append("\n");
                        sum += wt.getTime();
                    }
                    sb.append("-------------------\n");
                    String hourSum = AllData.formatWorkTime(AllData.intToDouble(sum));
                    sb.append("Итого  –  ").append(hourSum).append(" ").append(AllData.formatHours(hourSum));
                    projectNumberTextArea.setText(sb.toString());

                }
            }
        }
    }

    public void handleClearProjectNumberButton() {
        projectNumberTextField.setText("");
        projectNumberTextArea.clear();
    }

    public void handleButtonReloadBarChart() {
        String modeString = fillModeChoiceBox.getValue();
        int year = yearChoiceBox.getValue();
        FillChartMode mode = FillChartMode.MONTHLY;
        Month month = Month.JANUARY;
        if (modeString.equals(daily)) {
            mode = FillChartMode.DAILY;
            month = monthChoiceBox.getValue();
        }

        LocalDate date = LocalDate.of(year, month.getValue(), 1);
        initializeBarChart(mode, date);
    }

    public void handleCloseButton() {
        AllData.statisticStage.close();
    }

}
