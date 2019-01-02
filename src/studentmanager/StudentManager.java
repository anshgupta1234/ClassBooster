package studentmanager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class StudentManager extends Application {

    Gson gson;
    ArrayList<Button> studentButtons;
    ArrayList<Student> students;
    Pane classroom;
    Button addStudentButton;
    Button saveClass;
    TextField studentInput;
    File positionFile;
    File studentFile;
    Student selectedStudent;
    VBox navMenu;
    double side;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("StudentManager.fxml"));
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("Classroom Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
        gson = new Gson();
        side = 75;
        classroom = (Pane) scene.lookup("#classroom");
        studentButtons = new ArrayList<>();
        navMenu = (VBox) scene.lookup("#navMenu");
        if(new File("ClassroomPositions.txt").exists() && !new File("ClassroomPositions.txt").toString().equals("")){
            positionFile = new File("ClassroomPositions.txt");
            LineNumberReader positionLNR = new LineNumberReader(new FileReader(positionFile));
            String line;
            while((line = positionLNR.readLine()) != null){
                if(!line.isEmpty()){
                    String[] attributes = line.split("☈");
                    Background bg = new Background(new BackgroundImage( new Image( getClass().getResource("wood.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT));
                    Button btn = new Button(attributes[0]);
                    btn.setMinWidth(side);
                    btn.setMinHeight(side);
                    btn.setMaxHeight(side);
                    btn.setMaxWidth(side);
                    btn.setWrapText(true);
                    btn.setTextAlignment(TextAlignment.CENTER);
                    btn.setBackground(bg);
                    btn.setStyle("-fx-text-fill: white");
                    classroom.getChildren().add(btn);
                    btn.setLayoutX(Double.parseDouble(attributes[1]));
                    btn.setLayoutY(Double.parseDouble(attributes[2]));
                    studentButtons.add(btn);
                    btn.setOnMouseClicked(e -> {
                        selectedStudent = students.get(studentButtons.indexOf(btn));
                        updateVBox();
                    });
                    btn.setOnMouseDragged(e -> {
                        btn.setLayoutX(e.getSceneX() - side/2);
                        btn.setLayoutY(e.getSceneY() - side/2);
                        if(btn.getLayoutX() > 900 || btn.getLayoutY() > 700 || btn.getLayoutX() < 0 || btn.getLayoutY() < 0){
                            removeStudent(btn.getText());
                            studentButtons.remove(btn);
                            classroom.getChildren().remove(btn);
                        }
                    });
                }
            }
        }
        studentFile = new File("StudentJson.txt");
        LineNumberReader studentLNR = new LineNumberReader(new FileReader(studentFile));
        String studentLine;
        Type type = new TypeToken<ArrayList<Student>>(){}.getType();
        while((studentLine = studentLNR.readLine()) != null){
            if(!studentLine.isEmpty()){
                students = gson.fromJson(studentLine, type);
            }
        }
        if(students == null)
            students = new ArrayList<>();
        addStudentButton = (Button) scene.lookup("#addStudentButton");
        studentInput = (TextField) scene.lookup("#studentInput");
        EventHandler<MouseEvent> addStudentHandler = event -> {
                if(!studentInput.getText().isEmpty()){
                    double side = 75;
                    Background bg = new Background(new BackgroundImage( new Image( getClass().getResource("wood.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT));
                    Button btn = new Button(studentInput.getText());
                    students.add(new Student(studentInput.getText()));
                    studentInput.setText("");
                    btn.setMinWidth(side);
                    btn.setMinHeight(side);
                    btn.setMaxHeight(side);
                    btn.setMaxWidth(side);
                    btn.setLayoutX(400);
                    btn.setLayoutY(300);
                    btn.setWrapText(true);
                    btn.setTextAlignment(TextAlignment.CENTER);
                    btn.setBackground(bg);
                    btn.setStyle("-fx-text-fill: white");
                    classroom.getChildren().add(btn);
                    studentButtons.add(btn);
                    btn.setOnMouseClicked(e -> {
                        selectedStudent = students.get(studentButtons.indexOf(btn));
                        updateVBox();
                    });
                    btn.setOnMouseDragged(e -> {
                        btn.setLayoutX(e.getSceneX() - side/2);
                        btn.setLayoutY(e.getSceneY() - side/2);
                    });
                }
            };
        addStudentButton.setOnMouseClicked(addStudentHandler);
        saveClass = (Button) scene.lookup("#save");
        saveClass.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                savePositions();
                try {
                    System.out.println(gson.toJson(students));
                    PrintWriter studentPW = new PrintWriter(studentFile);
                    studentPW.println(gson.toJson(students));
                    studentPW.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void updateVBox(){
        navMenu.getChildren().clear();
        Label friends = new Label("Friends: ");
        TextField friendInput = new TextField();
        Button addFriend = new Button("Add");
        HBox friendBox = new HBox(friendInput, addFriend);
        navMenu.getChildren().addAll(friends, friendBox);
        navMenu.setVisible(true);
    }

    public void savePositions(){
        try{
            PrintWriter pw = new PrintWriter(positionFile);
            for(Button btn : studentButtons){
                pw.println(btn.getText()+"☈"+btn.getLayoutX()+"☈"+btn.getLayoutY());
            }
            pw.close();
        } catch (Exception ex){
            positionFile = new File("ClassroomPositions.txt");
            try{
                PrintWriter pw = new PrintWriter(positionFile);
                for(Button btn : studentButtons){
                    pw.println(btn.getText()+"☈"+btn.getLayoutX()+"☈"+btn.getLayoutY());
                }
                pw.close();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    public void removeStudent(String name){
        for(Student student: students){
            if(student.getName().equals(name))
                students.remove(student);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
