package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.util.Comparator;
import java.util.Iterator;

public class DeleteLoggedUsersWindowController {

    private ObservableList<Node> usersCheckBoxes = FXCollections.observableArrayList();
    private final String emptyReport = "Удалить некого, потому что кроме вас\nнет ни одного залогиненного пользователя.\nА удалять себя нельзя.";


    @FXML
    private Button selectAllButton;

    @FXML
    private Button deselectAllButton;

    @FXML
    private FlowPane usersFlowPane;

    @FXML
    private Button cancelButton;

    @FXML
    private Button deleteButton;

    @FXML
    public void initialize() {
        initUsersCheckBoxes();
        sortUsers();
        initButtons();
        initDeleteButton();
        initSelectCheckboxes();
    }

    private void initButtons() {
        cancelButton.setCancelButton(true);
        deleteButton.setDefaultButton(true);
    }

    private void initDeleteButton() {
        boolean selected = false;
        for (Node n : usersCheckBoxes) {
            CheckBox ch = (CheckBox) n;
            if (ch.isSelected()) {
                selected = true;
                break;
            }
        }
        if (selected) {
            deleteButton.setDisable(false);
        }
        else {
            deleteButton.setDisable(true);
        }
    }



    private void initUsersCheckBoxes() {
        if (usersCheckBoxes == null) {
            usersCheckBoxes = FXCollections.observableArrayList();
        }
        usersCheckBoxes.clear();

        for (String fullName : AllUsers.getUsersLogged()) {

            if (!fullName.equalsIgnoreCase(AllData.toLoginWindow) &&
                    !fullName.equalsIgnoreCase(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName())) {
                CheckBox checkBox = new CheckBox(fullName);
                checkBox.setCursor(Cursor.HAND);
                usersCheckBoxes.add(checkBox);
            }
        }
        if (!usersCheckBoxes.isEmpty()) {
            usersFlowPane.getChildren().setAll(usersCheckBoxes);
        }
        else {
            usersFlowPane.getChildren().clear();
            Label emptyList = new Label(emptyReport);
            emptyList.setWrapText(true);
            usersFlowPane.getChildren().add(emptyList);
        }
    }

    private void initSelectCheckboxes() {
        for (Node n : usersCheckBoxes) {
            CheckBox ch = (CheckBox) n;
            ch.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    initDeleteButton();
                }
            });
        }
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

    public void handleSelectAllButton() {
        for (Node node : usersCheckBoxes) {
            CheckBox ch = (CheckBox) node;
            ch.setSelected(true);
        }
        deleteButton.setDisable(false);
    }

    public void handleDeselectAllButton() {
        for (Node node : usersCheckBoxes) {
            CheckBox ch = (CheckBox) node;
            ch.setSelected(false);
        }
        deleteButton.setDisable(true);
    }

    public void handleCancelButton() {
        AllData.deleteLoggedUserStage.close();
    }

    public void handleDeleteButton() {

        Iterator<Node> iter = usersCheckBoxes.iterator();
        while (iter.hasNext()) {
            CheckBox ch = (CheckBox) iter.next();
            if (ch.isSelected()) {
                AllUsers.getUsersLogged().remove(ch.getText());
                iter.remove();
            }
        }
        //initUsersCheckBoxes();
        if (AllData.tableProjectsManagerController != null) {
            AllData.tableProjectsManagerController.initLoggedUsersChoiceBox();
        }
        if (AllData.tableProjectsDesignerController != null) {
            AllData.tableProjectsDesignerController.initLoggedUsersChoiceBox();
        }
        if (AllData.adminWindowController != null) {
            AllData.adminWindowController.initLoggedUsersChoiceBox();
        }
        AllData.deleteLoggedUserStage.close();
    }

}
