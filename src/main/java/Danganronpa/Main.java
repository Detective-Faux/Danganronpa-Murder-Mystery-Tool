package Danganronpa;

import Danganronpa.Helpers.Other.BotInfo;
import Danganronpa.Helpers.Other.Settings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    @Override
    public void start(Stage primaryStage) throws IOException{
        LOGGER.info("Starting Initialization Version {}", BotInfo.VERSION);
        Scene mainScene = new Scene(new FXMLLoader(Main.class.getResource("/Info/display.fxml")).load(), 800, 600);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Info/style.css")).toString());
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Danganronpa Murder Mystery Tool");
        primaryStage.getIcons().add(Settings.getInst().getLogo());
        LOGGER.info("Displaying Application");
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