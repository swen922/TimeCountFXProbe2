package com.horovod.timecountfxprobe.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

public class AboutWindowController {

    private Stage aboutStage;

    @FXML
    private Hyperlink javaRushHyperlink;

    @FXML
    private Button closeButton;

    public Stage getAboutStage() {
        return aboutStage;
    }

    public void setAboutStage(Stage aboutStage) {
        this.aboutStage = aboutStage;
    }

    public Hyperlink getJavaRushHyperlink() {
        return javaRushHyperlink;
    }

    public void setJavaRushHyperlink(Hyperlink javaRushHyperlink) {
        this.javaRushHyperlink = javaRushHyperlink;
    }

    public void handleCloseButton() {
        this.aboutStage.close();
    }
}
