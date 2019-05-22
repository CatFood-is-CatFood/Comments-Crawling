package ch.makery.crawling.view;

import ch.makery.crawling.model.Goods;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ch.makery.crawling.MainApp;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchController {
    @FXML
    private TextField inputGoodsName = new TextField();
    @FXML
    private Button search;
    @FXML
    private ContextMenu contextMenu = new ContextMenu();

    @FXML
    private TableView<Goods> goodsTable;
    @FXML
    private TableColumn<Goods, String> pictureColumn;
    @FXML
    private TableColumn<Goods, String> nameColumn;
    @FXML
    private TableColumn<Goods, String> priceColumn;
    @FXML
    private TableColumn<Goods, String> commentColumn;

    @FXML
    private TableView<Goods> chosenTable;
    @FXML
    private TableColumn<Goods, String> chosenPictureColumn;
    @FXML
    private TableColumn<Goods, String> chosenNameColumn;

    @FXML
    private Button add;
    @FXML
    private Button delete;

    @FXML
    private TextField priceUpperLimit = new TextField();
    @FXML
    private TextField priceDownLimit = new TextField();
    @FXML
    private Button confrim;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public SearchController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        inputGoodsName.clear();
        inputGoodsName.setPromptText("Please input what you want to search");
        inputGoodsName.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                handleSearchGoods();
        });
        inputGoodsName.setContextMenu(contextMenu);

        pictureColumn.setCellFactory((col) -> {
            TableCell<Goods, String> cell = new TableCell<Goods, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        WebView webView = new WebView();
                        webView.setPrefHeight(80);
                        webView.setPrefWidth(80);
                        WebEngine engine = webView.getEngine();
                        engine.load(this.getTableView().getItems().get(this.getIndex()).goodsPictureProperty().get());
                        this.setGraphic(webView);
                    }
                }
            };
            return cell;
        });
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().goodsNameProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentsNumberProperty());
        commentColumn.setCellFactory(col -> {
            TableCell<Goods, String> cell = new TableCell<Goods, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);

                    if (!empty) {
                        int n = Integer.parseInt(this.getTableView().getItems().get(this.getIndex()).getCommentsNumber());
                        if (n >= 10000) {
                            this.setText(String.format("%.1f万", n / 10000.0));
                        } else if (n == -1) {
                            this.setText("不详");
                        } else this.setText(n + "");
                    }
                }
            };
            return cell;
        });

        chosenPictureColumn.setCellFactory((col) -> {
            TableCell<Goods, String> cell = new TableCell<Goods, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        WebView webView = new WebView();
                        webView.setPrefHeight(80);
                        webView.setPrefWidth(80);
                        WebEngine engine = webView.getEngine();
                        engine.load(this.getTableView().getItems().get(this.getIndex()).goodsPictureProperty().get());
                        this.setGraphic(webView);
                    }
                }
            };
            return cell;
        });

        priceColumn.setComparator((r1, r2) -> {
            double p1, p2;
            p1 = Double.parseDouble(r1);
            p2 = Double.parseDouble(r2);
            if (p1 == p2) return 0;
            return p1 > p2 ? 1 : -1;
        });

        commentColumn.setComparator((r1, r2) -> {
            if (r2.equals("-1")) return 1;
            else if (r1.equals("-1")) return -1;
            else return Integer.parseInt(r1) - Integer.parseInt(r2);
        });

        chosenNameColumn.setCellValueFactory(cellData -> cellData.getValue().goodsNameProperty());

        priceDownLimit.clear();
        priceDownLimit.setPromptText("00.00");
        priceDownLimit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^[0-9]+\\.?[0-9]*$")) {
                    String[] str = newValue.split("\\.");
                    for (int i = 0; i < str.length; i++) {
                        str[i] = str[i].replaceAll("[^0-9]", "");
                    }
                    String output = "";
                    output += str[0] + (str.length > 1 ? "." : "");
                    for (int i = 1; i < str.length; i++)
                        output += (str[i]);
                    priceDownLimit.setText(output.toString());
                }
            }
        });

        priceUpperLimit.clear();
        priceUpperLimit.setPromptText("00.00");
        priceUpperLimit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^[0-9]+\\.?[0-9]*$")) {
                    String[] str = newValue.split("\\.");
                    for (int i = 0; i < str.length; i++) {
                        str[i] = str[i].replaceAll("[^0-9]", "");
                    }
                    String output = "";
                    output += str[0] + (str.length > 1 ? "." : "");
                    for (int i = 1; i < str.length; i++)
                        output += (str[i]);
                    priceUpperLimit.setText(output.toString());
                }
            }
        });
    }

    /**
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        goodsTable.setItems(mainApp.getShownGoodsData());
        chosenTable.setItems(mainApp.getChosenData());
    }

    @FXML
    private void handleDeleteChosen() {
        int selectedIndex = chosenTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            chosenTable.getSelectionModel().select(0);
            selectedIndex = chosenTable.getSelectionModel().getSelectedIndex();
        }
        if (selectedIndex >= 0) {
            chosenTable.getItems().remove(selectedIndex);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Chosen Good Selected");
            alert.setContentText("Please select a chosen goods in the table.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAddGoods() {
        int selectedIndex = goodsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            goodsTable.getSelectionModel().select(0);
            selectedIndex = goodsTable.getSelectionModel().getSelectedIndex();
        }
        if (selectedIndex >= 0) {
            Goods goods = goodsTable.getItems().get(selectedIndex);
            if (!chosenTable.getItems().contains(goods)) {
                chosenTable.getItems().add(goods);
                goodsTable.getSelectionModel().select(selectedIndex + 1);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Selected");
                alert.setHeaderText("Have Selected");
                alert.setContentText("Not Select The Same Goods Again.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Goods Selected");
            alert.setContentText("Please Search Again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSearchGoods() {
        String str = inputGoodsName.getText().trim();
        if (!str.equals("")) {
            try {
                priceDownLimit.clear();
                priceUpperLimit.clear();
                mainApp.getGoodsData().clear();
                mainApp.getShownGoodsData().clear();
                String url = "https://list.tmall.com/search_product.htm?q="
                        + java.net.URLEncoder.encode(str, "utf-8");
                Document search = Jsoup.connect(url).get();
                Elements goods = search.select("div.product");
                Collections.synchronizedList(goods).parallelStream().forEach(e -> {
                    String itemId, sellerId, goodsPicture, goodsName, price, commentsNumber;
                    Matcher matcher;
                    String content = goods.get(goods.indexOf(e)).toString();

                    Pattern pattern = Pattern.compile("data-id=\"([0-9]+)\".*?user_id=([0-9]+).*?\"(//img.*?)\">.*?<em title=\"([0-9]+.?[0-9]*)\">.*?target=\"_blank\" title=\"(.+?)\"", Pattern.DOTALL);
                    matcher = pattern.matcher(content);
                    matcher.find();
                    //System.out.println(content);
                    itemId = matcher.group(1);
                    sellerId = matcher.group(2);
                    goodsPicture = "https:" + matcher.group(3);
                    price = matcher.group(4);
                    goodsName = matcher.group(5);

                    pattern = Pattern.compile("评价.*?>([.0-9万]+)</a>");
                    matcher = pattern.matcher(content);
                    if (matcher.find())
                        if (matcher.group(1).endsWith("万"))
                            commentsNumber = (int) (Double.parseDouble(matcher.group(1).replace("万", "")) * 10000) + "";
                        else
                            commentsNumber = Integer.parseInt(matcher.group(1)) + "";
                    else
                        commentsNumber = -1 + "";

                    Goods good = new Goods(itemId, sellerId, goodsPicture, goodsName, price, commentsNumber);
                    mainApp.getGoodsData().add(good);
                    mainApp.getShownGoodsData().add(good);
                });
                sort();

                contextMenu.getItems().removeIf(e -> e.getText().equals(str));
                MenuItem item = new MenuItem(str);
                item.setOnAction(e -> inputGoodsName.setText(item.getText()));
                contextMenu.getItems().add(0, item);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @FXML
    private void handleConfirm() {
        if (mainApp.getGoodsData().size() < 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Search Result");
            alert.setHeaderText("No Search Result");
            alert.setContentText("Please Search Again.");
            alert.showAndWait();
        } else {
            mainApp.getShownGoodsData().clear();
            mainApp.getShownGoodsData().addAll(mainApp.getGoodsData());
            String downLimit = priceDownLimit.getText().trim();
            String upperLimit = priceUpperLimit.getText().trim();
            if (!downLimit.equals("") && !upperLimit.equals("") && Double.parseDouble(downLimit) > Double.parseDouble(upperLimit)) {
                String temp = downLimit;
                downLimit = upperLimit;
                upperLimit = temp;
            }
            if (!downLimit.equals("")) {
                double down = Double.parseDouble(downLimit);
                mainApp.getShownGoodsData().removeIf(s -> Double.parseDouble(s.getPrice()) < down);
            }
            if (!upperLimit.equals("")) {
                double upper = Double.parseDouble(upperLimit);
                mainApp.getShownGoodsData().removeIf(s -> Double.parseDouble(s.getPrice()) > upper);
            }
            sort();
        }
    }

    private void sort() {
        if (priceColumn.getSortType().equals(TableColumn.SortType.ASCENDING)) {
            priceColumn.setSortType(TableColumn.SortType.DESCENDING);
            priceColumn.setSortType(TableColumn.SortType.ASCENDING);
        } else if (priceColumn.getSortType().equals(TableColumn.SortType.DESCENDING)) {
            priceColumn.setSortType(TableColumn.SortType.ASCENDING);
            priceColumn.setSortType(TableColumn.SortType.DESCENDING);
        }
        if (commentColumn.getSortType().equals(TableColumn.SortType.ASCENDING)) {
            commentColumn.setSortType(TableColumn.SortType.DESCENDING);
            commentColumn.setSortType(TableColumn.SortType.ASCENDING);
        } else if (commentColumn.getSortType().equals(TableColumn.SortType.DESCENDING)) {
            commentColumn.setSortType(TableColumn.SortType.ASCENDING);
            commentColumn.setSortType(TableColumn.SortType.DESCENDING);
        }
    }

    @FXML
    public void handleShowCompare() {
        if (mainApp.getCookiesUsing().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Please Login First!");
            alert.showAndWait();
            Map<String, String> cookiesUsing = mainApp.getCookiesUsing();
            Map<String, String> cookiesSaved = mainApp.getCookiesSaved();
            mainApp.showLogin(cookiesUsing, cookiesSaved);
            return;
        }

        if (mainApp.getChosenData().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("No Chosen Goods!");
            alert.setContentText("Please select goods in the table first!");
            alert.showAndWait();
            return;
        }

        DownComment(mainApp);
        mainApp.showCompare();

    }

    public static void DownComment(MainApp mainApp) {
        List<Goods> list = mainApp.getChosenData();
        for (int i = 0; i < list.size(); i++) {
            Set<String> hash = Collections.synchronizedSet(new HashSet<>());
            List<String> comments = Collections.synchronizedList(new ArrayList<>());
            List<String> commentsURL = Collections.synchronizedList(new ArrayList<>());
            Goods good = list.get(i);

            File file = new File("data/Comments/" + good.getGoodsName());
            if (!file.exists() && file.mkdirs()) {
                for (int j = 0; j < 100; j++)
                    commentsURL.add("https://rate.tmall.com/list_detail_rate.htm?itemId=" + good.getItemId()
                            + "&sellerId=" + good.getSellerId() + "&currentPage=" + (j + 1));

                commentsURL.parallelStream().forEach(url -> {
                    try {
                        FileWriter fw = new FileWriter("data/Comments/" + good.getGoodsName() + "/" + (commentsURL.indexOf(url) + 1) + ".txt");
                        String document = Jsoup.connect(url).cookies(mainApp.getCookiesUsing()).get().text();
                        String reComments = "\"(content|rateContent)\":(?!\"此用户没有填写评论!\")(?!\"评价方未及时做出评价,系统默认好评!\")\"(.*?)\"";
                        Pattern pattern = Pattern.compile(reComments);
                        Matcher matcher = pattern.matcher(document);
                        while (matcher.find()) {
                            if (!hash.contains(matcher.group(2))) {
                                hash.add(matcher.group(2));
                                comments.add(matcher.group(2));
                                fw.write(matcher.group(2) + "\n");
                                fw.flush();
                            }
                        }
                        fw.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                });
            } else if (file.exists()) {
                try {
                    for (int j = 0; j < 100; j++) {
                        Scanner scan = new Scanner(new FileInputStream("data/Comments/" + good.getGoodsName() + "/" + (j + 1) + ".txt"));
                        while (scan.hasNextLine()) {
                            comments.add(scan.nextLine());
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

            good.setComments(comments);
        }
    }
}

