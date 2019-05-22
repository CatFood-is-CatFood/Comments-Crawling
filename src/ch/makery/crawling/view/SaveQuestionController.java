package ch.makery.crawling.view;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class SaveQuestionController {
    private Stage saveQuestionStage;
    private boolean isSave = false;

    public boolean isSave() {
        return isSave;
    }

    @FXML
    private void initialize() {
    }

    /*
     * @param loginStage
     * */
    public void setSaveQuestionStage(Stage saveQuestionStage) {
        this.saveQuestionStage = saveQuestionStage;
    }

    @FXML
    private void handleYes() {
        isSave = true;
        saveQuestionStage.close();
    }

    @FXML
    private void handleNo() {
        isSave = false;
        saveQuestionStage.close();
    }
}
