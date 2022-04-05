package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import worldMap.World;

import java.util.Objects;

import static javafx.application.Application.launch;

public class App extends Application {
    @FXML
    public StackPane pane;
    public World world;
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Home.fxml")));
            Scene scene = new Scene(root);
            //MoveScreen.moveScreen(root, stage); // move windows by clicking on the stage
            stage.setScene(scene);
            stage.setResizable(false);
            //stage.initStyle(StageStyle.UNDECORATED); // to remove windows border
            stage.setTitle("Home");
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}