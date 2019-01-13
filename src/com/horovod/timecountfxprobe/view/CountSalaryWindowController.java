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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.WindowEvent;

import java.time.LocalDate;
import java.util.*;

public class CountSalaryWindowController {

    private ObservableList<Node> usersCheckBoxes = FXCollections.observableArrayList();


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
    private void initialize() {
        if (this.usersCheckBoxes == null) {
            this.usersCheckBoxes = FXCollections.observableArrayList();
        }

        designersOnlyCheckBox.setSelected(true);

        initUsers();

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

        if (fromDatePicker.getValue() != null && tillDatePicker.getValue() != null) {
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
            textArea.setText(salary.toString());
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

}
