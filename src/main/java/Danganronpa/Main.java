package Danganronpa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException{
        System.out.println("Starting Initialization");
        Scene mainScene = new Scene(new FXMLLoader(Main.class.getResource("/Info/display.fxml")).load(), 800, 600);
        mainScene.getStylesheets().add(getClass().getResource("/Info/style.css").toString());
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Danganronpa Murder Mystery Tool");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Media/logo.png")));
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.exit(0);
    }

    public static void main(String[] args){
        launch(args);
    }
}