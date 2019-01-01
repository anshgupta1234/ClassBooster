package flashcards;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import mainmenu.MainMenu;

import java.io.*;
import java.util.*;

public class FlashCards extends Application {

    private ArrayList<String> questions;
    private ArrayList<String> answers;
    private Label label;
    static Scene scene;
    private Pane fc;
    private Button fwd;
    private Button bwd;
    private Button shuffle;
    private Button openCard;
    private Button addCard;
    private Button deleteCard;
    private Button backButton;
    private TextArea qBox;
    private TextArea aBox;
    int num;
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("flashcards.fxml"));
        scene = new Scene(root, 722, 515);
        primaryStage.setTitle("Flashcards");
        primaryStage.setScene(scene);
        primaryStage.show();
        num = 0;
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        if(new File("QAndA.txt").exists() && !new File("QAndA.txt").toString().equals("")){
            File f = new File("QAndA.txt");
            LineNumberReader lnr = new LineNumberReader(new FileReader(f));
            String line;
            while((line = lnr.readLine()) != null){
                if(!line.isEmpty()){
                    if(lnr.getLineNumber() == 1){
                        String[] qu = line.split("☈");
                        questions.addAll(Arrays.asList(qu));
                    }
                    else if(lnr.getLineNumber() == 2){
                        String[] ans = line.split("☈");
                        answers.addAll(Arrays.asList(ans));
                    }
                }
            }
        }
        else{
            File f = new File("QAndA.txt");
        }
        label = (Label) scene.lookup("#txt");
        if(questions.size() != 0)
            label.setText(questions.get(0));
        fc = (Pane) scene.lookup("#card");
        EventHandler<MouseEvent> cardHandler = e -> {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), fc);
            rotateTransition.setByAngle(360);
            rotateTransition.setAutoReverse(false);
            rotateTransition.setCycleCount(1);
            rotateTransition.setAxis(new Point3D(5, 0, 0));
            rotateTransition.play();
            if(questions.contains(label.getText())){
                label.setText(answers.get(questions.indexOf(label.getText())));
            }
            else if(answers.contains(label.getText())){
                label.setText(questions.get(answers.indexOf(label.getText())));
            }
        };
        label.addEventFilter(MouseEvent.MOUSE_CLICKED, cardHandler);
        fwd = (Button) scene.lookup("#fwd");
        EventHandler<MouseEvent> fwdHandler = e -> {
            if(questions.size() != 0){
                if(num != questions.size()-1){
                    num++;
                    label.setText(questions.get(num));
                }
                else{
                    num = 0;
                    label.setText(questions.get(num));
                }
            }
        };
        fwd.addEventFilter(MouseEvent.MOUSE_CLICKED, fwdHandler);
        bwd = (Button) scene.lookup("#bwd");
        EventHandler<MouseEvent> bwdHandler = e -> {
            if(questions.size() != 0){
                if(num != 0){
                    num--;
                    label.setText(questions.get(num));
                }
                else{
                    num = questions.size()-1;
                    label.setText(questions.get(num));
                }
            }
        };
        bwd.addEventFilter(MouseEvent.MOUSE_CLICKED, bwdHandler);
        shuffle = (Button) scene.lookup("#shuffle");
        EventHandler<MouseEvent> shuffleHandler = e -> {
            HashMap<String, String> temp = new HashMap<>();
            for(int i = 0; i < questions.size(); i++){
                temp.put(questions.get(i), answers.get(i));
            }
            Collections.shuffle(questions);
            for(int j = 0; j < questions.size(); j++){
                answers.set(j, temp.get(questions.get(j)));
            }
            label.setText(questions.get(0));
        };
        shuffle.addEventFilter(MouseEvent.MOUSE_CLICKED, shuffleHandler);
        openCard = (Button) scene.lookup("#openCard");
        EventHandler<MouseEvent> openCardHandler = e -> {
                try{
                    Stage stage = new Stage();
                    stage.setX(710);
                    stage.setY(400);
                    stage.setTitle("Add A Card");
                    Parent addRoot = FXMLLoader.load(getClass().getResource("addcard.fxml"));
                    Scene cardScene = new Scene(addRoot, 710, 400);
                    stage.setScene(cardScene);
                    stage.show();
                    qBox = (TextArea) cardScene.lookup("#questionBox");
                    aBox = (TextArea) cardScene.lookup("#answerBox");
                    addCard = (Button) cardScene.lookup("#addCard");
                    EventHandler<MouseEvent> addCardHandler = event -> {
                        addFC();
                    };
                    addCard.addEventFilter(MouseEvent.MOUSE_CLICKED, addCardHandler);
                 }
                catch (IOException ex){
                    ex.printStackTrace();
                }
        };
        openCard.addEventFilter(MouseEvent.MOUSE_CLICKED, openCardHandler);
        deleteCard = (Button) scene.lookup("#deleteCard");
        EventHandler<MouseEvent> deleteCardHandler = event -> {
            questions.remove(num);
            answers.remove(num);
            updateFiles();
            try{
                label.setText(questions.get(num));
            }catch (Exception e){
                num = 0;
                try{
                    label.setText(questions.get(num));
                }catch (Exception ex){
                    label.setText("Please add a flashcard to get started.");
                }
            }
        };
        deleteCard.addEventFilter(MouseEvent.MOUSE_CLICKED, deleteCardHandler);
        backButton = (Button) scene.lookup("#back");
        backButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            try{
                new MainMenu().start(primaryStage);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void updateFiles(){
        try{
            String q = getAsString(questions);
            String a = getAsString(answers);
            File f = new File("QAndA.txt");
            PrintWriter pw = new PrintWriter(f);
            pw.println(q);
            pw.println(a);
            pw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getAsString(ArrayList<String> arrayList){
        String temp = "";
        for(String s: arrayList){
            temp += s + "☈";
        }
        return temp;
    }

    public void addFC(){
        if(!(qBox.getText().isEmpty() || aBox.getText().isEmpty())){
            String q = qBox.getText();
            String a = aBox.getText();
            questions.add(q);
            answers.add(a);
            if(questions.size() == 1){
                label.setText(questions.get(num));
            }
            qBox.setText("");
            aBox.setText("");
            updateFiles();
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
