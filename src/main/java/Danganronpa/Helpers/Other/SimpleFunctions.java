package Danganronpa.Helpers.Other;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

public final class SimpleFunctions {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFunctions.class);

    public static String rollTheDice(int dice){
        return (dice <= 0)?("0"):(Integer.toString((int) (Math.random()*dice +1)));
    }

    public static boolean fieldHasDigits(TextField field){
        return field.getText().matches("[0-9]+");
    }

    public static int getTimeInSeconds(int minutes){
        return getTimeInSeconds(minutes, 0);
    }
    public static int getTimeInSeconds(int minutes, int seconds){
        return minutes*60 + seconds;
    }
    public static String displayTime(int curTime){
        int sec = curTime%60;
        return curTime/60+":"+((sec < 10)?("0"):(""))+sec;
    }

    public static void getGithub(boolean silent){
        try{
            HttpURLConnection httpCon = (HttpURLConnection) new URL("https://api.github.com/repos/"+ Settings.GITHUB_REPO+"/releases/latest").openConnection();
            httpCon.addRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
            StringBuilder responseSB = new StringBuilder(), sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) responseSB.append("\n").append(line);
            in.close();
            Arrays.stream(responseSB.toString().split("\"tag_name\":")).skip(1).map(l -> l.split(",")[0]).forEach(sb::append);
            String version = sb.toString().split("\"")[1];
            if(!version.equals(Settings.VERSION)) {
                Alert alert = createBasicAlert(Settings.getInst().getLogo(), Alert.AlertType.CONFIRMATION, "Check for Updates",
                        "A new version (v"+version+") of the program is available for download on Github",
                        "Would you like to Update?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    openGithubLink("/releases/latest");
                    if (silent) System.exit(0);
                }
            }
            else if(!silent) createBasicAlert(Settings.getInst().getLogo(), Alert.AlertType.INFORMATION,"Check for Updates", "Your version is up to date!", "").showAndWait();
            else LOGGER.info("No new Update Available");
        } catch (IOException e) {
            LOGGER.error("Exception in 'GetGithub' Method");
        }
    }
    public static void openGithubLink(String url){
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI("https://github.com/"+ Settings.GITHUB_REPO+url));
            } catch (IOException | URISyntaxException e) {
                LOGGER.warn("Exception in 'OpenGithubLink' Method");
            }
        }
    }

    public static Alert createBasicAlert(Image image, Alert.AlertType type, String title, String header, String content){
        Alert alert = new Alert(type, content);
        alert.setTitle(title);
        alert.setHeaderText(header);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(image);
        return alert;
    }
}
