package curver;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Curver extends Application {

    private Scene scene;
    private BarChart<String, Number> bc;
    private PieChart pc;
    private Pane pcPane;
    private Pane scPane;
    private VBox curveBox;
    private Text avgText;
    private TextField pointInput;
    private TextField addCurveInput;
    private TextField multiplyCurveInput;
    private TextField desAvgInput;
    private TextArea gradeInput;
    private Button enterBtn;
    private Button addCurveBtn;
    private Button multiplyCurveBtn;
    private Button desAvgBtn;
    private MenuButton curveButton;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Curver.fxml"));
        scene = new Scene(root, 975, 800);
        primaryStage.setTitle("Classroom Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
        Random rand = new Random();
        String exCase = "";
        for (int i = 0; i < 60; i++) {
            exCase += rand.nextInt(35)+5+",";
        }
        scPane = (Pane) scene.lookup("#scPane");
        curveBox = (VBox) scene.lookup("#curveBox");
        curveBox.setVisible(false);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        bc = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Grade");
        yAxis.setLabel("Percent");
        bc.setTitle("Occurences of grade");
        pcPane = (Pane) scene.lookup("#pcPane");
        pc = new PieChart();
        pc.setLegendVisible(false);
        gradeInput = (TextArea) scene.lookup("#gradeInput");
        gradeInput.setText(exCase);
        gradeInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d+.\\d+?,?")){
                gradeInput.setText(newValue.replaceAll("[^\\d+.\\d+?,?]", ""));
            }
        }));
        pointInput = (TextField) scene.lookup("#pointInput");
        pointInput.setText("40");
        pointInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d+.\\d+?")){
                pointInput.setText(newValue.replaceAll("[^\\d+.\\d+?]", ""));
            }
        });
        avgText = (Text) scene.lookup("#avg");
        enterBtn = (Button) scene.lookup("#enterBtn");
        enterBtn.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                double points;
                try{
                    points = Double.parseDouble(pointInput.getText());
                }
                catch (Exception ex){
                    points = 100;
                }
                ArrayList<Double> grades = getGrades();
                grades = getPercentGrades(grades, points);
                updateChart(grades);
                curveBox.setVisible(true);
            }
        });
        scPane.getChildren().addAll(bc);
        pcPane.getChildren().addAll(pc);
        curveButton = (MenuButton) scene.lookup("#curveButton");
        MenuItem byAvg = new MenuItem("By Average");
        MenuItem byHighScore = new MenuItem("By Highest Score");
        byAvg.setOnAction(e -> {
            ArrayList<Double> grades = getGrades();
            double avg = getGradeAvg(grades);
            grades = getPercentGrades(grades, avg);
            updateChart(grades);
            pointInput.setText(Integer.toString((int) avg));
        });
        byHighScore.setOnAction(e -> {
            ArrayList<Double> grades = getGrades();
            double highest = 1;
            for (Double grade : grades) {
                if(grade > highest)
                    highest = grade;
            }
            grades =  getPercentGrades(grades, highest);
            updateChart(grades);
            pointInput.setText(Double.toString(highest));
        });
        curveButton.getItems().addAll(byAvg, byHighScore);
        addCurveBtn = (Button) scene.lookup("#addCurveBtn");
        addCurveInput = (TextField) scene.lookup("#addCurveInput");
        addCurveBtn.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY)
                addCurve();
        });
        addCurveInput.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER)
                addCurve();
        });
        addCurveInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d+.\\d+?")){
                addCurveInput.setText(newValue.replaceAll("[^\\d+.\\d+?]", ""));
            }
        });
        multiplyCurveInput = (TextField) scene.lookup("#multiplyCurveInput");
        multiplyCurveBtn = (Button) scene.lookup("#multiplyCurveBtn");
        multiplyCurveBtn.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY)
                multiplyCurve();
        });
        multiplyCurveInput.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER)
                addCurve();
        });
        multiplyCurveInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d+.\\d+?")){
                multiplyCurveInput.setText(newValue.replaceAll("[^\\d+.\\d+?]", ""));
            }
        });
        desAvgInput = (TextField) scene.lookup("#desAvgInput");
        desAvgBtn = (Button) scene.lookup("#desAvgBtn");
        desAvgInput.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER)
                desAvg();
        });
        desAvgInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d+.\\d+?")){
                desAvgInput.setText(newValue.replaceAll("[^\\d+.\\d+?]", ""));
            }
        });
        desAvgBtn.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY)
                desAvg();
        });
    }

    private void updateChart(ArrayList<Double> grades){
        double avg = getGradeAvg(grades);
        avgText.setVisible(true);
        avgText.setText("Average: "+ avg +"%");
        HashMap<String, Integer> occurrences = new HashMap<>();
        for(Double grade: grades){
            String gradeLetter = getGradeLetter(grade);
            if(occurrences.keySet().contains(gradeLetter))
                occurrences.put(gradeLetter, occurrences.get(gradeLetter) + 1);
            else
                occurrences.put(gradeLetter, 1);
        }
        bc.getData().clear();
        pc.getData().clear();
        occurrences.forEach((letter, grade) -> {
            XYChart.Series series = new XYChart.Series();
            series.setName(letter);
            series.getData().add(new XYChart.Data("", (grade*100)/grades.size()));
            bc.getData().add(series);
            pc.getData().add(new PieChart.Data(letter + " - " + grade + "(" + (grade*100)/grades.size() + "%)", grade));
        });
    }

    private String getGradeLetter(Double grade){
        if(grade >= 90)
            return "A";
        else if(grade >= 80)
            return "B";
        else if(grade >= 70)
            return "C";
        else if(grade >= 60)
            return "D";
        else
            return "F";
    }

    public ArrayList<Double> getPercentGrades(ArrayList<Double> grades, double divisor){
        if(divisor == 0)
            return null;
        for (int i = 0; i < grades.size(); i++) {
            grades.set(i, (grades.get(i)*100)/divisor);
        }
        return grades;
    }

    private ArrayList<Double> getGrades(){
        String[] gradeStr = gradeInput.getText().replaceAll("\\s", "").split(",");
        ArrayList<Double> grades = new ArrayList<>();
        for (String g : gradeStr) {
            try{
                grades.add(Double.parseDouble(g));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return grades;
    }

    private double getGradeAvg(ArrayList<Double> grades){
        double sum = 0;
        for(Double gr : grades){
            sum += gr;
        }
        double avg = sum/(double) grades.size();
        avg = Math.round(avg * 100.0) / 100.0;
        return avg;
    }

    private void addCurve(){
        if(!addCurveInput.getText().isEmpty()){
            ArrayList<Double> grades = getGrades();
            gradeInput.setText("");
            double curvePoints = Double.parseDouble(addCurveInput.getText());
            for (int i = 0; i < grades.size(); i++) {
                grades.set(i, grades.get(i)+curvePoints);
                gradeInput.setText(gradeInput.getText()+grades.get(i)+",");
            }
            double points = Double.parseDouble(pointInput.getText());
            grades = getPercentGrades(grades, points);
            updateChart(grades);
        }
    }

    private void multiplyCurve(){
        if(!multiplyCurveInput.getText().isEmpty()){
            ArrayList<Double> grades = getGrades();
            gradeInput.setText("");
            double curvePoints = Double.parseDouble(multiplyCurveInput.getText());
            for (int i = 0; i < grades.size(); i++) {
                grades.set(i, Math.round(grades.get(i)*curvePoints*10.0)/10.0);
                gradeInput.setText(gradeInput.getText()+grades.get(i)+",");
            }
            double points = Double.parseDouble(pointInput.getText());
            grades = getPercentGrades(grades, points);
            updateChart(grades);
        }
    }

    private void desAvg(){
        if(!desAvgInput.getText().isEmpty()){
            ArrayList<Double> grades = getGrades();
            gradeInput.setText("");
            double desAvg = Double.parseDouble(desAvgInput.getText());
            double percent = (getGradeAvg(grades)*100)/Double.parseDouble(pointInput.getText());
            double factor = desAvg/percent;
            for (int i = 0; i < grades.size(); i++) {
                grades.set(i, Math.round(grades.get(i)*factor*10.0)/10.0);
                gradeInput.setText(gradeInput.getText()+grades.get(i)+",");
            }
            double points = Double.parseDouble(pointInput.getText());
            grades = getPercentGrades(grades, points);
            updateChart(grades);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}