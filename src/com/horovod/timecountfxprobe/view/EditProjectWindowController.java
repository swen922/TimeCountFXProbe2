package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkDay;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.serialize.UpdateType;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.User;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class EditProjectWindowController {

    private Project myProject;
    private int myProjectID;
    private Stage myStage;

    private ObservableMap<String, WorkDay> workDays;
    private ObservableList<WorkDay> workDaysList;
    private List<TableColumn<WorkDay, String>> listColumns = FXCollections.observableArrayList();
    private List<Integer> des = new ArrayList<>();

    private boolean isChanged = false;
    private Map<Node, String> textAreas = new HashMap<>();

    private final String exportToTXT = "в Текст";
    private final String exportToCSV = "в CSV";


    @FXML
    private AnchorPane topColoredPane;

    @FXML
    private Label idNumberLabel;

    @FXML
    private Label dateCreationLabel;

    @FXML
    private Button openFolderButton;

    @FXML
    private CheckBox archiveCheckBox;



    @FXML
    private TextArea companyNameTextArea;

    @FXML
    private TextArea managerTextArea;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private TextField linkedProjectsTextField;

    @FXML
    private TextField budgetTextField;

    @FXML
    private TextField POnumberTextField;

    @FXML
    private TextField pathToFolderTextField;



    @FXML
    private Label workSum;

    @FXML
    private Label hoursSum;

    @FXML
    private Label designersWorkSums;

    @FXML
    private Button addWorkDayButton;

    @FXML
    private Button exportButton;

    @FXML
    private ChoiceBox<String> selectFormatChoiceBox;

    @FXML
    private Button closeButton;

    @FXML
    private Button revertButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button saveAndCloseButton;

    @FXML
    private TableView<WorkDay> workTimeTableView;

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    @FXML
    private void initialize() {

        myProjectID = AllData.IDnumberForEdit;

        if (myProject == null) {
            myProject = AllData.getAnyProject(myProjectID);
        }

        initProjectFields();

        //стартовая инициализация чекбокса Архивный согласно состоянию проекта
        initializeArchiveCheckBox();

        initSaveButtons();

        initSelectFormatChoiceBox();

        initializeTable();

    }

    public void updateProject() {
        myProject = AllData.getAnyProject(myProjectID);

        initProjectFields();

        //стартовая инициализация чекбокса Архивный согласно состоянию проекта
        initializeArchiveCheckBox();

        initSaveButtons();

        initSelectFormatChoiceBox();

        initializeTable();

    }


    private void initProjectFields() {

        // id-номер проекта
        idNumberLabel.setText(String.valueOf(myProjectID));

        dateCreationLabel.setText(myProject.getDateCreationString());

        descriptionTextArea.setText(myProject.getDescription());
        textAreas.put(descriptionTextArea, descriptionTextArea.getText());

        // инициализация кнопки открытия папки – прописано в FXML

        companyNameTextArea.setText(myProject.getCompany());
        textAreas.put(companyNameTextArea, companyNameTextArea.getText());

        managerTextArea.setText(myProject.getManager());
        textAreas.put(managerTextArea, managerTextArea.getText());

        commentTextArea.setText(myProject.getComment());
        textAreas.put(commentTextArea, commentTextArea.getText());

        linkedProjectsTextField.setText(myProject.getLinkedProjects());
        textAreas.put(linkedProjectsTextField, linkedProjectsTextField.getText());

        if (myProject.getBudget() == 0) {
            budgetTextField.setText("");
        }
        else {
            budgetTextField.setText(AllData.formatInputInteger(myProject.getBudget()));
        }
        textAreas.put(budgetTextField, budgetTextField.getText());

        POnumberTextField.setText(myProject.getPONumber());
        textAreas.put(POnumberTextField, POnumberTextField.getText());

        pathToFolderTextField.setText(myProject.getFolderPath());
        textAreas.put(pathToFolderTextField, pathToFolderTextField.getText());

        workSum.textProperty().bind(myProject.workSumProperty());
        // Эта строчка перенесена в initializeTable()
        //hoursSum.setText(AllData.formatHours(AllData.formatWorkTime(myProject.getWorkSumDouble())));

    }



    public void initializeTable() {

        hoursSum.setText(AllData.formatHours(AllData.formatWorkTime(myProject.getWorkSumDouble())));

        if (workDays == null) {
            workDays = FXCollections.observableHashMap();
        }
        workDays.clear();

        if (listColumns == null) {
            listColumns = FXCollections.observableArrayList();
        }
        listColumns.clear();

        if (des == null) {
            des = new ArrayList<>();
        }
        des.clear();

        workTimeTableView.getColumns().clear();

        TableColumn<WorkDay, String> datesTableColumn = new TableColumn<>("Дата");
        datesTableColumn.setStyle("-fx-alignment: CENTER;");
        listColumns.add(datesTableColumn);

        for (WorkTime wt : myProject.getWork()) {
            String dateString = wt.getDateString();

            if (!des.contains(wt.getDesignerID())) {
                des.add(wt.getDesignerID());
            }

            if (!workDays.containsKey(dateString)) {
                WorkDay workDay = new WorkDay(dateString);
                workDay.addWorkTime(wt.getDesignerID(), wt.getTimeDouble());
                workDays.put(dateString, workDay);
            }
            else {
                WorkDay existingDay = workDays.get(wt.getDateString());
                existingDay.addWorkTime(wt.getDesignerID(), wt.getTimeDouble());
            }
        }

        des.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                // Первый вариант сортировки – по номеру ID юзера
                //return Integer.compare(o1, o2);

                // Новый вариант сортировки – по полному имени,
                // надо тогда первой фамилию писать всегда в полном имени
                Designer des1 = (Designer) AllUsers.getOneUser(o1);
                Designer des2 = (Designer) AllUsers.getOneUser(o2);
                String fullName1 = des1.getFullName();
                String fullName2 = des2.getFullName();
                return fullName1.compareTo(fullName2);
            }
        });

        Callback<TableColumn<WorkDay, String>, TableCell<WorkDay, String>> cellFactory =
                (TableColumn<WorkDay, String> p) -> new EditingCell(FillChartMode.TIME);


        for (Integer i : des) {
            TableColumn<WorkDay, String> column = new TableColumn<>();
            column.setEditable(true);
            column.setText(AllUsers.getOneUser(i).getFullName());
            column.setStyle("-fx-alignment: CENTER;");

            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WorkDay, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<WorkDay, String> param) {
                    return new SimpleStringProperty(AllData.formatWorkTime(param.getValue().getWorkTimeForDesigner(i)));
                }
            });

            column.setCellFactory(cellFactory);

            column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<WorkDay, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<WorkDay, String> event) {

                    /**м TODO Не забыть, что у дизайнеров должно обновляться после внесения правок.
                     * TODO Видимо, тут нужен запуск thread с обновлением на сервер */
                    String dateString = event.getRowValue().getDateString();
                    double current = myProject.getWorkSumForDesignerAndDate(i, AllData.parseDate(dateString));
                    double newTimeDouble = AllData.getDoubleFromText(current, event.getNewValue(), 1);

                    AllData.addWorkTime(myProjectID, AllData.parseDate(dateString), i, newTimeDouble);
                    AllData.deleteZeroTime(myProjectID);
                    AllData.tableProjectsManagerController.initializeTable();
                    if (AllData.staffWindowController != null) {
                        AllData.staffWindowController.initializeTable();
                    }
                    if (AllData.statisticManagerWindowController != null) {
                        AllData.statisticManagerWindowController.handleButtonReloadBarChart();
                    }
                    initializeTable();
                }
            });

            listColumns.add(column);
        }

        workDaysList = FXCollections.observableArrayList(workDays.values());

        SortedList<WorkDay> sortedList = new SortedList<>(workDaysList, new Comparator<WorkDay>() {
            @Override
            public int compare(WorkDay o1, WorkDay o2) {
                return o2.getDateString().compareTo(o1.getDateString());
            }
        });

        datesTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WorkDay, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WorkDay, String> param) {
                String date = param.getValue().getDateString();
                return new SimpleStringProperty(date);
            }
        });

        workTimeTableView.getColumns().setAll(listColumns);

        workTimeTableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(workTimeTableView.comparatorProperty());
        workDaysList.sort(new Comparator<WorkDay>() {
            @Override
            public int compare(WorkDay o1, WorkDay o2) {
                return o2.getDateString().compareTo(o1.getDateString());
            }
        });

        initDesignersWorkSums();

        datesTableColumn.setMaxWidth(200);
        for (TableColumn<WorkDay, String> tc : listColumns) {
            tc.setMinWidth(100);
            tc.setMaxWidth(300);
        }
    }

    public void initDesignersWorkSums() {
        if (!workDays.isEmpty()) {
            StringBuilder sb = new StringBuilder("Итого:   ");

            Map<Integer, Integer> counterMap = new HashMap<>();

            for (int i : des) {
                for (WorkDay wd : workDaysList) {
                    if (!counterMap.containsKey(i)) {
                        counterMap.put(i, AllData.doubleToInt(wd.getWorkTimeForDesigner(i)));
                    }
                    else {
                        int old = counterMap.get(i);
                        counterMap.put(i, (AllData.doubleToInt(wd.getWorkTimeForDesigner(i)) + old));
                    }
                }
                double result = AllData.intToDouble(counterMap.get(i));
                sb.append(AllUsers.getOneUser(i).getFullName()).append(" = ").append(AllData.formatWorkTime(result)).append(" ").
                        append(AllData.formatHours(String.valueOf(result))).append(";   ");
            }
            designersWorkSums.setText(sb.toString());
        }
    }


    public void initOpenFolderButton() {
        String startPath = "/Volumes/design/";
        String projectName = myProject.getDescription().split(" - ")[0].trim() + " id-" + myProjectID;
        String path = startPath + myProject.getCompany() + "/" + projectName;

        if (myProject.getFolderPath() != null) {

            try {
                browsDir(myProject.getFolderPath());
            } catch (Exception e) {
                try {
                    browsDir(path);
                } catch (Exception e1) {
                    showAlertOpenFolder(myProjectID);
                }
            }
        }
        else {
            try {
                browsDir(path);
            } catch (Exception e) {
                showAlertOpenFolder(myProjectID);
            }
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



    public void initializeArchiveCheckBox() {
        if (myProject.isArchive()) {
            topColoredPane.setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
            openFolderButton.setDisable(true);
            archiveCheckBox.setSelected(true);
            companyNameTextArea.setEditable(false);
            managerTextArea.setEditable(false);
            descriptionTextArea.setEditable(false);
            commentTextArea.setEditable(false);
            linkedProjectsTextField.setEditable(false);
            budgetTextField.setEditable(false);
            POnumberTextField.setEditable(false);
            pathToFolderTextField.setEditable(false);
            addWorkDayButton.setDisable(true);
        }
        else {
            topColoredPane.setStyle(null);
            openFolderButton.setDisable(false);
            archiveCheckBox.setSelected(false);
            companyNameTextArea.setEditable(true);
            managerTextArea.setEditable(true);
            descriptionTextArea.setEditable(true);
            commentTextArea.setEditable(true);
            linkedProjectsTextField.setEditable(true);
            budgetTextField.setEditable(true);
            POnumberTextField.setEditable(true);
            pathToFolderTextField.setEditable(true);
            addWorkDayButton.setDisable(false);
        }
    }

    public void handleArchiveCheckBox() {
        if (archiveCheckBox.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение перевода в архив");
            alert.setHeaderText("Перевести проект id-" + myProjectID + " в архив?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK) {
                AllData.changeProjectArchiveStatus(myProjectID, true);
            }
            else {
                AllData.changeProjectArchiveStatus(myProjectID, false);
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение вывода из архива");
            alert.setHeaderText("Вывести проект id-" + myProjectID + " из архива?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK) {
                AllData.changeProjectArchiveStatus(myProjectID, false);
            }
            else if (option.get() == ButtonType.CANCEL) {
                AllData.changeProjectArchiveStatus(myProjectID, true);
            }
        }
        initializeArchiveCheckBox();
        AllData.tableProjectsManagerController.initializeTable();
    }

    public void handleAddWorkDayButton() {
        AllData.mainApp.showAddWorkDayDialog(myProjectID, myStage, this);
    }

    private void initSelectFormatChoiceBox() {
        if (selectFormatChoiceBox.getItems().isEmpty()) {
            selectFormatChoiceBox.getItems().add(exportToCSV);
            selectFormatChoiceBox.getItems().add(exportToTXT);
            selectFormatChoiceBox.setValue(exportToTXT);

        }
    }

    public void handleExport() {

        if (selectFormatChoiceBox.getValue().equalsIgnoreCase(exportToCSV)) {
            writeCSV();
        }
        else if (selectFormatChoiceBox.getValue().equalsIgnoreCase(exportToTXT)) {
            writeText();
        }
    }

    public void writeCSV() {

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV file", "*.csv");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(AllData.pathToDownloads));
        String fileName = "Проект id-" + myProjectID + " на " + AllData.formatDate(LocalDate.now()).replaceAll("\\.", "_");
        chooser.setInitialFileName(fileName);

        File file = chooser.showSaveDialog(myStage);

        if (file != null) {
            if (!file.getPath().endsWith(".csv")) {
                file = new File(file.getPath() + ".csv");
            }
        }

        if (file != null) {

            try (Writer writer = new BufferedWriter(new FileWriter(file))) {



                StringBuilder sb = new StringBuilder("Проект id-").append(myProjectID).append("\n\n");

                String[] descr = myProject.getDescription().split(" - ");

                sb.append("Описание: ");

                for (String s : descr) {
                    sb.append(s).append("\n");
                }

                //sb.append("Описание: ").append(myProject.getDescription()).append("\n");

                sb.append("\nСоздан: ").append(myProject.getDateCreationString()).append("\n");
                sb.append("Статус: ").append(myProject.isArchive() ? "Архивный\n" : "Активный\n");
                sb.append("Компания: ").append(myProject.getCompany()).append("\n");
                sb.append("Менеджер: ").append(myProject.getManager()).append("\n");
                sb.append("Комментарий: ").append(myProject.getComment() == null ? "нет" : myProject.getComment().isEmpty() ? "нет" : myProject.getComment()).append("\n");
                sb.append("Связанные проекты: ").append(myProject.getLinkedProjects() == null ? "нет" : myProject.getLinkedProjects().isEmpty() ? "нет" : myProject.getLinkedProjects()).append("\n");
                String sum = myProject.getBudget() == 0 ? "нет" : (myProject.getBudget() + " руб.");
                sb.append("Сумма по смете: ").append(sum).append("\n");
                sb.append("Номер РО: ").append(myProject.getPONumber() == null ? "нет" : myProject.getPONumber().isEmpty() ? "нет" : myProject.getPONumber()).append("\n\n\n");

                writer.write(sb.toString());

                writer.write("Дата" + "\t");

                // Заголовок таблицы
                for (int i = 0; i < des.size(); i++) {
                    String desName = null;
                    if (AllUsers.getOneUser(des.get(i)).getFullName() != null) {
                        desName = AllUsers.getOneUser(des.get(i)).getFullName();
                    }
                    else {
                        desName = AllUsers.getOneUser(des.get(i)).getNameLogin();
                    }

                    if (i == (des.size() - 1)) {
                        writer.write(desName + "\n");
                    }
                    else {
                        writer.write(desName + "\t");
                    }
                }

                // Цифры в таблице
                for (WorkDay wd : workDaysList) {
                    writer.write(wd.getDateString() + "\t");

                    for (int j = 0; j < des.size(); j++) {
                        if (j == (des.size() - 1)) {
                            writer.write(AllData.formatWorkTime(wd.getWorkTimeForDesigner(des.get(j))) + "\n");
                        }
                        else {
                            writer.write(AllData.formatWorkTime(wd.getWorkTimeForDesigner(des.get(j))) + "\t");
                        }
                    }
                }

                writer.write("\n");

                // Итог по каждому дизайнеру
                if (!workDays.isEmpty()) {

                    Map<Integer, Integer> counterMap = new HashMap<>();

                    for (int k = 0; k < des.size(); k++) {

                        if (k == 0) {
                            writer.write("Итого по дизайнерам:\t");
                        }

                        for (WorkDay wd : workDaysList) {
                            if (!counterMap.containsKey(des.get(k))) {
                                counterMap.put(des.get(k), AllData.doubleToInt(wd.getWorkTimeForDesigner(des.get(k))));
                            }
                            else {
                                int old = counterMap.get(des.get(k));
                                counterMap.put(des.get(k), (AllData.doubleToInt(wd.getWorkTimeForDesigner(des.get(k))) + old));
                            }
                        }

                        double result = AllData.intToDouble(counterMap.get(des.get(k)));

                        if (k == (des.size() - 1)) {
                            writer.write(AllData.formatWorkTime(result) + "\n");
                        }
                        else {
                            writer.write(AllData.formatWorkTime(result) + "\t");
                        }
                    }
                    writer.write("\n");
                }

                // Итог обший
                writer.write("\n");
                writer.write("Итого в проекте: " + "\t" + AllData.formatWorkTime(myProject.getWorkSumDouble()) + "\n");
                writer.flush();
            }
            catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка записи");
                alert.setHeaderText("Ошибка при сохранении файла");
                alert.showAndWait();
                AllData.logger.error(ex.getMessage(), ex);
            }
        }
    }


    public void writeText() {

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setInitialDirectory(new File(AllData.pathToDownloads));
        String fileName = "Проект id-" + myProjectID + " на " + AllData.formatDate(LocalDate.now()).replaceAll("\\.", "_");
        chooser.setInitialFileName(fileName);

        File file = chooser.showSaveDialog(myStage);

        if (file != null) {
            if (!file.getPath().endsWith(".txt")) {
                file = new File(file.getPath() + ".txt");
            }
        }

        if (file != null) {

            try (Writer writer = new BufferedWriter(new FileWriter(file))) {

                StringBuilder sb = new StringBuilder("Проект id-").append(myProjectID).append("\n\n");

                String[] descr = myProject.getDescription().split(" - ");

                sb.append("Описание: ");

                for (String s : descr) {
                    sb.append(s).append("\n");
                }

                sb.append("\nСоздан: ").append(myProject.getDateCreationString()).append("\n");
                sb.append("Статус: ").append(myProject.isArchive() ? "Архивный\n" : "Активный\n");
                sb.append("Компания: ").append(myProject.getCompany()).append("\n");
                sb.append("Менеджер: ").append(myProject.getManager()).append("\n");
                sb.append("Комментарий: ").append(myProject.getComment() == null ? "нет" : myProject.getComment().isEmpty() ? "нет" : myProject.getComment()).append("\n");
                sb.append("Связанные проекты: ").append(myProject.getLinkedProjects() == null ? "нет" : myProject.getLinkedProjects().isEmpty() ? "нет" : myProject.getLinkedProjects()).append("\n");
                String sum = myProject.getBudget() == 0 ? "нет" : (myProject.getBudget() + " руб.");
                sb.append("Сумма по смете: ").append(sum).append("\n");
                sb.append("Номер РО: ").append(myProject.getPONumber() == null ? "нет" : myProject.getPONumber().isEmpty() ? "нет" : myProject.getPONumber()).append("\n\n\n");

                sb.append("Рабочее время по проекту id-").append(myProjectID).append(" на ").append(AllData.formatDate(LocalDate.now())).append("\n\n");

                for (WorkDay wd : workDaysList) {
                    sb.append(wd.getDateString()).append("\n");
                    int workdayCounter = 0;

                    for (int i = 0; i < des.size(); i++) {
                        if (wd.containsWorkTimeForDesigner(des.get(i))) {

                            Designer designer = (Designer) AllUsers.getOneUser(des.get(i));
                            String desName = designer.getNameLogin();
                            if (designer.getFullName() != null && !designer.getFullName().isEmpty()) {
                                desName = designer.getFullName();
                            }
                            sb.append(desName).append(" = ").append(AllData.formatWorkTime(wd.getWorkTimeForDesigner(des.get(i)))).append(" ");
                            sb.append(AllData.formatHours(String.valueOf(wd.getWorkTimeForDesigner(des.get(i))))).append("\n");
                            workdayCounter += AllData.doubleToInt(wd.getWorkTimeForDesigner(des.get(i)));
                        }

                    }

                    if (wd.getWorkTimeMap().size() > 1) {
                        sb.append("Итого за ").append(wd.getDateString()).append(" = ");
                        sb.append(AllData.formatWorkTime(AllData.intToDouble(workdayCounter))).append(" ");
                        sb.append(AllData.formatHours(String.valueOf(AllData.intToDouble(workdayCounter)))).append("\n");
                    }
                    sb.append("\n");

                }

                sb.append(designersWorkSums.getText().replaceAll("Итого:   ", "Итого:\n").replaceAll(";   ", "\n"));
                sb.append("\n");
                sb.append("Итого в проекте: ").append(AllData.formatWorkTime(myProject.getWorkSumDouble())).append(" ");
                sb.append(AllData.formatHours(String.valueOf(myProject.getWorkSumDouble()))).append("\n");

                writer.write(sb.toString());
                writer.flush();
            }
            catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка записи");
                alert.setHeaderText("Ошибка при сохранении файла");
                alert.showAndWait();
                AllData.logger.error(ex.getMessage(), ex);

            }
        }

    }

    public void listenChanges() {

        if (myStage != null) {
            myStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    handleCloseButton();
                    event.consume();
                }
            });
        }

        isChanged = false;

        budgetTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode keyCode = event.getCode();
                if (keyCode == KeyCode.ENTER) {
                    String input = AllData.formatStringInputInteger(textAreas.get(budgetTextField), budgetTextField.getText());
                    budgetTextField.setText(input);
                }
            }
        });

        //listenBudgetTextField();

        Map<Node, String> changedAreas = new HashMap<>();
        changedAreas.put(companyNameTextArea, companyNameTextArea.getText());
        changedAreas.put(managerTextArea, managerTextArea.getText());
        changedAreas.put(descriptionTextArea, descriptionTextArea.getText());
        changedAreas.put(commentTextArea, commentTextArea.getText());
        changedAreas.put(linkedProjectsTextField, linkedProjectsTextField.getText());
        changedAreas.put(budgetTextField, budgetTextField.getText());
        changedAreas.put(POnumberTextField, POnumberTextField.getText());
        changedAreas.put(pathToFolderTextField, pathToFolderTextField.getText());

        Iterator<Map.Entry<Node, String>> iter = textAreas.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Node, String> entry = iter.next();
            Node node = entry.getKey();
            String text = entry.getValue();
            String newText = changedAreas.get(node);

            if (text == null && newText == null) {
                continue;
            }
            else if (text == null && newText != null) {
                if (newText.isEmpty()) {
                    isChanged = false;
                }
                else {
                    isChanged = true;
                    break;
                }
            }
            else if (text != null && newText == null) {
                if (text.isEmpty()) {
                    isChanged = false;
                }
                else {
                    isChanged = true;
                    break;
                }
            }
            else if (!text.equals(newText)) {
                isChanged = true;
                break;
            }
        }

        initSaveButtons();
    }


    public void initSaveButtons() {
        if (!isChanged) {
            revertButton.setDisable(true);
            saveButton.setDisable(true);
            saveAndCloseButton.setDisable(true);
        }
        else {
            revertButton.setDisable(false);
            saveButton.setDisable(false);
            saveAndCloseButton.setDisable(false);
        }
    }

    public void handleRevertButton() {

        //projectNameTextArea.setText(textAreas.get(projectNameTextArea));
        companyNameTextArea.setText(textAreas.get(companyNameTextArea));
        managerTextArea.setText(textAreas.get(managerTextArea));
        descriptionTextArea.setText(textAreas.get(descriptionTextArea));

        if (textAreas.get(commentTextArea) == null) {
            commentTextArea.setText("");
        }
        else {
            commentTextArea.setText(textAreas.get(commentTextArea));
        }

        if (textAreas.get(linkedProjectsTextField) == null) {
            linkedProjectsTextField.setText("");
        }
        else {
            linkedProjectsTextField.setText(textAreas.get(linkedProjectsTextField));
        }

        if (textAreas.get(budgetTextField) == null) {
            budgetTextField.setText("");
        }
        else {
            budgetTextField.setText(textAreas.get(budgetTextField));
        }

        if (textAreas.get(POnumberTextField) == null) {
            POnumberTextField.setText("");
        }
        else {
            POnumberTextField.setText(textAreas.get(POnumberTextField));
        }

        if (textAreas.get(pathToFolderTextField) == null) {
            pathToFolderTextField.setText("");
        }
        else {
            pathToFolderTextField.setText(textAreas.get(pathToFolderTextField));
        }
        isChanged = false;
        initSaveButtons();
    }


    public void handleSaveButton() {
        textAreas.put(descriptionTextArea, descriptionTextArea.getText());
        myProject.setDescription(descriptionTextArea.getText());
        textAreas.put(companyNameTextArea, companyNameTextArea.getText());
        myProject.setCompany(companyNameTextArea.getText());
        textAreas.put(managerTextArea, managerTextArea.getText());
        myProject.setManager(managerTextArea.getText());
        textAreas.put(commentTextArea, commentTextArea.getText());
        myProject.setComment(commentTextArea.getText());
        textAreas.put(linkedProjectsTextField, linkedProjectsTextField.getText());
        myProject.setLinkedProjects(linkedProjectsTextField.getText());

        if (budgetTextField.getText() != null && !budgetTextField.getText().isEmpty()) {
            myProject.setBudget(AllData.parseMoney(myProject.getBudget(), budgetTextField.getText()));
        }
        budgetTextField.setText(AllData.formatInputInteger(myProject.getBudget()));
        textAreas.put(budgetTextField, budgetTextField.getText());


        textAreas.put(POnumberTextField, POnumberTextField.getText());
        myProject.setPONumber(POnumberTextField.getText());
        textAreas.put(pathToFolderTextField, pathToFolderTextField.getText());
        myProject.setFolderPath(pathToFolderTextField.getText());

        isChanged = false;
        initSaveButtons();
        AllData.tableProjectsManagerController.initializeTable();

        AllData.status = "Локально изменены свойства проекта id-" + myProjectID;
        AllData.updateAllStatus();
        AllData.logger.info(AllData.status);

        Updater.update(UpdateType.UPDATE_PROJECT, myProject);

    }

    public void handleSaveAndCloseButton() {
        handleSaveButton();
        handleCloseButton();
    }

    public void handleCloseButton() {

        if (!isChanged) {
            if (AllData.openEditProjectStages.containsKey(myProjectID)) {
                AllData.openEditProjectStages.remove(myProjectID);
            }
            if (AllData.editProjectWindowControllers.containsKey(myProjectID)) {
                AllData.editProjectWindowControllers.remove(myProjectID);
            }
            myStage.close();
        }
        else {
            handleAlerts();
        }
    }

    private void handleAlerts() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение закрытия");
        alert.setHeaderText("В текст полей проекта id-" + myProjectID + " были внесены изменения.\nСохранить их или проигнорировать?");

        ButtonType returnButton = new ButtonType("Вернуться обратно");
        ButtonType dontSaveButton = new ButtonType("Не сохранять");
        ButtonType saveButton = new ButtonType("Сохранить");

        // Remove default ButtonTypes
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(returnButton, dontSaveButton, saveButton);

        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == returnButton) {
            alert.close();
        }
        else if (option.get() == dontSaveButton) {
            handleRevertButton();
            if (AllData.openEditProjectStages.containsKey(myProjectID)) {
                AllData.openEditProjectStages.remove(myProjectID);
            }
            if (AllData.editProjectWindowControllers.containsKey(myProjectID)) {
                AllData.editProjectWindowControllers.remove(myProjectID);

            }
            alert.close();
            myStage.close();
        }
        else {
            alert.close();
            handleSaveAndCloseButton();
        }
    }

}
