package com.horovod.timecountfxprobe;


import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.test.Generator;
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
import java.time.LocalDate;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private AnchorPane statisticWindow;
    private Stage statStage;
    private TableProjectsDesignerController tableProjectsDesignerController;
    private StatisticWindowController statisticWindowController;


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Time Count System Probe FX -1");

        /** TODO убрать эту строчку в рабочем варианте */
        Generator.generateUsers();
        Generator.generateProjects2();

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
        }

    }

    public void initRootLayut() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setTitle("Заголовок окна");

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            AllData.setRootLayout(rootLayout);
        } catch (IOException e) {
            e.printStackTrace();
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

            tableProjectsDesignerController = loader.getController();
            AllData.setTableProjectsDesignerController(tableProjectsDesignerController);
            tableProjectsDesignerController.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showTableProjectsManager() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TableProjectsManager.fxml"));
            AnchorPane tableManager = (AnchorPane) loader.load();

            rootLayout.setCenter(tableManager);

            AllData.tableProjectsManagerController = (TableProjectsManagerController) loader.getController();
            AllData.tableProjectsManagerController.setMainApp(this);
            AllData.tableProjectsManagerController.setStage(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showEditProjectWindow(int projectIDnumber) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EditProjectWindow.fxml"));
            AnchorPane editWindow = (AnchorPane) loader.load();

            Stage editProjectStage = new Stage();
            editProjectStage.initModality(Modality.NONE);
            Scene scene = new Scene(editWindow);
            editProjectStage.setScene(scene);

            EditProjectWindowController controller = (EditProjectWindowController) loader.getController();
            controller.setMainApp(this);
            AllData.editProjectWindowControllers.put(projectIDnumber, controller);
            AllData.openEditProjectStages.put(projectIDnumber, editProjectStage);

            editProjectStage.show();

        } catch (IOException e) {
            e.printStackTrace();
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
            loginWindowController.setMainApp(this);

            loginWindowController.setStage(logWinStage);
            logWinStage.showAndWait();
        } catch (IOException e) {

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
            logWinStage.showAndWait();
        } catch (IOException e) {

        }
    }

    public void showStatisticWindow() {

        if (statisticWindow == null || statStage == null || statisticWindowController == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource("view/StatisticWindow.fxml"));
                statisticWindow = (AnchorPane) loader.load();
                statStage = new Stage();
                statStage.initModality(Modality.NONE);
                //statStage.initOwner(primaryStage);
                Scene scene = new Scene(statisticWindow);
                statStage.setScene(scene);
                statStage.show();

                statisticWindowController = loader.getController();
                statisticWindowController.setMainApp(this);
                statisticWindowController.setStage(statStage);
                AllData.setStatisticWindowController(statisticWindowController);
                AllData.setStatStage(statStage);

            } catch (IOException e) {
            }
        }
        else {
            statStage.hide();
            statStage.show();
            statisticWindowController.initialize();
            LocalDate today = LocalDate.now();
            int y = today.getYear();
            int m = today.getMonthValue();
            statisticWindowController.initializeBarChart(FillChartMode.DAILY, LocalDate.of(y, m, 1) );
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

            aboutStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
