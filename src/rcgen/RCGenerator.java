package rcgen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;

public class RCGenerator extends Application {

    private Scene scene;
    private AnchorPane mainPane;
    private AutoCompleteTextField searchStudent;
    private ArrayList<String> posTraits, negTraits;
    private Pane posPane;
    private Pane negPane;
    private Button generateButton;
    private TextArea result;
    private RadioButton maleChoice;
    private RadioButton femaleChoice;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("RCGenerator.fxml"));
        scene = new Scene(root, 900, 750);
        primaryStage.setTitle("Report Card Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
        posTraits = new ArrayList<>();
        negTraits = new ArrayList<>();
        mainPane = (AnchorPane) scene.lookup("#mainPane");
        Text text = new Text("Enter a Student: ");
        searchStudent = new AutoCompleteTextField();
        HBox hBox = new HBox(text, searchStudent);
        hBox.setLayoutX(25);
        hBox.setLayoutY(60);
        hBox.setSpacing(10);
        mainPane.getChildren().addAll(hBox);
        posPane = (Pane) scene.lookup("#posPane");
        negPane = (Pane) scene.lookup("#negPane");
        posPane.getChildren().forEach(node -> {
            if(node instanceof Label){
                Label trait = (Label) node;
                highlightLabel(trait, posTraits);
            }
            else if(node instanceof Button){
                Button b = (Button) node;
                addTrait(b, posPane, posTraits);
            }
        });
        negPane.getChildren().forEach(node -> {
            if(node instanceof Label){
                Label trait = (Label) node;
                highlightLabel(trait, negTraits);
            }
            else if(node instanceof Button){
                Button b = (Button) node;
                addTrait(b, negPane, negTraits);
            }
        });
        result = (TextArea) scene.lookup("#result");
        maleChoice = (RadioButton) scene.lookup("#maleChoice");
        maleChoice.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                femaleChoice.setSelected(false);
                maleChoice.setSelected(true);
            }
        });
        femaleChoice = (RadioButton) scene.lookup("#femaleChoice");
        femaleChoice.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                maleChoice.setSelected(false);
                femaleChoice.setSelected(true);
            }
        });
        generateButton = (Button) scene.lookup("#generate");
        generateButton.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                String gender = maleChoice.isSelected() ? "He" : "She";
                String name = String.format("%s is a wonderful student. %s is %s. However, %s is still %s. Other than that, %s is a great joy to have in the class.",
                searchStudent.getText(), gender, makeTraitList(posTraits), gender.toLowerCase(), makeTraitList(negTraits), gender.toLowerCase());
                result.setText(name);
            }
        });
    }

    private void highlightLabel(Label trait, ArrayList<String> toAdd) {
        trait.setOnMouseClicked(e -> {
            if (trait.getStyle().contains("blue")) {
                trait.setStyle("-fx-border-color:white");
                toAdd.remove(trait.getText());
            } else {
                trait.setStyle("-fx-border-color:blue");
                toAdd.add(trait.getText());
            }
        });
    }
    private void addTrait(Button b, Pane p, ArrayList<String> toAdd){
        b.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                TextField textField = new TextField();
                textField.setPrefWidth(100);
                textField.setPrefHeight(24);
                textField.setLayoutX(b.getLayoutX());
                textField.setLayoutY(b.getLayoutY());
                p.getChildren().remove(b);
                p.getChildren().add(textField);
                textField.setOnKeyPressed(event -> {
                    if(event.getCode() == KeyCode.ENTER){
                        Label label = new Label();
                        label.setPrefWidth(100);
                        label.setLayoutX(textField.getLayoutX());
                        label.setLayoutY(textField.getLayoutY());
                        label.setText(textField.getText());
                        label.setWrapText(true);
                        if(label.getText().length() > 21) {
                            label.setFont(Font.font("Avenir", 11.5));
                            label.setPrefHeight(35);
                        }
                        else if(label.getText().length() > 17) {
                            label.setFont(Font.font("Avenir", 13));
                            label.setPrefHeight(42);
                        }
                        else
                            label.setFont(Font.font("Avenir", 16));
                        label.setAlignment(Pos.CENTER);
                        label.setTextAlignment(TextAlignment.CENTER);
                        highlightLabel(label, toAdd);
                        p.getChildren().remove(textField);
                        p.getChildren().add(label);
                    }
                });
            }
        });
    }

    private String makeTraitList(ArrayList<String> traits){
        if(traits.size() == 0)
            return "";
        if(traits.size() == 1)
            return traits.get(0).toLowerCase();
        String toReturn = "";
        for (int i = 0; i < traits.size()-2; i++) {
            String s =  traits.get(i).toLowerCase();
            toReturn += s + ", ";
        }
        toReturn += traits.get(traits.size()-2).toLowerCase() + " and " + traits.get(traits.size()-1).toLowerCase();
        return toReturn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
