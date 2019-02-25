package com.horovod.timecountfxprobe;


import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.serialize.Loader;
import com.horovod.timecountfxprobe.serialize.Updater;
import com.horovod.timecountfxprobe.test.Generator;
import com.horovod.timecountfxprobe.threads.ThreadStartCheckingWaitingTasks;
import com.horovod.timecountfxprobe.threads.ThreadUtil;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.view.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    /** TODO Во все методы show... в блок catch вставить показ Alert'а "Не удалось загрузить..." */

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        AllData.primaryStage = primaryStage;
        this.primaryStage.setTitle("Time Count System Probe FX -1");

        AllData.resetStatus();

        /** TODO убрать эту строчку в рабочем варианте */
        Generator.generateUsers2();
        Generator.generateProjects2();

        //Loader loader = new Loader();
        //loader.load();

        Updater.getService().submit(new ThreadStartCheckingWaitingTasks());

        initRootLayut();

        if (AllUsers.getCurrentUser() == 0) {
            showLoginWindowOnStart();
        }
        else {
            Role role = AllUsers.getOneUser(AllUsers.getCurrentUser()).getRole();
            if (role.equals(Role.DESIGNER)) {
                showTableProjectsDesigner();
            }
            else if (role.equals(Role.MANAGER)) {
                showTableProjectsManager();
            }
            else if (role.equals(Role.ADMIN)) {
                showAdminWindow();
            }
        }
        AllData.mainApp = this;


    }

    public void initRootLayut() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setTitle("Time Count FX Probe");

            RootLayoutController controller = loader.getController();
            AllData.rootLayout = rootLayout;
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);
        }
    }



    /** В чистовой версии сделать единообразно с табликой менеджера:
     * поля stage, controller вытащить в AllData статиками */
    public void showTableProjectsDesigner() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TableProjectsDesigner.fxml"));
            AnchorPane tableDesigner = (AnchorPane) loader.load();

            rootLayout.setCenter(tableDesigner);


            AllData.tableProjectsDesignerController = loader.getController();;
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }


    public void showTableProjectsManager() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TableProjectsManager.fxml"));
            AnchorPane tableManager = (AnchorPane) loader.load();

            rootLayout.setCenter(tableManager);

            AllData.tableProjectsManagerController = (TableProjectsManagerController) loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }


    public void showAdminWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/AdminWindow.fxml"));
            AnchorPane adminWindow = (AnchorPane) loader.load();

            rootLayout.setCenter(adminWindow);

            AllData.adminWindowController = (AdminWindowController) loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }


    public void showEditProjectWindow(int projectIDnumber) {
        try {
            AllData.IDnumberForEdit = projectIDnumber;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EditProjectWindow.fxml"));
            AnchorPane editWindow = (AnchorPane) loader.load();

            Stage editProjectStage = new Stage();
            editProjectStage.initModality(Modality.NONE);
            Scene scene = new Scene(editWindow);
            editProjectStage.setScene(scene);

            EditProjectWindowController controller = (EditProjectWindowController) loader.getController();
            controller.setMyStage(editProjectStage);

            AllData.editProjectWindowControllers.put(projectIDnumber, controller);
            AllData.openEditProjectStages.put(projectIDnumber, editProjectStage);

            editProjectStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }

    public void showInfoProjectWindow(int projectIDnumber) {
        try {
            AllData.IDnumberForEdit = projectIDnumber;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/InfoProjectWindow.fxml"));
            AnchorPane infoWindow = (AnchorPane) loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.NONE);
            Scene scene = new Scene(infoWindow);
            stage.setScene(scene);

            InfoProjectWindowController controller = (InfoProjectWindowController) loader.getController();
            controller.setMyStage(stage);

            AllData.infoProjectWindowControllers.put(projectIDnumber, controller);
            AllData.openInfoProjectStages.put(projectIDnumber, stage);
            AllData.infoProjectWindowControllers.get(projectIDnumber).initClosing();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }

    public void showAddWorkDayDialog(int projectIDnumber, Stage editProjectStage, EditProjectWindowController editProjectWindowController) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/AddWorkDayDialog.fxml"));
            AnchorPane addWindow = (AnchorPane) loader.load();

            Stage addWorkStage = new Stage();
            addWorkStage.setTitle("Добавление рабочего дня");
            addWorkStage.initOwner(editProjectStage);
            addWorkStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(addWindow);
            addWorkStage.setScene(scene);
            AddWorkDayDialogController controller = loader.getController();
            controller.setProjectIDnumber(projectIDnumber);
            controller.setMyStage(addWorkStage);
            //controller.setEditProjectWindowController(editProjectWindowController);

            addWorkStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }

    public void showStaffWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/StaffWindow.fxml"));
            AnchorPane staffWin = (AnchorPane) loader.load();

            Stage staffStage = new Stage();
            staffStage.setTitle("Информация о пользователях");
            staffStage.initModality(Modality.NONE);
            //staffStage.initOwner(primaryStage);
            //AllData.staffStage = staffStage;

            Scene scene = new Scene(staffWin);
            staffStage.setScene(scene);
            StaffWindowController controller = loader.getController();

            AllData.staffWindowController = controller;
            AllData.staffWindowStage = staffStage;

            staffStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }

    public void showEditUserWindow(int userID, Stage staff) {
        try {
            AllData.IDnumberForEdit = userID;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EditUserWindow.fxml"));
            AnchorPane editUser = (AnchorPane) loader.load();

            Stage editUserStage = new Stage();
            editUserStage.setTitle("Работник id-" + userID + ", " + AllUsers.getOneUser(userID).getFullName());
            editUserStage.initOwner(staff);
            editUserStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(editUser);
            editUserStage.setScene(scene);

            EditUserWindowController controller = loader.getController();
            controller.setMyStage(editUserStage);

            editUserStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }

    public void showCreateUserWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/CreateUserWindow.fxml"));
            AnchorPane createUser = (AnchorPane) loader.load();

            Stage stage = new Stage();
            stage.setTitle("Создать нового пользователя");
            stage.initOwner(AllData.staffWindowStage);
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(createUser);
            stage.setScene(scene);

            AllData.createUserWindowController = loader.getController();
            AllData.createUserWindow = stage;
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }

    }

    public void showCountSalaryWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/CountSalaryWindow.fxml"));
            AnchorPane salaryPane = (AnchorPane) loader.load();

            Stage salaryStage = new Stage();
            salaryStage.setTitle("Расчет зарплаты");
            salaryStage.initModality(Modality.NONE);
            Scene scene = new Scene(salaryPane);
            salaryStage.setScene(scene);
            AllData.countSalaryWindow = salaryStage;
            AllData.countSalaryWindowController = loader.getController();

            salaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);
        }
    }

    public void showDeleteLoggedUserWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/DeleteLoggedUsersWindow.fxml"));
            AnchorPane deleteUsersPane = (AnchorPane) loader.load();

            AllData.deleteLoggedUserStage = new Stage();
            AllData.deleteLoggedUserStage.setTitle("Удаление залогиненных имен");
            AllData.deleteLoggedUserStage.initModality(Modality.WINDOW_MODAL);
            AllData.deleteLoggedUserStage.initOwner(AllData.primaryStage);
            Scene scene = new Scene(deleteUsersPane);
            AllData.deleteLoggedUserStage.setScene(scene);
            AllData.deleteLoggedUsersWindowController = loader.getController();

            AllData.deleteLoggedUserStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);
        }
    }


    public void showLoginWindow() {
        try {
            FXMLLoader loaderLoginWindow = new FXMLLoader();
            loaderLoginWindow.setLocation(MainApp.class.getResource("view/LoginWindow.fxml"));
            AnchorPane loginWindow = (AnchorPane) loaderLoginWindow.load();

            Stage logWinStage = new Stage();
            logWinStage.setTitle("Вот вам окно логина");
            logWinStage.initModality(Modality.APPLICATION_MODAL);
            logWinStage.initOwner(primaryStage);
            Scene scene = new Scene(loginWindow);
            logWinStage.setScene(scene);
            LoginWindowController loginWindowController = loaderLoginWindow.getController();
            loginWindowController.setStage(logWinStage);

            logWinStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);
        }
    }

    public void showLoginWindowOnStart() {
        try {
            FXMLLoader loaderLoginWindowOnStart = new FXMLLoader();
            loaderLoginWindowOnStart.setLocation(MainApp.class.getResource("view/LoginWindowOnStart.fxml"));
            AnchorPane loginWindowOnStart = (AnchorPane) loaderLoginWindowOnStart.load();

            Stage logWinStage = new Stage();
            logWinStage.setTitle("Войти в программу");
            logWinStage.initModality(Modality.APPLICATION_MODAL);
            logWinStage.initOwner(primaryStage);
            Scene scene = new Scene(loginWindowOnStart);
            logWinStage.setScene(scene);
            LoginWindowOnStartController loginWindowOnStartController = loaderLoginWindowOnStart.getController();
            loginWindowOnStartController.setMainApp(this);

            loginWindowOnStartController.setStage(logWinStage);
            logWinStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }

    public void showStatisticWindow() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/StatisticWindow.fxml"));
            AnchorPane statisticWindow = (AnchorPane) loader.load();
            Stage statStage = new Stage();
            statStage.initModality(Modality.NONE);
            Scene scene = new Scene(statisticWindow);
            statStage.setScene(scene);

            AllData.statisticWindowController = loader.getController();
            AllData.statisticStage = statStage;

            statStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }

    public void showStatisticManagerWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/StatisticManagerWindow.fxml"));
            AnchorPane stManagerWindow = (AnchorPane) loader.load();
            Stage stManagerStage = new Stage();
            stManagerStage.setTitle("Статистика");
            stManagerStage.initModality(Modality.NONE);
            Scene scene = new Scene(stManagerWindow);
            stManagerStage.setScene(scene);
            stManagerStage.show();

            AllData.statisticManagerWindowController = loader.getController();
            AllData.statisticManagerStage = stManagerStage;
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }

    public void showCreateProjectWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/CreateProjectWindow.fxml"));
            AnchorPane createProjectPane = (AnchorPane) loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.NONE);
            stage.initOwner(AllData.primaryStage);
            Scene scene = new Scene(createProjectPane);
            stage.setScene(scene);

            AllData.createProjectWindowController = loader.getController();
            AllData.createProjectWindow = stage;

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);

        }
    }


    public void closeApp() {
        primaryStage.close();
        System.exit(0);
    }

    public void showAboutWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/AboutWindow.fxml"));
            AnchorPane aboutWindow = (AnchorPane) loader.load();

            Stage aboutStage = new Stage();
            aboutStage.setTitle("О программе");
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.initOwner(primaryStage);
            Scene scene = new Scene(aboutWindow);
            aboutStage.setScene(scene);
            AboutWindowController aboutWindowController = loader.getController();
            aboutWindowController.getJavaRushHyperlink().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    getHostServices().showDocument("https://javarush.ru");
                    aboutWindowController.getJavaRushHyperlink().setVisited(false);
                }
            });
            aboutWindowController.getJavaRushHyperlink().setVisited(false);
            aboutWindowController.setAboutStage(aboutStage);

            aboutStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AllData.logger.error(e.getMessage(), e);
        }
    }
}
