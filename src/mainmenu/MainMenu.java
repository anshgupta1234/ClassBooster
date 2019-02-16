package mainmenu;

import curver.Curver;
import flashcards.FlashCards;
import studentmanager.StudentManager;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainMenu extends Application {

    Pane flashCards;
    Pane studentManager;
    Pane curver;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        primaryStage.setTitle("Class Booster -- FlashCards Menu");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaxHeight(600);
        primaryStage.setMaxWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);
        primaryStage.show();
        flashCards = (Pane) scene.lookup("#flashCards");
        EventHandler<MouseEvent> fcHandler = event -> {
            if(event.getButton() == MouseButton.PRIMARY){
                try{
                    primaryStage.setMinWidth(720);
                    primaryStage.setMinHeight(515);
                    primaryStage.setMaxWidth(720);
                    primaryStage.setMaxHeight(515);
                    new FlashCards().start(primaryStage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        flashCards.addEventFilter(MouseEvent.MOUSE_CLICKED, fcHandler);
        studentManager = (Pane) scene.lookup("#studentManager");
        EventHandler<MouseEvent> smHandler = event -> {
            if(event.getButton() == MouseButton.PRIMARY){
                try{
                    primaryStage.setMinWidth(900);
                    primaryStage.setMinHeight(800);
                    primaryStage.setMaxWidth(900);
                    primaryStage.setMaxHeight(800);
                    new StudentManager().start(primaryStage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        studentManager.addEventFilter(MouseEvent.MOUSE_CLICKED, smHandler);
        curver = (Pane) scene.lookup("#curver");
        curver.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY){
                try{
                    primaryStage.setMinWidth(975);
                    primaryStage.setMinHeight(800);
                    primaryStage.setMaxWidth(975);
                    primaryStage.setMaxHeight(800);
                    new Curver().start(primaryStage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}
