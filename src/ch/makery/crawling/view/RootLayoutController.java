package ch.makery.crawling.view;

import java.io.File;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import ch.makery.crawling.MainApp;

public class RootLayoutController {

    // Reference to the main application
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleNew() {
        mainApp.getChosenData().clear();
        mainApp.setGoodsFilePath(null);
    }

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadGoodsDataFromFile(file);
        }
    }

    @FXML
    private void handleSave() {
        File personFile = mainApp.getGoodsFilePath();
        if (personFile != null) {
            mainApp.saveGoodsDataToFile(personFile);
        } else {
            handleSaveAs();
        }
    }


    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.saveGoodsDataToFile(file);
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tmall Wrapper");
        alert.setHeaderText("About");
        alert.setContentText("Author: 邹泽桦 Zou Zehua 杨智宇 Yang Zhiyu");
        alert.showAndWait();
    }

    @FXML
    private void handleLogin() {
        Map<String, String> cookiesUsing = mainApp.getCookiesUsing();
        Map<String, String> cookiesSaved = mainApp.getCookiesSaved();
        mainApp.showLogin(cookiesUsing, cookiesSaved);
    }

    @FXML
    private void handleClear() {
        mainApp.getCookiesUsing().clear();
        mainApp.getCookiesSaved().clear();
    }
}