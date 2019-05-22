package ch.makery.crawling.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ch.makery.crawling.MainApp;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class ChartSet {
    //创建评分表格
    public static Stage gradeChart(MainApp mainApp) {
        Stage stage = new Stage();
        stage.setTitle("Grade Chart");
        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();
        //x坐标为商品，y为评分
        final StackedBarChart<String, Number> bc =
                new StackedBarChart<>(xAxis, yAxis);
        bc.setTitle("Grade Chart");
        xAxis.setLabel("Goods");
        xAxis.setTickLabelRotation(90);
        yAxis.setLabel("Grade");
        int count = mainApp.getChosenData().size();
        for (int i = 0; i < count; i++) {
            double grade = grader.gradeCount(mainApp.getChosenData().get(i), mainApp);
            XYChart.Series series1 = new XYChart.Series();
            String title = (mainApp.getChosenData().get(i).getGoodsName().length() > 4 ? (String.format("%s...", mainApp.getChosenData().get(i).getGoodsName().substring(0, 3))) : (mainApp.getChosenData().get(i).getGoodsName()));
            series1.setName(mainApp.getChosenData().get(i).getGoodsName());
            series1.getData().add(new XYChart.Data(title, grade));

            bc.getData().add(series1);
        }

        bc.setCategoryGap((150 - count * 25 / 2) > 25 ? (150 - count * 25 / 2) : 25);
        Scene scene = new Scene(bc, 800, 600);
        stage.setScene(scene);
        return stage;
    }

    //创建关键词表格
    public static void keyWordChart(MainApp mainApp) {
        Stage stage = new Stage();
        stage.setTitle("KeyWord Chart");
        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();
        final BarChart<String, Number> bc =
                new BarChart<>(xAxis, yAxis);
        //z轴为商品，y轴为数量，分类进行比较
        bc.setTitle("KeyWord Chart");
        xAxis.setLabel("Goods");
        xAxis.setTickLabelRotation(90);
        yAxis.setLabel("Count");

        for (Goods good : mainApp.getChosenData()) {
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(good.getGoodsName());
            for (String keyWord : good.getKeyWord()) {
                Long l = KeyType.wordCounter(good, keyWord);
                String title = keyWord.length() > 5 ? keyWord.substring(0, 5) : keyWord;
                series1.getData().add(new XYChart.Data(keyWord, l));
            }
            bc.getData().add(series1);
        }
        Scene scene = new Scene(bc, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    //好评率比较
    public static void ApplauseRateChart(MainApp mainApp) {
        Stage stage = new Stage();
        stage.setTitle("Applause Rate Chart");
        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();
        final StackedBarChart<String, Number> bc =
                new StackedBarChart<>(xAxis, yAxis);
        bc.setTitle("Applause Rate Compare");
        xAxis.setLabel("Goods");
        xAxis.setTickLabelRotation(90);
        yAxis.setLabel("Applause Rate");
        int count = mainApp.getChosenData().size();
        for (int i = 0; i < count; i++) {
            double grade = grader.gradeCount(mainApp.getChosenData().get(i), mainApp);
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(mainApp.getChosenData().get(i).getGoodsName());
            BigDecimal bi1 = new BigDecimal(grader.badCount(mainApp.getChosenData().get(i)).toString());
            Long l = mainApp.getChosenData().get(i).getComments().stream().count();
            BigDecimal bi2 = new BigDecimal(l.toString());
            if (bi2.equals(new BigDecimal(0))) {
                String title = (mainApp.getChosenData().get(i).getGoodsName().length() > 4 ? (String.format("%s...", mainApp.getChosenData().get(i).getGoodsName().substring(0, 3))) : (mainApp.getChosenData().get(i).getGoodsName()));
                series1.getData().add(new XYChart.Data(title, 0));
            } else {
                BigDecimal divide = bi1.divide(bi2, 4, RoundingMode.HALF_UP);
                double applauseRate = divide.doubleValue();
                String title = (mainApp.getChosenData().get(i).getGoodsName().length() > 4 ? (String.format("%s...", mainApp.getChosenData().get(i).getGoodsName().substring(0, 3))) : (mainApp.getChosenData().get(i).getGoodsName()));
                series1.getData().add(new XYChart.Data(title, 100 - applauseRate * 100));
            }
            bc.getData().add(series1);
        }

        bc.setCategoryGap((150 - count * 25 / 2) > 25 ? (150 - count * 25 / 2) : 25);
        Scene scene = new Scene(bc, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

}
