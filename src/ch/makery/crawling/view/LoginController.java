package ch.makery.crawling.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField inputName = new TextField();
    @FXML
    private PasswordField inputContent = new PasswordField();
    @FXML
    private CheckBox doNotForget;
    @FXML
    private Tooltip tooltip = new Tooltip();

    private Stage loginStage;
    private Map<String, String> cookiesUsing = new HashMap<>();
    private Map<String, String> cookiesSaved = new HashMap<>();

    @FXML
    private void initialize() {
        inputName.clear();
        inputName.setText("x5sec");
        inputName.setEditable(false);

        inputContent.clear();
        inputContent.setPromptText("The content of cookie \"x5sec\"");

        tooltip.setText("Please open your system brower\n" +
                "And fine the content of \"x5sec\" in cookies of \"list.tmall.com\"\n");
        inputContent.setTooltip(tooltip);
        tooltip.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("cookies.png"),120,0,true,true)));

        doNotForget.setSelected(false);
        doNotForget.setIndeterminate(false);
    }

    /*
     * @param loginStage
     * */
    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    public void setCookies(Map<String, String> cookiesUsing, Map<String, String> cookiesSaved) {
        this.cookiesUsing = cookiesUsing;
        this.cookiesSaved = cookiesSaved;
    }

    @FXML
    private void handleLogin() {
        if (isInputValid()) {
            cookiesUsing.put(inputName.getText().trim(), inputContent.getText().trim());
            if (doNotForget.isSelected())
                cookiesSaved.put(inputName.getText().trim(), inputContent.getText().trim());
            loginStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        loginStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (inputContent.getText() == null || inputContent.getText().length() == 0)
            errorMessage += "No valid cookie!\n";

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Fail!");
            alert.setHeaderText("Please input again!");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}
