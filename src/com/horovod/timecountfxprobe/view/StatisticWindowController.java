package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.*;

public class StatisticWindowController {

    private ObservableList<String> datesForBarChart;
    private ObservableList<XYChart.Data<String, Double>> workTimeForBarChart;
    private XYChart.Series<String, Double> seriesBars;

    private static final String daily = "По дням";
    private static final String monthly = "По месяцам";

    private ObservableList<String> listFillModes;
    private ObservableList<Integer> yearsValues;
    private ObservableList<Month> monthsValues;


    @FXML
    private DatePicker selectDayDatePicker;

    @FXML
    private Button clearSelectDayButton;

    @FXML
    private TextArea selectedDayTextArea;

    @FXML
    private TextField projectNumberTextField;

    @FXML
    private Button clearProjectNumberButton;

    @FXML
    private TextArea projectNumberTextArea;

    @FXML
    private Label yearWorkSumLabel;

    @FXML
    private Label monthWorkSumLabel;

    @FXML
    private Label weekWorkSumLabel;


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

        /*selectDayDatePicker.setValue(null);
        selectedDayTextArea.setText("");
        projectNumberTextField.setText("");
        projectNumberTextArea.setText("");*/

        initializeChoiceBoxes();

        LocalDate now = LocalDate.now();
        Year year = Year.from(now);
        Month month = now.getMonth();

        initializeBarChart(FillChartMode.DAILY, LocalDate.of(year.getValue(), month.getValue(), 1));

        yearWorkSumLabel.textProperty().bind(AllData.designerYearWorkSumProperty().asString());
        monthWorkSumLabel.textProperty().bind(AllData.designerMonthWorkSumProperty().asString());
        weekWorkSumLabel.textProperty().bind(AllData.designerWeekWorkSumProperty().asString());
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
                    if (modeString.equals(monthly)) {
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
                    if (modeString.equals(daily)) {
                        mode = FillChartMode.DAILY;
                    }
                    else {
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
                List<Project> tmp = AllData.getAllProjectsForDesignerAndDate(AllUsers.getCurrentUser(), oneDay);
                int sum = 0;
                for (Project p : tmp) {
                    sum += p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), oneDay);
                }
                workSums.put(String.valueOf(i + 1), AllData.intToDouble(sum));
            }
        }
        else if (mode.equals(FillChartMode.MONTHLY)) {

            Year year = Year.from(from);

            for (int i = 0; i < 12; i++) {
                Month month = from.getMonth().plus(i);
                List<Project> monthlyProjects = AllData.getAllProjectsForDesignerAndMonth(AllUsers.getCurrentUser(), year.getValue(), month.getValue());
                int sum = 0;
                for (Project p : monthlyProjects) {
                    LocalDate fromdate = LocalDate.of(year.getValue(), month.getValue(), 1);
                    LocalDate tillDate = LocalDate.of(year.getValue(), month.getValue(), month.length(year.isLeap()));
                    sum += p.getWorkSumForDesignerAndPeriod(AllUsers.getCurrentUser(), fromdate, tillDate);
                }
                workSums.put(month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()), AllData.intToDouble(sum));
            }
        }

        workTimeForBarChart.clear();

        Iterator<Map.Entry<String, Double>> iter = workSums.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Double> entry = iter.next();
            workTimeForBarChart.add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
    }



    public void handleSelectDayDatePicker() {

        LocalDate localDate = selectDayDatePicker.getValue();
        List<Project> myProjects = null;
        selectedDayTextArea.clear();

        if (localDate != null) {

            if (localDate.compareTo(LocalDate.now()) > 0) {
                selectDayDatePicker.setValue(null);
                selectedDayTextArea.setText("");
                return;
            }
            else {
                myProjects = AllData.getAllProjectsForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
            }
        }
        if (myProjects == null || myProjects.isEmpty()) {
            selectedDayTextArea.setText("В этот день у меня нет рабочего времени");
        }
        else {

            myProjects.sort(new Comparator<Project>() {
                @Override
                public int compare(Project o1, Project o2) {
                    int o1WorkSum = o1.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
                    int o2WorkSum = o2.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
                    return Integer.compare(o2WorkSum, o1WorkSum);
                }
            });

            int sum = 0;
            StringBuilder sb = new StringBuilder("В этот день у меня было время в проектах:\n");
            sb.append("-------------------\n");
            for (Project p : myProjects) {
                sb.append("id-").append(p.getIdNumber()).append("  —  ");
                String hour = AllData.formatWorkTime(AllData.intToDouble(p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate)));
                sb.append(hour);
                sb.append(" ").append(AllData.formatHours(hour)).append("\n");
                sum += p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
            }
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

                List<WorkTime> myWorks = AllData.getOneActiveProject(projectIDnumber).getWorkTimeForDesigner(AllUsers.getCurrentUser());

                if (myWorks.isEmpty()) {
                    projectNumberTextArea.setText("В этом проекте у меня нет рабочего времени");
                }
                else {

                    myWorks.sort(new Comparator<WorkTime>() {
                        @Override
                        public int compare(WorkTime o1, WorkTime o2) {
                            return o2.getDateString().compareTo(o1.getDateString());
                        }
                    });

                    int sum = 0;
                    StringBuilder sb = new StringBuilder("В этом проекте у меня было следуюшее рабочее время:\n");
                    sb.append("-------------------\n");
                    for (WorkTime wt : myWorks) {
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
        FillChartMode mode = null;
        Month month = Month.JANUARY;
        if (modeString.equals(daily)) {
            mode = FillChartMode.DAILY;
            month = monthChoiceBox.getValue();
        }
        else {
            mode = FillChartMode.MONTHLY;
        }
        LocalDate date = LocalDate.of(year, month.getValue(), 1);
        initializeBarChart(mode, date);
    }

    public void handleCloseButton() {
        AllData.statisticStage.close();
    }
}
