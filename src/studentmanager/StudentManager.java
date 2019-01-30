package studentmanager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.*;

public class StudentManager extends Application {

    private Gson gson;
    private Scene scene;
    private ArrayList<Button> studentButtons;
    private HashMap<String, Student> students;
    private Pane classroom;
    private TitledPane titledMenu;
    private Button teacherDesk;
    private Button board;
    private Button addStudentButton;
    private Button saveClass;
    private Button shuffle;
    private Button print;
    private MenuButton modeButton;
    private TextField studentInput;
    private File positionFile;
    private File studentFile;
    private VBox navMenu;
    private enum Mode{
        EDIT, VIEW
    }
    private Mode currentMode;
    private double side;
    private boolean isStudentDragged;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("StudentManager.fxml"));
        scene = new Scene(root, 900, 800);
        primaryStage.setTitle("Classroom Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
        gson = new Gson();
        side = 75;
        currentMode = Mode.VIEW;
        classroom = (Pane) scene.lookup("#classroom");
        navMenu = (VBox) scene.lookup("#navMenu");
        teacherDesk = (Button) scene.lookup("#teacherDesk");
        teacherDesk.setOnMouseDragged(e -> {
            
            teacherDesk.setLayoutX(e.getSceneX() - teacherDesk.getWidth()/2 - 10);
            teacherDesk.setLayoutY(e.getSceneY() - teacherDesk.getHeight()/2 - 100);
        });
        board = (Button) scene.lookup("#board");
        board.setOnMouseDragged(e -> {
            
            titledMenu.setVisible(false);
            board.setLayoutX(e.getSceneX() - board.getWidth()/2 - 5);
            board.setLayoutY(e.getSceneY() - board.getHeight()/2 - 90);
        });
        titledMenu = (TitledPane) scene.lookup("#titledMenu");
        classroom.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                titledMenu.setVisible(false);
                
            }
        });
        studentButtons = new ArrayList<>();
        if(new File("ClassroomPositions.txt").exists() && !new File("ClassroomPositions.txt").toString().equals("")){
            positionFile = new File("ClassroomPositions.txt");
            LineNumberReader positionLNR = new LineNumberReader(new FileReader(positionFile));
            String line;
            while((line = positionLNR.readLine()) != null){
                if(!line.isEmpty()){
                    String[] attributes = line.split("☈");
                    Button btn = new Button(attributes[0]);
                    btn = makeDesk(btn);
                    classroom.getChildren().add(btn);
                    btn.setLayoutX(Double.parseDouble(attributes[1]));
                    btn.setLayoutY(Double.parseDouble(attributes[2]));
                    studentButtons.add(btn);
                }
            }
        }
        studentFile = new File("StudentJson.txt");
        LineNumberReader studentLNR = new LineNumberReader(new FileReader(studentFile));
        String studentLine;
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Type type2 = new TypeToken<ArrayList<Student.Characteristic>>(){}.getType();
        students = new HashMap<>();
        while((studentLine = studentLNR.readLine()) != null){
            if(!studentLine.isEmpty()){
                JsonParser parser = new JsonParser();
                JsonArray json = parser.parse(studentLine).getAsJsonArray();
                for(JsonElement st: json){
                    JsonObject student = st.getAsJsonObject();
                    ArrayList<String> friends = gson.fromJson(student.get("friends"), type);
                    ArrayList<String> ww = gson.fromJson(student.get("ww"), type);
                    ArrayList<String> dislikes = gson.fromJson(student.get("dislikes"), type);
                    ArrayList<Student.Characteristic> chars = gson.fromJson(student.get("chars"), type2);
                    Boolean needsFront = student.get("nf").getAsBoolean();
                    Student s = new Student(student.get("name").getAsString(), chars, friends, ww, dislikes, needsFront);
                    students.put(s.getName(), s);
                }
            }
        }
        addStudentButton = (Button) scene.lookup("#addStudentButton");
        studentInput = (TextField) scene.lookup("#studentInput");
        EventHandler<MouseEvent> addStudentHandler = event -> {
                if(!studentInput.getText().isEmpty() && !students.keySet().contains(studentInput.getText())){
                    Button btn = new Button(studentInput.getText());
                    students.put(studentInput.getText(), new Student(studentInput.getText()));
                    studentInput.setText("");
                    btn = makeDesk(btn);
                    classroom.getChildren().add(btn);
                    studentButtons.add(btn);
                }
            };
        addStudentButton.setOnMouseClicked(addStudentHandler);
        saveClass = (Button) scene.lookup("#save");
        saveClass.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                savePositions();
                saveStudents();
            }
        });

        shuffle = (Button) scene.lookup("#shuffle");
        shuffle.setOnMouseClicked(e -> {
            ArrayList<String> names = new ArrayList<>(students.keySet());
            Collections.shuffle(names);
            HashMap<Double, Button> distanceMap = new HashMap<>();
            ArrayList<Double> distances = new ArrayList<>();
            for (int i = 0; i < studentButtons.size(); i++) {
                Button studentButton = studentButtons.get(i);
                double distance = getDistanceFromBoard(studentButton);
                distanceMap.put(distance, studentButton);
                distances.add(distance);
            }
            Collections.sort(distances);
            int i = 0;
            while (i < names.size()) {
                String name = names.get(i);
                Student s = students.get(name);
                if(s.isNeedFront()){
                    distanceMap.get(distances.get(0)).setText(name);
                    distanceMap.remove(distances.get(0));
                    names.remove(name);
                    distances.remove(0);
                }else{
                    i++;
                }
            }
            ArrayList<Button> remainingButtons = new ArrayList<>(distanceMap.values());
            Collections.shuffle(remainingButtons);
            for (int x = 0; x < remainingButtons.size(); x++) {
                Button button =  remainingButtons.get(x);
                button.setText(names.get(x));
            }
        });
        print = (Button) scene.lookup("#print");
        print.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to print?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                
                addStudentButton.setVisible(false);
                saveClass.setVisible(false);
                studentInput.setVisible(false);
                shuffle.setVisible(false);
                print.setVisible(false);
                WritableImage snapshot = scene.snapshot(null);
                BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);
                PrinterJob printerJob = PrinterJob.getPrinterJob();
                printerJob.setPrintable(new Printable() {
                    @Override
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                        if(pageIndex != 0)
                            return NO_SUCH_PAGE;
                        int w = (int) Math.round(image.getWidth()*0.6);
                        int h = (int) Math.round(image.getHeight()*0.6);
                        graphics.drawImage(image, 50, 100, w, h, null);
                        return PAGE_EXISTS;
                    }
                });
                try{
                    printerJob.print();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                
                addStudentButton.setVisible(true);
                saveClass.setVisible(true);
                studentInput.setVisible(true);
                shuffle.setVisible(true);
                print.setVisible(true);
            }
        });
        double imageSize = 15;
        ImageView editImage = getIcon("/studentmanager/resources/edit.png");
        editImage.setFitHeight(imageSize);
        editImage.setFitWidth(imageSize);
        ImageView viewImage = getIcon("/studentmanager/resources/view.png");
        viewImage.setFitHeight(imageSize);
        viewImage.setFitWidth(imageSize);
        CheckMenuItem editItem = new CheckMenuItem("Edit", editImage);
        CheckMenuItem viewItem = new CheckMenuItem("View", viewImage);
        editItem.setOnAction(e -> {
            modeButton.setText("Edit");
            modeButton.setGraphic(editImage);
            editItem.setSelected(true);
            viewItem.setSelected(false);
            currentMode = Mode.EDIT;
        });
        viewItem.setOnAction(e -> {
            modeButton.setText("View");
            modeButton.setGraphic(viewImage);
            editItem.setSelected(false);
            viewItem.setSelected(true);
            currentMode = Mode.VIEW;
        });
        modeButton = (MenuButton) scene.lookup("#modeButton");
        modeButton.getItems().addAll(editItem, viewItem);
        modeButton.setText("View");
        modeButton.setGraphic(viewImage);
        viewItem.setSelected(true);
        board.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                moveVBox(board);
                updateBoardVBox();
            }
        });
    }

    public void updateStudentVBox(Student s){
        navMenu.getChildren().clear();
        titledMenu.setText(s.getName());
        navMenu.getChildren().add(makeBoldLabel("Relations:"));
        students.forEach((n, student) -> {
            if(student != s){
                HBox hBox = new HBox();
                hBox.setSpacing(5);
                Label sName = new Label(student.getName() + ":");
                sName.setPrefWidth(100);
                CheckBox fc = new CheckBox("Friend");
                fc.setSelected(student.getFriends().contains(s.getName()));
                fc.setOnMouseClicked(e -> {
                    if(e.getButton() == MouseButton.PRIMARY){
                        if(fc.isSelected() && !student.getFriends().contains(s.getName()) && !s.getFriends().contains(student.getName())){
                            s.addFriend(student.getName());
                            student.addFriend(s.getName());
                        } else if(!fc.isSelected() && student.getFriends().contains(s.getName()) && s.getFriends().contains(student.getName())){
                            student.removeFriend(s.getName());
                            s.removeFriend(student.getName());
                        }
                    }
                });
                CheckBox wc = new CheckBox("Works With");
                wc.setSelected(student.getWorkWith().contains(s.getName()));
                wc.setOnMouseClicked(e -> {
                    if(e.getButton() == MouseButton.PRIMARY){
                        if(wc.isSelected() && !student.getWorkWith().contains(s.getName()) && !s.getWorkWith().contains(student.getName())){
                            s.addWorkWith(student.getName());
                            student.addWorkWith(s.getName());
                        } else if(!wc.isSelected() && student.getWorkWith().contains(s.getName()) && s.getWorkWith().contains(student.getName())){
                            student.removeWorkWith(s.getName());
                            s.removeWorkWith(student.getName());
                        }
                    }
                });
                CheckBox dc = new CheckBox("Dislikes");
                dc.setSelected(student.getDislike().contains(s.getName()));
                dc.setOnMouseClicked(e -> {
                    if(e.getButton() == MouseButton.PRIMARY){
                        if(dc.isSelected() && !student.getDislike().contains(s.getName()) && !s.getDislike().contains(student.getName())){
                            s.addDislikes(student.getName());
                            student.addDislikes(s.getName());
                        } else if(!dc.isSelected() && student.getDislike().contains(s.getName()) && s.getDislike().contains(student.getName())){
                            student.removeDislikes(s.getName());
                            s.removeDislikes(student.getName());
                        }
                    }
                });
                hBox.getChildren().addAll(sName, fc, wc, dc);
                navMenu.getChildren().add(hBox);
            }
        });
        Line line1 = new Line();
        line1.setEndX(navMenu.getWidth()-40);
        HBox charBox1 = new HBox(makeCharBox(s, "Smart", Student.Characteristic.SMART), makeCharBox(s, "Needs Help", Student.Characteristic.NEEDSHELP));
        HBox charBox2 = new HBox(makeCharBox(s, "Talkative", Student.Characteristic.TALKATIVE), makeCharBox(s, "Shy", Student.Characteristic.SHY));
        Line line2 = new Line();
        line2.setEndX(navMenu.getWidth()-40);
        CheckBox nf = new CheckBox(("Needs Front"));
        nf.setWrapText(true);
        nf.setSelected(s.isNeedFront());
        nf.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                s.setNeedFront(nf.isSelected());
                }
            });
        nf.setPadding(new Insets(10, 0, 0, 0));
        navMenu.getChildren().addAll(line1, makeBoldLabel("Characteristics:"), charBox1, charBox2, line2, nf);
        titledMenu.setVisible(true);
        
    }

    public void updateBoardVBox(){
        navMenu.getChildren().clear();
        DecimalFormat df = new DecimalFormat("#.##");
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(360);
        slider.setMaxWidth(200);
        slider.setValue(Double.parseDouble(df.format(board.getRotate())));
        TextField textField = new TextField(Double.toString(board.getRotate()));
        textField.setPrefWidth(100);
        slider.valueProperty().addListener(observable -> {
            textField.setText(df.format(slider.getValue()));
            board.setRotate(slider.getValue());
        });
        textField.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ENTER)){
                double rotation = Double.parseDouble(textField.getText());
                slider.setValue(rotation);
                board.setRotate(rotation);
            }
        });
        HBox hBox = new HBox(slider, textField);
        hBox.setSpacing(20);
        navMenu.getChildren().addAll(hBox);
        titledMenu.setVisible(true);
        
    }

    private void moveVBox(Node node){
        if(node.getLayoutX() > scene.getWidth()/2){
            titledMenu.setLayoutX(0);
        }
        else{
            titledMenu.setLayoutX(scene.getWidth()-titledMenu.getWidth());
        }
    }

    public Button makeDesk(Button btn){
        btn.setMinWidth(side);
        btn.setMinHeight(side);
        btn.setMaxHeight(side);
        btn.setMaxWidth(side);
        btn.setLayoutX(400);
        btn.setLayoutY(300);
        btn.setWrapText(true);
        btn.setTextAlignment(TextAlignment.CENTER);
        btn.setStyle("-fx-text-fill: white; -fx-border-color: black; -fx-background-image: url('/studentmanager/resources/wood.png')");
        btn.setOnMouseClicked(e -> {
            if(!isStudentDragged && e.getButton() == MouseButton.PRIMARY){
                updateStudentVBox(students.get(btn.getText()));
                moveVBox(btn);
            }
            isStudentDragged = false;
        });
        makeStudentDraggable(btn);
        return btn;
    }

    public void saveStudents(){
        try {
            JsonArray json = new JsonArray();
            students.forEach((String name, Student student) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", student.getName());
                JsonArray friendsObject = gson.toJsonTree(student.getFriends()).getAsJsonArray();
                jsonObject.add("friends", friendsObject);
                JsonArray worksObject = gson.toJsonTree(student.getWorkWith()).getAsJsonArray();
                jsonObject.add("ww", worksObject);
                JsonArray dislikesObject = gson.toJsonTree(student.getDislike()).getAsJsonArray();
                jsonObject.add("dislikes", dislikesObject);
                JsonArray chars = gson.toJsonTree(student.getChars()).getAsJsonArray();
                jsonObject.add("chars", chars);
                jsonObject.addProperty("nf", student.isNeedFront());
                json.add(jsonObject);
            });
            PrintWriter studentPW = new PrintWriter(studentFile);
            studentPW.println(json);
            studentPW.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void savePositions(){
        try{
            PrintWriter pw = new PrintWriter(positionFile);
            for(Button btn : studentButtons){
                pw.println(btn.getText()+"☈"+btn.getLayoutX()+"☈"+btn.getLayoutY());
            }
            pw.println();
            pw.println(board.getLayoutX()+"☈"+board.getLayoutY()+"☈"+board.getRotate());
            pw.println(teacherDesk.getLayoutX()+"☈"+teacherDesk.getLayoutY()+"☈"+teacherDesk.getRotate());
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

    private Button makeStudentDraggable(Button btn){
        btn.setOnMouseDragged(e -> {
            if(currentMode == Mode.EDIT && e.getButton() == MouseButton.PRIMARY){
                titledMenu.setVisible(false);
                btn.setLayoutX(e.getSceneX() - side/2 - 10);
                btn.setLayoutY(e.getSceneY() - side/2 - 100);
                if(btn.getLayoutX() < 10 || btn.getLayoutX() > scene.getWidth()-10 || btn.getLayoutY() < 0 || btn.getLayoutY() > scene.getHeight()){
                    students.forEach((name, student) -> {
                        student.getFriends().remove(btn.getText());
                        student.getWorkWith().remove(btn.getText());
                        student.getDislike().remove(btn.getText());
                    });
                    students.remove(btn.getText());
                    studentButtons.remove(btn);
                    classroom.getChildren().remove(btn);
                }
                isStudentDragged = true;
            }
        });

        return btn;
    }

    private double getDistanceFromBoard(Button studentButton){
        return Math.sqrt(Math.pow(board.getLayoutX() - studentButton.getLayoutX(), 2) + Math.pow(board.getLayoutY() - studentButton.getLayoutY(), 2));
    }

    private double getDistanceFromTeacher(Button studentButton){
        return Math.sqrt(Math.pow(teacherDesk.getLayoutX() - studentButton.getLayoutX(), 2) + Math.pow(teacherDesk.getLayoutY() - studentButton.getLayoutY(), 2));
    }

    private CheckBox makeCharBox(Student student, String s, Student.Characteristic ch){
        CheckBox checkBox = new CheckBox(s);
        checkBox.setPrefWidth((navMenu.getWidth()-20)/2);
        checkBox.setSelected(student.getChars().contains(ch));
        checkBox.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                student.changeChar(ch, checkBox.isSelected());
            }
        });
        return checkBox;
    }

    private Text makeBoldLabel(String s){
        Label label = new Label(s);
        Text text = new Text(s);
        text.setStyle("-fx-font-weight: bold");
        return text;
    }

    private ImageView getIcon(String resourcePath) {
        InputStream input = this.getClass().getResourceAsStream(resourcePath);
        Image image = new Image(input);
        return new ImageView(image);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
