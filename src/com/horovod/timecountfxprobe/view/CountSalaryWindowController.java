package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class CountSalaryWindowController {

    private ObservableList<Node> usersCheckBoxes = FXCollections.observableArrayList();
    private Map<String, Integer> countedSalaries = new TreeMap<>();

    private final String exportToTXT = "в Текст";
    private final String exportToCSV = "в CSV";


    @FXML
    private CheckBox designersOnlyCheckBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private Button clearFromDatePickerButton;

    @FXML
    private DatePicker tillDatePicker;

    @FXML
    private Button clearTillDatePickerButton;

    @FXML
    private Button selectAllButton;

    @FXML
    private Button deselectAllButton;

    @FXML
    private FlowPane usersFlowPane;

    @FXML
    private Button countSalaryButton;

    @FXML
    private TextArea textArea;

    @FXML
    private Button exportButton;

    @FXML
    private ChoiceBox<String> exportChoiceBox;

    @FXML
    private Button closeButton;



    @FXML
    private void initialize() {
        if (this.usersCheckBoxes == null) {
            this.usersCheckBoxes = FXCollections.observableArrayList();
        }

        designersOnlyCheckBox.setSelected(true);

        initUsers();
        initExportChoiceBox();

    }

    private void initUsers() {
        usersCheckBoxes.clear();
        if (designersOnlyCheckBox.isSelected()) {
            for (User u : AllUsers.getActiveDesigners().values()) {
                CheckBox ch = new CheckBox(u.getFullName());
                ch.setSelected(true);
                ch.setCursor(Cursor.HAND);
                usersCheckBoxes.add(ch);
            }
        }
        else {
            for (User u : AllUsers.getActiveUsers().values()) {
                CheckBox ch = new CheckBox(u.getFullName());
                ch.setSelected(true);
                ch.setCursor(Cursor.HAND);
                usersCheckBoxes.add(ch);
            }
        }
        usersFlowPane.getChildren().setAll(usersCheckBoxes);
        sortUsers();
    }

    private void sortUsers() {
        usersCheckBoxes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                CheckBox ch1 = (CheckBox) o1;
                CheckBox ch2 = (CheckBox) o2;
                return ch1.getText().compareTo(ch2.getText());
            }
        });
    }

    /** Немножко громоздкая конструкция, чтобы не просто обновлять набор чекбоксов-юзеров внутри flowPane
     * согласно опции, выславленной в чекбоксе designersOnlyCheckBox после щелчка юзера,
     * но и чтобы при этом сохранять положение "включен" или "выключен", которое было ранее сделано для каждого чекбокса */
    public void updateUsers() {

        if (designersOnlyCheckBox.isSelected()) {

            for (User u : AllUsers.getActiveDesigners().values()) {

                boolean cont = false;
                for (Node n : usersCheckBoxes) {
                    CheckBox checkBox = (CheckBox) n;
                    if (checkBox.getText().equals(u.getFullName())) {
                        cont = true;
                        break;
                    }
                }

                if (!cont) {
                    CheckBox ch = new CheckBox(u.getFullName());
                    ch.setSelected(true);
                    ch.setCursor(Cursor.HAND);
                    usersCheckBoxes.add(ch);
                }

                Iterator<Node> iter = usersCheckBoxes.iterator();
                while (iter.hasNext()) {
                    Node entry = iter.next();
                    CheckBox chbx = (CheckBox) entry;
                    if (!AllUsers.getActiveDesigners().containsKey(AllUsers.getOneUserForFullName(chbx.getText()).getIDNumber())) {
                        iter.remove();
                    }
                }
            }
        }
        else {
            for (User u : AllUsers.getActiveUsers().values()) {

                boolean cont = false;
                for (Node n : usersCheckBoxes) {
                    CheckBox checkBox = (CheckBox) n;
                    if (checkBox.getText().equals(u.getFullName())) {
                        cont = true;
                        break;
                    }
                }

                if (!cont) {
                    CheckBox ch = new CheckBox(u.getFullName());
                    ch.setSelected(true);
                    ch.setCursor(Cursor.HAND);
                    usersCheckBoxes.add(ch);
                }

                Iterator<Node> iter = usersCheckBoxes.iterator();
                while (iter.hasNext()) {
                    Node entry = iter.next();
                    CheckBox chbx = (CheckBox) entry;
                    if (!AllUsers.getActiveUsers().containsKey(AllUsers.getOneUserForFullName(chbx.getText()).getIDNumber())) {
                        iter.remove();
                    }
                }
            }
        }
        usersFlowPane.getChildren().setAll(usersCheckBoxes);
        sortUsers();
    }



    public void handleSelectAllButton() {
        for (Node node : usersCheckBoxes) {
            CheckBox ch = (CheckBox) node;
            ch.setSelected(true);
        }
    }

    public void handleDeselectAllButton() {
        for (Node node : usersCheckBoxes) {
            CheckBox ch = (CheckBox) node;
            ch.setSelected(false);
        }
    }

    public void handleFromDatePicker() {
        checkDatePicker(fromDatePicker);
    }

    public void handleTillDatePicker() {
        checkDatePicker(tillDatePicker);
        onClosing();
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

    public void handleClearFromDatePicker() {
        fromDatePicker.setValue(null);
    }

    public void handleClearTillDatePciker() {
        tillDatePicker.setValue(null);
    }

    public void countSalary() {
        handleFromDatePicker();
        handleTillDatePicker();

        if (this.countedSalaries == null) {
            this.countedSalaries = new TreeMap<>();
        }

        if (fromDatePicker.getValue() != null && tillDatePicker.getValue() != null) {
            this.countedSalaries.clear();
            StringBuilder salary = new StringBuilder("Расчет зарплаты за период с ");
            salary.append(AllData.formatDate(fromDatePicker.getValue())).append(" по ").append(AllData.formatDate(tillDatePicker.getValue())).append("\n\n");
            List<CheckBox> noHourPay = new ArrayList<>();
            int totalSum = 0;
            for (Node node : usersCheckBoxes) {
                CheckBox checkBox = (CheckBox) node;
                if (checkBox.isSelected()) {
                    int workTime = 0;
                    int hourPay = AllUsers.getOneUserForFullName(checkBox.getText()).getWorkHourValue();
                    int salaryPay = 0;
                    if (hourPay > 0) {
                        int userID = AllUsers.getOneUserForFullName(checkBox.getText()).getIDNumber();
                        workTime += AllData.getWorkSumForDesignerAndPeriod(userID, fromDatePicker.getValue(), tillDatePicker.getValue());
                        salary.append(checkBox.getText()).append(" = ");
                        salary.append(AllData.formatWorkTime(AllData.intToDouble(workTime))).append(" ");
                        salary.append(AllData.formatHours(String.valueOf(AllData.intToDouble(workTime))));
                        salary.append(" * ").append(hourPay).append(" руб./час").append(" = ");
                        salaryPay = (int) (AllData.formatDouble(hourPay * AllData.intToDouble(workTime), 0));
                        salary.append(AllData.formatInputInteger(salaryPay));
                        salary.append(" руб.\n");
                        this.countedSalaries.put(checkBox.getText(), salaryPay);
                    }
                    else {
                        noHourPay.add(checkBox);
                    }
                    totalSum += salaryPay;

                }
            }
            salary.append("\n").append("Итого вынуть из кассы = ");
            salary.append(AllData.formatInputInteger(totalSum)).append(" руб.\n\n");

            if (!noHourPay.isEmpty()) {
                salary.append("В данном расчете не учтены следующие работники:\n");
                for (CheckBox noCh : noHourPay) {
                    salary.append(noCh.getText()).append("\n");
                }
                salary.append("по причине отсутствия указанной стоимости рабочего часа.\n\n");
            }
            if (noHourPay.size() == usersCheckBoxes.size()) {
                textArea.setText("Расчет невозможен. Укажите размер часовой зарплаты хотя бы у одного работника.");
            }
            else if (totalSum != 0) {
                textArea.setText(salary.toString());
            }
            else {
                textArea.setText("Выберите хотя бы одного работника, у которого указан размер часовой оплаты.");
            }

        }
    }

    public void handleClearSalaryButton() {
        textArea.setText("");
    }

    private void onClosing() {
        AllData.countSalaryWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                AllData.countSalaryWindow.hide();
                event.consume();
            }
        });
    }

    public void handleCloseButton() {
        AllData.countSalaryWindow.hide();
    }

    private void writeText() {

        if (textArea.getText() != null && !textArea.getText().isEmpty()) {

            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT file", "*.txt");
            chooser.getExtensionFilters().add(extFilter);

            String path = new File(System.getProperty("user.home")).getPath() + "/Documents";
            chooser.setInitialDirectory(new File(path));

            String fileName = textArea.getText().split("\n")[0];
            chooser.setInitialFileName(fileName);

            File file = chooser.showSaveDialog(AllData.countSalaryWindow);

            if (file != null) {
                if (!file.getPath().endsWith(".txt")) {
                    file = new File(file.getPath() + ".txt");
                }

                try (Writer writer = new BufferedWriter(new FileWriter(file))) {

                    writer.write(textArea.getText());
                    writer.flush();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void writeCSV() {

        if (textArea.getText() != null && !textArea.getText().isEmpty()) {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV file", "*.csv");
            chooser.getExtensionFilters().add(extFilter);

            String path = new File(System.getProperty("user.home")).getPath() + "/Documents";
            chooser.setInitialDirectory(new File(path));

            String fileName = textArea.getText().split("\n")[0];
            chooser.setInitialFileName(fileName);

            File file = chooser.showSaveDialog(AllData.countSalaryWindow);

            if (file != null) {
                int total = 0;
                if (!file.getPath().endsWith(".csv")) {
                    file = new File(file.getPath() + ".csv");
                }

                StringBuilder sb = new StringBuilder("");

                for (Map.Entry<String, Integer> entry : countedSalaries.entrySet()) {
                    sb.append(entry.getKey()).append("\t");
                }
                sb.append("\n");

                for (Map.Entry<String, Integer> entry : countedSalaries.entrySet()) {
                    sb.append(AllData.formatInputInteger(entry.getValue())).append("\t");
                    total += entry.getValue();
                }
                sb.append("\n\n");
                sb.append("\n").append("Итого вынуть из кассы = \t");
                sb.append(AllData.formatInputInteger(total)).append(" руб.\t\n\n");


                try (Writer writer = new BufferedWriter(new FileWriter(file))) {

                    writer.write(sb.toString());
                    writer.flush();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
