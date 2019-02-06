package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;

import java.util.Comparator;
import java.util.Iterator;

public class DeleteLoggedUsersWindowController {

    private ObservableList<Node> usersCheckBoxes = FXCollections.observableArrayList();


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
    }

    private void initButtons() {
        cancelButton.setCancelButton(true);
        deleteButton.setDefaultButton(true);
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
        usersFlowPane.getChildren().setAll(usersCheckBoxes);

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
    }

    public void handleDeselectAllButton() {
        for (Node node : usersCheckBoxes) {
            CheckBox ch = (CheckBox) node;
            ch.setSelected(false);
        }
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
        initUsersCheckBoxes();
        AllData.tableProjectsManagerController.initLoggedUsersChoiceBox();
        AllData.deleteLoggedUserStage.close();
    }

}
