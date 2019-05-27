package ch.makery.crawling.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import ch.makery.crawling.MainApp;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class ChartSet {

    public static Stage gradeChart(MainApp mainApp) {
        Stage stage = new Stage();
        stage.setTitle("Grade Chart");
        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();

        final StackedBarChart<String, Number> bc =
                new StackedBarChart<>(xAxis, yAxis);
        bc.setTitle("Grade Chart");
        xAxis.setLabel("Goods");

        yAxis.setLabel("Grade");
        int count = mainApp.getChosenData().size();
        ArrayList<String> titles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double grade = grader.gradeCount(mainApp.getChosenData().get(i), mainApp);
            XYChart.Series series1 = new XYChart.Series();
            String title = (mainApp.getChosenData().get(i).getGoodsName().length() > 7 ? (String.format("%s...", mainApp.getChosenData().get(i).getGoodsName().substring(0, 8))) : (mainApp.getChosenData().get(i).getGoodsName()));
            int c = 1;
            for(String s : titles) {
                if(s.contains(title) || s.equals(title)) c++;
            }
            if(c > 1) {
                title += "No. " + c;
            }
            series1.setName(mainApp.getChosenData().get(i).getGoodsName());
            series1.getData().add(new XYChart.Data(title, grade));

            titles.add(title);
            bc.getData().add(series1);
        }

        bc.setCategoryGap((150 - count * 25 / 2) > 25 ? (150 - count * 25 / 2) : 25);
        Scene scene = new Scene(bc, 800, 600);
        stage.setScene(scene);
        return stage;
    }


    public static void keyWordChart(MainApp mainApp) {
        Stage stage = new Stage();
        stage.setTitle("KeyWord Chart");
        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();
        final BarChart<String, Number> bc =
                new BarChart<>(xAxis, yAxis);

        bc.setTitle("KeyWord Chart");
        xAxis.setLabel("Goods");
        yAxis.setLabel("Count");

        for (Goods good : mainApp.getChosenData()) {
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(good.getGoodsName());
            for (String keyWord : good.getKeyWord()) {
                Long l = KeyType.wordCounter(good, keyWord);
                String title = keyWord.length() > 7 ? keyWord.substring(0, 8) : keyWord;
                series1.getData().add(new XYChart.Data(keyWord, l));
            }
            bc.getData().add(series1);
        }
        int count = mainApp.getChosenData().get(0).getKeyWord().size();
        if(count == 2) {
            bc.setCategoryGap(150);
        }
        Scene scene = new Scene(bc, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void ApplauseRateChart(MainApp mainApp) {
        Stage stage = new Stage();
        stage.setTitle("Applause Rate Chart");
        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();
        final StackedBarChart<String, Number> bc =
                new StackedBarChart<>(xAxis, yAxis);
        bc.setTitle("Applause Rate Compare");
        xAxis.setLabel("Goods");
        yAxis.setLabel("Applause Rate");
        int count = mainApp.getChosenData().size();
        ArrayList<String> titles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double grade = grader.gradeCount(mainApp.getChosenData().get(i), mainApp);
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(mainApp.getChosenData().get(i).getGoodsName());
            BigDecimal bi1 = new BigDecimal(grader.badCount(mainApp.getChosenData().get(i)).toString());
            Long l = mainApp.getChosenData().get(i).getComments().stream().count();
            BigDecimal bi2 = new BigDecimal(l.toString());
            if (bi2.equals(new BigDecimal(0))) {
                String title = (mainApp.getChosenData().get(i).getGoodsName().length() > 7 ? (String.format("%s...", mainApp.getChosenData().get(i).getGoodsName().substring(0, 8))) : (mainApp.getChosenData().get(i).getGoodsName()));
                int c = 1;
                for(String s : titles) {
                    if(s.contains(title) || s.equals(title)) c++;
                }
                if(c > 1) {
                    title += "No. " + c;
                }
                series1.getData().add(new XYChart.Data(title, 0));
                titles.add(title);
            } else {
                BigDecimal divide = bi1.divide(bi2, 4, RoundingMode.HALF_UP);
                double applauseRate = divide.doubleValue();
                String title = (mainApp.getChosenData().get(i).getGoodsName().length() > 5 ? (String.format("%s...", mainApp.getChosenData().get(i).getGoodsName().substring(0, 4))) : (mainApp.getChosenData().get(i).getGoodsName()));
                int c = 1;
                for(String s : titles) {
                    if(s.contains(title) || s.equals(title)) c++;
                }
                if(c > 1) {
                    title += "No. " + c;
                }
                series1.getData().add(new XYChart.Data(title, 100 - applauseRate * 100));
                titles.add(title);
            }
            bc.getData().add(series1);
        }

        bc.setCategoryGap((150 - count * 25 / 2) > 25 ? (150 - count * 25 / 2) : 25);
        Scene scene = new Scene(bc, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}