package ch.makery.crawling;

import ch.makery.crawling.model.Goods;
import ch.makery.crawling.model.GoodsListWrapper;
import ch.makery.crawling.view.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;

public class MainApp extends Application {
    private Timer timer = new Timer();
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Goods> goodsData = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private ObservableList<Goods> shownGoodsData = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private ObservableList<Goods> chosenData = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private Map<String, String> cookiesSaved = new HashMap<>();
    private Map<String, String> cookiesUsing = new HashMap<>();

    private List<String> levelOne = new ArrayList<>();
    private List<String> levelTwo = new ArrayList<>();
    private List<String> levelThree = new ArrayList<>();

    public MainApp() {
    }

    public void setTimer(Timer timer){
        this.timer = timer;
    }

    public Timer getTimer() {
        return timer;
    }

    public Map<String, String> getCookiesSaved() {
        return cookiesSaved;
    }

    public Map<String, String> getCookiesUsing() {
        return cookiesUsing;
    }

    public ObservableList<Goods> getShownGoodsData() {
        return shownGoodsData;
    }

    public ObservableList<Goods> getGoodsData() {
        return goodsData;
    }

    public ObservableList<Goods> getChosenData() {
        return chosenData;
    }

    public List<String> getLevelOne() {
        return levelOne;
    }

    public void setLevelOne(List<String> levelOne) {
        this.levelOne = levelOne;
    }

    public List<String> getLevelTwo() {
        return levelTwo;
    }

    public void setLevelTwo(List<String> levelTwo) {
        this.levelTwo = levelTwo;
    }

    public List<String> getLevelThree() {
        return levelThree;
    }

    public void setLevelThree(List<String> levelThree) {
        this.levelThree = levelThree;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setResizable(false);
        this.primaryStage.setTitle("Tmall Crawling");
        this.primaryStage.getIcons().add(new Image("file:src/ch/makery/crawling/view/tmall.png"));
        this.primaryStage.setOnCloseRequest(e -> {
            File file = getGoodsFilePath();
            try {
                boolean isSave = false;
                if (file == null) {
                    isSave = showSaveQuestion();
                } else {
                    JAXBContext context = JAXBContext
                            .newInstance(GoodsListWrapper.class);
                    Unmarshaller um = context.createUnmarshaller();

                    // Reading XML from the file and unmarshalling.
                    GoodsListWrapper wrapper = (GoodsListWrapper) um.unmarshal(file);
                    if (wrapper.getCookies() == null && cookiesSaved.size() != 0)
                        isSave = showSaveQuestion();
                    else if (wrapper.getCookies() != null && (!wrapper.getCookies().equals(cookiesSaved)))
                        isSave = showSaveQuestion();
                    else if (wrapper.getChosenGoods() == null && chosenData.size() != 0)
                        isSave = showSaveQuestion();
                    else if (wrapper.getChosenGoods() != null && (!wrapper.getChosenGoods().equals(chosenData)))
                        isSave = showSaveQuestion();
                    else if (wrapper.getLevelOne() == null && levelOne.size() != 0)
                        isSave = showSaveQuestion();
                    else if (wrapper.getLevelOne() != null && (!wrapper.getLevelOne().equals(levelOne)))
                        isSave = showSaveQuestion();
                    else if (wrapper.getLevelTwo() == null && levelTwo.size() != 0)
                        isSave = showSaveQuestion();
                    else if (wrapper.getLevelTwo() != null && (!wrapper.getLevelTwo().equals(levelTwo)))
                        isSave = showSaveQuestion();
                    else if (wrapper.getLevelThree() == null && levelThree.size() != 0)
                        isSave = showSaveQuestion();
                    else if (wrapper.getLevelThree() != null && (!wrapper.getLevelThree().equals(levelThree)))
                        isSave = showSaveQuestion();
                }

                if (isSave) {
                    e.consume();
                    if(file!=null)
                    saveGoodsDataToFile(file);
                    else {
                        FileChooser fileChooser = new FileChooser();

                        // Set extension filter
                        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                                "XML files (*.xml)", "*.xml");
                        fileChooser.getExtensionFilters().add(extFilter);

                        // Show save file dialog
                        File file1 = fileChooser.showSaveDialog(getPrimaryStage());

                        if (file1 != null) {
                            // Make sure it has the correct extension
                            if (!file1.getPath().endsWith(".xml")) {
                                file1 = new File(file1.getPath() + ".xml");
                            }
                            saveGoodsDataToFile(file1);
                        }
                    }
                }
                timer.cancel();
            } catch (Exception exception) {
                //catches ANY exception
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fail");
                alert.setHeaderText("Save Fail");
                alert.showAndWait();
                exception.printStackTrace();
            }

        });

        initRootLayout();

        showSearch();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = getGoodsFilePath();
        if (file != null) {
            loadGoodsDataFromFile(file);
        }
    }

    public void showSearch() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Search.fxml"));
            AnchorPane search = (AnchorPane) loader.load();

            rootLayout.setCenter(search);

            SearchController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLogin(Map<String, String> cookiesUsing, Map<String, String> cookiesSaved) {
        try {
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.initModality(Modality.WINDOW_MODAL);
            loginStage.initOwner(primaryStage);
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setResizable(false);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Login.fxml"));
            GridPane loginPane = (GridPane) loader.load();

            Scene scene = new Scene(loginPane);
            loginStage.setScene(scene);

            LoginController controller = loader.getController();
            controller.setLoginStage(loginStage);
            controller.setCookies(cookiesUsing, cookiesSaved);

            loginStage.showAndWait();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public boolean showSaveQuestion() {
        try {
            Stage saveQuestionStage = new Stage();
            saveQuestionStage.setTitle("Do you want to Save?");
            saveQuestionStage.initModality(Modality.WINDOW_MODAL);
            saveQuestionStage.initOwner(primaryStage);
            saveQuestionStage.setResizable(false);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SaveQuestion.fxml"));
            GridPane saveQuestionPane = (GridPane) loader.load();

            Scene scene = new Scene(saveQuestionPane);
            saveQuestionStage.setScene(scene);

            SaveQuestionController controller = loader.getController();
            controller.setSaveQuestionStage(saveQuestionStage);

            saveQuestionStage.showAndWait();
            return controller.isSave();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    public void showCompare() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Compare.fxml"));
            AnchorPane compare = (AnchorPane) loader.load();
            rootLayout.setCenter(compare);

            CompareController compareController = loader.getController();
            compareController.setMainApp(this);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public File getGoodsFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setGoodsFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("Tmall Crawling - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("Tmall Crawling");
        }
    }

    public void loadGoodsDataFromFile(File file) {
        try {
            chosenData.clear();
            cookiesSaved.clear();
            cookiesUsing.clear();
            levelOne.clear();
            levelTwo.clear();
            levelThree.clear();

            JAXBContext context = JAXBContext
                    .newInstance(GoodsListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            GoodsListWrapper wrapper = (GoodsListWrapper) um.unmarshal(file);
            if (wrapper.getChosenGoods() != null) {
                chosenData.addAll(wrapper.getChosenGoods());
            }
            if (wrapper.getCookies() != null) {
                cookiesUsing.putAll(wrapper.getCookies());
                cookiesSaved.putAll(wrapper.getCookies());
            }
            if (wrapper.getLevelOne() != null) {
                levelOne.addAll(wrapper.getLevelOne());
            }
            if (wrapper.getLevelTwo() != null) {
                levelTwo.addAll(wrapper.getLevelTwo());
            }
            if (wrapper.getLevelThree() != null) {
                levelThree.addAll(wrapper.getLevelThree());
            }

            // Save the file path to the registry.
            setGoodsFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Could not load data from file:\n" + file.getPath());
            alert.showAndWait();
        }
    }

    public void saveGoodsDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(GoodsListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our goods data.
            GoodsListWrapper wrapper = new GoodsListWrapper();
            wrapper.setChosenGoods(chosenData);
            wrapper.setCookies(cookiesSaved);
            wrapper.setLevelOne(levelOne);
            wrapper.setLevelTwo(levelTwo);
            wrapper.setLevelThree(levelThree);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setGoodsFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Could not save data from file:\n" + file.getPath());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
