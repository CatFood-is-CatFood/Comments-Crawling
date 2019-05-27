/**
 * 右边列表尚未完成
 * 包括实时显示选择的keyword，以及下方的权重选择
 */
package ch.makery.crawling.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.makery.crawling.MainApp;
import ch.makery.crawling.model.ChartSet;
import ch.makery.crawling.model.Goods;
import ch.makery.crawling.model.KeyType;
import ch.makery.crawling.model.segmenter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jsoup.Jsoup;

public class CompareController {
    @FXML
    private TextField inKeyWord;
    @FXML
    private Button back;
    @FXML
    private SplitMenuButton compare;
    @FXML
    private TableView<Goods> goodsTable;
    @FXML
    private TableColumn<Goods, String> pictureColumn;
    @FXML
    private TableColumn<Goods, String> nameColumn;
    @FXML
    private TableColumn<Goods, Boolean> actionCol;
    @FXML
    private Button add;
    @FXML
    private ScrollPane scroll;
    private FlowPane flowPane = new FlowPane();
    private MainApp mainApp;

    @FXML
    private MenuButton levelOne;
    @FXML
    private MenuButton levelTwo;
    @FXML
    private MenuButton levelThree;

    //表格Button
    @FXML
    private MenuItem grade;
    @FXML
    private MenuItem keyWords;
    @FXML
    private MenuItem applauseRate;

    @FXML
    private CheckBox ten = new CheckBox();
    @FXML
    private CheckBox thirty = new CheckBox();
    @FXML
    private CheckBox fifty = new CheckBox();
    @FXML
    private Button confirm;

    @FXML
    private TextField time = new TextField();

    public CompareController() {
    }

    @FXML
    private void initialize() {
        scroll.setContent(flowPane);
        flowPane.setMaxWidth(180);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        ten.setIndeterminate(false);
        ten.setSelected(false);
        thirty.setIndeterminate(false);
        thirty.setSelected(false);
        fifty.setIndeterminate(false);
        fifty.setSelected(false);
        ten.setOnAction(e -> {
            if (!ten.isSelected() && (thirty.isSelected() || fifty.isSelected()))
                ten.setSelected(true);
        });
        thirty.setOnAction(e -> {
            if (thirty.isSelected()) {
                ten.setSelected(true);
            } else {
                if (fifty.isSelected())
                    thirty.setSelected(true);
            }
        });
        fifty.setOnAction(e -> {
            if (fifty.isSelected()) {
                ten.setSelected(true);
                thirty.setSelected(true);
            }
        });

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
        nameColumn.setCellFactory(col -> {
            TableCell<Goods, String> cell = new TableCell<Goods, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        Tooltip tooltip = new Tooltip();
                        tooltip.setText(this.getTableView().getItems().get(this.getIndex()).getGoodsName());
                        this.setTooltip(tooltip);
                        this.setText(this.getTableView().getItems().get(this.getIndex()).getGoodsName());
                    }
                }
            };
            return cell;
        });

        inKeyWord.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                handleAdd();
        });

        time.clear();
        time.setPromptText("周期1min起");
        time.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^[0-9]+$")) {
                    String str = newValue.replaceAll("[^0-9]", "");
                    time.setText(str);
                }
            }
        });
        time.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER)
                handleOK();
        });
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        goodsTable.setItems(mainApp.getChosenData());

        List<String> keyWord = new ArrayList<>();
        mainApp.getChosenData().forEach(e -> {
            keyWord.addAll(e.getKeyWord());
            e.getKeyWord().clear();
        });
        keyWord.stream().distinct().forEach(e -> {
            inKeyWord.clear();
            inKeyWord.setText(e);
            handleAdd();
            inKeyWord.clear();
            if (mainApp.getLevelOne().contains(e)) {
                levelOne.getItems().removeIf(r -> r.getText().equals(e));
                levelTwo.getItems().removeIf(r -> r.getText().equals(e));
                levelThree.getItems().removeIf(r -> r.getText().equals(e));
                CheckMenuItem mi = new CheckMenuItem(e);
                mi.setSelected(true);
                mi.setOnAction(a -> {
                    if (mi.isSelected()) setGrade(0, e);
                    else removeGrade(0, e);
                });
                levelOne.getItems().add(mi);
            } else if (mainApp.getLevelTwo().contains(e)) {
                levelOne.getItems().removeIf(r -> r.getText().equals(e));
                levelTwo.getItems().removeIf(r -> r.getText().equals(e));
                levelThree.getItems().removeIf(r -> r.getText().equals(e));
                CheckMenuItem mi = new CheckMenuItem(e);
                mi.setSelected(true);
                mi.setOnAction(a -> {
                    if (mi.isSelected()) setGrade(1, e);
                    else removeGrade(1, e);
                });
                levelTwo.getItems().add(mi);
            } else if (mainApp.getLevelThree().contains(e)) {
                levelOne.getItems().removeIf(r -> r.getText().equals(e));
                levelTwo.getItems().removeIf(r -> r.getText().equals(e));
                levelThree.getItems().removeIf(r -> r.getText().equals(e));
                CheckMenuItem mi = new CheckMenuItem(e);
                mi.setSelected(true);
                mi.setOnAction(a -> {
                    if (mi.isSelected()) setGrade(2, e);
                    else removeGrade(2, e);
                });
                levelThree.getItems().add(mi);
            }
        });
    }

    @FXML
    public void handleAdd() {
        String s = inKeyWord.getText().trim();
        if (!mainApp.getChosenData().get(0).getKeyWord().contains(s)) {
            inKeyWord.setText("");
            //在选中的goods中添加选中的关键词
            for (Goods g : goodsTable.getItems()) {
                g.getKeyWord().add(s);
            }

            //分别在评级处建立按钮
            levelOne.getItems().add(setCheckMenuItem(0, s));
            levelTwo.getItems().add(setCheckMenuItem(1, s));
            levelThree.getItems().add(setCheckMenuItem(2, s));

            Button b = new Button(s);
            flowPane.getChildren().add(b);
            //按下右上方按钮时，弹出删除关键词窗口
            b.setOnAction(e -> {
                Stage dialog = new Stage();
                dialog.setHeight(150);
                dialog.setWidth(300);
                dialog.setTitle("提示");
                dialog.initOwner(mainApp.getPrimaryStage());  //对话框永远在前面
                dialog.initModality(Modality.WINDOW_MODAL);  //必须关闭对话框后才能操作其他的
                dialog.initStyle(StageStyle.UTILITY); //对话框-只保留关闭按钮

                FlowPane flow = new FlowPane();
                Label lab = new Label("是否删除？");
                flow.getChildren().add(lab);

                Button ok = new Button("Yes");
                ok.setDefaultButton(true);
                Button cancel = new Button("No");
                cancel.setCancelButton(true);

                ok.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        flowPane.getChildren().remove(b);
                        removeAllCheckMenuItem(s);
                        for (Goods g : goodsTable.getItems()) {
                            g.getKeyWord().remove(s);
                        }

                        actionCol.setCellFactory(cells -> {
                            TableCell<Goods, Boolean> call = new TableCell<Goods, Boolean>() {
                                final ScrollPane scrollPane = new ScrollPane();
                                final HBox hBox = new HBox();

                                @Override
                                protected void updateItem(Boolean item, boolean empty) {
                                    if (this.getIndex() < cells.getTableView().getItems().size() && this.getIndex() >= 0) {
                                        super.updateItem(item, empty);
                                        scrollPane.setContent(hBox);
                                        Goods good = cells.getTableView().getItems().get(this.getIndex());
                                        for (String str : good.getKeyWord()) {
                                            List<String> list1 = segmenter.customerCount(good.getComments(), str);
                                            if (list1.size() < 1) {
                                                Button b = new Button(str + " 0");
                                                hBox.getChildren().add(b);
                                                continue;
                                            }
                                            Button b = new Button(str + " " + KeyType.wordCounter(good, str));
                                            //添加监听器
                                            b.setOnAction(new EventHandler<ActionEvent>() {
                                                @Override
                                                public void handle(ActionEvent event) {
                                                    showKeyComments(str, good);
                                                }
                                            });
                                            hBox.getChildren().add(b);
                                        }

                                        if (!empty) {
                                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                            setGraphic(scrollPane);
                                        } else {
                                            setGraphic(null);
                                        }
                                    }

                                }
                            };
                            return call;
                        });
                        dialog.close();
                    }
                });
                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        dialog.close();
                    }
                });

                // layout the dialog.
                HBox buttons = HBoxBuilder.create().spacing(10).children(ok, cancel).alignment(Pos.CENTER_RIGHT).build();
                VBox layout = new VBox(10);
                layout.getChildren().addAll(flow, buttons);
                layout.setPadding(new Insets(5));
                dialog.setScene(new Scene(layout));
                dialog.show();
            });

            actionCol.setCellFactory(cells -> {
                TableCell<Goods, Boolean> call = new TableCell<Goods, Boolean>() {
                    final ScrollPane scrollPane = new ScrollPane();
                    final HBox hBox = new HBox();

                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        if (this.getIndex() < cells.getTableView().getItems().size() && this.getIndex() >= 0) {
                            super.updateItem(item, empty);
                            scrollPane.setContent(hBox);
                            Goods good = cells.getTableView().getItems().get(this.getIndex());
                            for (String str : good.getKeyWord()) {
                                List<String> list1 = segmenter.customerCount(good.getComments(), str);
                                if (list1.size() < 1) {
                                    Button b = new Button(str + " 0");
                                    hBox.getChildren().add(b);
                                    continue;
                                }
                                Button b = new Button(str + " " + KeyType.wordCounter(good, str));
                                //添加监听器
                                b.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        showKeyComments(str, good);
                                    }
                                });
                                hBox.getChildren().add(b);
                            }

                            if (!empty) {
                                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                setGraphic(scrollPane);
                            } else {
                                setGraphic(null);
                            }
                        }

                    }
                };
                return call;
            });
        }
    }

    //返回按钮的方法
    @FXML
    public void handleBack() {
        mainApp.showSearch();
        mainApp.getChosenData().forEach(e -> e.getKeyWord().clear());
        mainApp.getLevelOne().clear();
        mainApp.getLevelTwo().clear();
        mainApp.getLevelThree().clear();
        mainApp.getTimer().cancel();
    }

    //关键词检索

    //展示每个包括每个关键词的评论
    public void showKeyComments(String keyWord, Goods good) {
        List<String> list = good.getComments();
        Stage dialog = new Stage();
        dialog.setTitle(keyWord + " comments");
        dialog.setResizable(false);

        ScrollPane sp = new ScrollPane();
        GridPane grid = new GridPane();
        int count = 0;
        //System.out.println(list.size());
        for (int i = 0; i < list.size(); i++) {
            if (KeyType.isSuit(list.get(i), keyWord)) {
                grid.addRow(count, new Label("No." + (count / 2 + 1) + ": " + list.get(i)));
                grid.addRow(count + 1);
                count += 2;
            }
        }
        grid.setHgap(10);
        grid.setVgap(10);
        sp.setContent(grid);
        grid.setPrefHeight(400);
        grid.setPrefWidth(800);
        sp.prefHeight(400);
        sp.prefWidth(800);
        VBox layout = new VBox(10);
        layout.getChildren().add(sp);
        layout.setPadding(new Insets(5));
        dialog.setScene(new Scene(layout));
        dialog.setHeight(300);
        dialog.setWidth(400);
        dialog.show();

    }

    //设置评分系统
    //右下评分选中某一项时的操作，删除另外两项中的button，在mainApp中添加关键词
    public void setGrade(int index, String keyWord) {
        switch (index) {
            case 0: {
                mainApp.getLevelOne().add(keyWord);
                for (int i = 0; i < levelTwo.getItems().size(); i++) {
                    if (levelTwo.getItems().get(i).getText().equals(keyWord)) levelTwo.getItems().remove(i);
                }
                for (int i = 0; i < levelThree.getItems().size(); i++) {
                    if (levelThree.getItems().get(i).getText().equals(keyWord)) levelThree.getItems().remove(i);
                }
                break;
            }
            case 1: {
                mainApp.getLevelTwo().add(keyWord);
                for (int i = 0; i < levelThree.getItems().size(); i++) {
                    if (levelThree.getItems().get(i).getText().equals(keyWord)) levelThree.getItems().remove(i);
                }
                for (int i = 0; i < levelOne.getItems().size(); i++) {
                    if (levelOne.getItems().get(i).getText().equals(keyWord)) levelOne.getItems().remove(i);
                }
                break;
            }
            case 2: {
                mainApp.getLevelThree().add(keyWord);
                for (int i = 0; i < levelOne.getItems().size(); i++) {
                    if (levelOne.getItems().get(i).getText().equals(keyWord)) levelOne.getItems().remove(i);
                }
                for (int i = 0; i < levelTwo.getItems().size(); i++) {
                    if (levelTwo.getItems().get(i).getText().equals(keyWord)) levelTwo.getItems().remove(i);
                }
                break;
            }
        }
    }

    //取消勾选时，在mainApp中删除取消勾选的关键词，并在另外格子中加入相关button
    public void removeGrade(int index, String keyWord) {
        switch (index) {
            case 0: {
                for (int i = 0; i < mainApp.getLevelOne().size(); i++) {
                    if (mainApp.getLevelOne().get(i).equals(keyWord)) mainApp.getLevelOne().remove(i);
                }
                ;
                levelTwo.getItems().add(setCheckMenuItem(1, keyWord));
                levelThree.getItems().add(setCheckMenuItem(2, keyWord));
                break;
            }
            case 1: {
                for (int i = 0; i < mainApp.getLevelTwo().size(); i++) {
                    if (mainApp.getLevelTwo().get(i).equals(keyWord)) mainApp.getLevelTwo().remove(i);
                }
                ;
                levelThree.getItems().add(setCheckMenuItem(2, keyWord));
                levelOne.getItems().add(setCheckMenuItem(0, keyWord));
                break;
            }
            case 2: {
                for (int i = 0; i < mainApp.getLevelThree().size(); i++) {
                    if (mainApp.getLevelThree().get(i).equals(keyWord)) mainApp.getLevelThree().remove(i);
                }
                ;
                levelOne.getItems().add(setCheckMenuItem(0, keyWord));
                levelTwo.getItems().add(setCheckMenuItem(1, keyWord));
                break;
            }
        }
    }

    //删除该关键词时，删除mainApp中的button
    public void removeAllCheckMenuItem(String keyWord) {
        for (int i = 0; i < mainApp.getLevelOne().size(); i++) {
            if (mainApp.getLevelOne().get(i).equals(keyWord)) mainApp.getLevelOne().remove(i);
        }

        for (int i = 0; i < mainApp.getLevelTwo().size(); i++) {
            if (mainApp.getLevelOne().get(i).equals(keyWord)) mainApp.getLevelOne().remove(i);
        }

        for (int i = 0; i < mainApp.getLevelThree().size(); i++) {
            if (mainApp.getLevelOne().get(i).equals(keyWord)) mainApp.getLevelOne().remove(i);
        }

        for (int i = 0; i < levelOne.getItems().size(); i++) {
            if (levelOne.getItems().get(i).getText().equals(keyWord)) levelOne.getItems().remove(i);
        }
        for (int i = 0; i < levelTwo.getItems().size(); i++) {
            if (levelTwo.getItems().get(i).getText().equals(keyWord)) levelTwo.getItems().remove(i);
        }
        for (int i = 0; i < levelThree.getItems().size(); i++) {
            if (levelThree.getItems().get(i).getText().equals(keyWord)) levelThree.getItems().remove(i);
        }
    }

    //添加button到相应位置，0-》levelOne；1 -》 levelTwo； 2- 》 levelThree
    public CheckMenuItem setCheckMenuItem(int index, String keyWord) {
        CheckMenuItem mi = new CheckMenuItem(keyWord);
        mi.setOnAction(e -> {
            if (mi.isSelected()) setGrade(index, keyWord);
            else removeGrade(index, keyWord);
        });
        return mi;
    }

    //显示评分表格
    @FXML
    public void handleGradeChart() {
        Stage stage = ChartSet.gradeChart(mainApp);
        stage.setResizable(false);
        stage.show();
    }

    //显示关键词表格
    @FXML
    public void handleKeyWordChart() {
        if (mainApp.getChosenData().get(0).getKeyWord().size() == 0) {
            Stage dialog = new Stage();
            dialog.setTitle("Hint");
            BorderPane bp = new BorderPane();
            bp.setCenter(new Label("Please input at least one KeyWord."));
            dialog.setScene(new Scene(bp));
            dialog.setHeight(200);
            dialog.setWidth(300);
            dialog.show();
            return;
        }
        ChartSet.keyWordChart(mainApp);
    }

    //显示好评率表格
    @FXML
    public void handleApplauseRateChart() {
        ChartSet.ApplauseRateChart(mainApp);
    }

    @FXML
    public void handleCompare() {
        handleGradeChart();
        handleKeyWordChart();
        handleApplauseRateChart();
    }

    @FXML
    private void handleConfirm() {
        int length = 0;
        if (ten.isSelected()) length = 10;
        if (thirty.isSelected()) length = 30;
        if (fifty.isSelected()) length = 50;
        final int LENGTH = length;
        SearchController.DownComment(mainApp);
        mainApp.getChosenData().forEach(e -> {
            Iterator<String> iterator = e.getComments().iterator();
            while (iterator.hasNext()) {
                String str = iterator.next();
                if (str.length() <= LENGTH)
                    iterator.remove();
            }
        });

        actionCol.setCellFactory(cells -> {
            TableCell<Goods, Boolean> call = new TableCell<Goods, Boolean>() {
                final ScrollPane scrollPane = new ScrollPane();
                final HBox hBox = new HBox();

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    if (this.getIndex() < cells.getTableView().getItems().size() && this.getIndex() >= 0) {
                        super.updateItem(item, empty);
                        scrollPane.setContent(hBox);
                        Goods good = cells.getTableView().getItems().get(this.getIndex());
                        for (String str : good.getKeyWord()) {
                            List<String> list1 = segmenter.customerCount(good.getComments(), str);
                            if (list1.size() < 1) {
                                Button b = new Button(str + " 0");
                                hBox.getChildren().add(b);
                                continue;
                            }
                            Button b = new Button(str + " " + KeyType.wordCounter(good, str));
                            //添加监听器
                            b.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    showKeyComments(str, good);
                                }
                            });
                            hBox.getChildren().add(b);
                        }

                        if (!empty) {
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            setGraphic(scrollPane);
                        } else {
                            setGraphic(null);
                        }
                    }

                }
            };
            return call;
        });
    }

    @FXML
    public void handleOK() {
        String str = time.getText().trim();
        if (!str.equals("") && Integer.parseInt(str) >= 1) {
            mainApp.setTimer(new Timer());
            mainApp.getTimer().schedule(new TimerTask() {
                @Override
                public void run() {
                    List<Goods> list = mainApp.getChosenData();
                    for (int i = 0; i < list.size(); i++) {
                        Set<String> hash = Collections.synchronizedSet(new HashSet<>());
                        List<String> comments = Collections.synchronizedList(new ArrayList<>());
                        List<String> commentsURL = Collections.synchronizedList(new ArrayList<>());
                        Goods good = list.get(i);

                        File file = new File("data/Comments/" + good.getGoodsName());
                        if (file.exists() || file.mkdirs()) {
                            for (int j = 0; j < 100; j++)
                                commentsURL.add("https://rate.tmall.com/list_detail_rate.htm?itemId=" + good.getItemId() + "&sellerId=" + good.getSellerId() + "&currentPage=" + (j + 1));

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
                        }
                        good.setComments(comments);
                    }
                    //System.out.println("success");

                    actionCol.setCellFactory(cells -> {
                        TableCell<Goods, Boolean> call = new TableCell<Goods, Boolean>() {
                            final ScrollPane scrollPane = new ScrollPane();
                            final HBox hBox = new HBox();

                            @Override
                            protected void updateItem(Boolean item, boolean empty) {
                                if (this.getIndex() < cells.getTableView().getItems().size() && this.getIndex() >= 0) {
                                    super.updateItem(item, empty);
                                    scrollPane.setContent(hBox);
                                    Goods good = cells.getTableView().getItems().get(this.getIndex());
                                    for (String str : good.getKeyWord()) {
                                        List<String> list1 = segmenter.customerCount(good.getComments(), str);
                                        if (list1.size() < 1) {
                                            Button b = new Button(str + " 0");
                                            hBox.getChildren().add(b);
                                            continue;
                                        }
                                        Button b = new Button(str + " " + KeyType.wordCounter(good, str));
                                        //添加监听器
                                        b.setOnAction(new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent event) {
                                                showKeyComments(str, good);
                                            }
                                        });
                                        hBox.getChildren().add(b);
                                    }

                                    if (!empty) {
                                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                        setGraphic(scrollPane);
                                    } else {
                                        setGraphic(null);
                                    }
                                }

                            }
                        };
                        return call;
                    });
                }
            }, 0, Integer.parseInt(str) * 60 * 1000);
        handleConfirm();
        }
    }
}
