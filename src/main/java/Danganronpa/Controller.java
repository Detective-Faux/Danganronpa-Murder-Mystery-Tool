package Danganronpa;

import Danganronpa.Helpers.Discord.Capsule;
import Danganronpa.Helpers.GameItems.*;
import Danganronpa.Helpers.Info.SelfCredentials;
import Danganronpa.Helpers.Info.Spreadsheet;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Member;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Controller implements Initializable {
    //--Global Items--//
    public static final ArrayList<Tag> TAGS = new ArrayList<>();
    private static final ArrayList<GameMode> GAME_MODES = new ArrayList<>();
    private static final ArrayList<Player> PLAYERS = new ArrayList<>();
    private static final ArrayList<String> RESOURCES = new ArrayList<>();
    private static final ArrayList<Role> ROLES = new ArrayList<>();
    private static final String[] CONFIG_FIELDS = new String[]{"guildID","sheetID","discordToken","playersRange","rolesRange","tagsRange","gameModesRange","prefix","hierarchy"},
            CONFIG_DEFAULTS = new String[]{"","","","Players!A2:P","Roles!A2:C","Tags!A2:C","Game Modes!A2:Z","m&","Investigative,Evidence Tampering,Chaos,Miscellaneous,Protector,Investigative,Neutral Evil,Miscellaneous,Chaos,Miscellaneous,Chain"};
    private static final String GITHUB_URL = "Shadow-Spade/Danganronpa-Murder-Mystery-Tool/releases/latest";
    private static SelfCredentials sc;
    private static Sheets service;
    private static JDA jda;
    private ArrayList<TextField> customGameField = new ArrayList<>(), playerWinsFields = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private Timeline timer = new Timeline();
    private Student toUpdate = null;
    private Media gong;
    private int curTime;

    //--Screen Items--//
    public TextField discordNameTextField, effectiveNameTextField, lossesTextField,
            killsTextField, performanceTextField, leftTextField,
            customTimeField, customRollField, standardGameField;
    public VBox customGameVBox;
    public HBox winsHBox;
    public FlowPane rngFlow, timeFlow;
    public Button confirmButton;
    public ToggleButton playToggle;
    public Tab scoreUpdaterTab;
    public ProgressBar dmProgress;
    public ContextMenu editRolesMenu;
    public MenuItem removeRoleMenuItem, updateBar;
    public Menu addRoleMenu, tagMenu;
    public TreeView<String> roleTree;
    public TreeView<Player> playerTree, playingTree, modsTree;
    public TreeView<Student> studentTreeView;
    public ComboBox<GameMode> gamemodeBox;
    public Label rollLabel, statusLabel, timerLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT;
        try {
            System.out.println("Checking for Updates...");
            updateBar.setOnAction(e -> getGithub(false));
            getGithub(true);
            System.out.println("Checking for config.properties file");
            File f = new File("./config.properties");
            Properties config = new Properties();
            if(f.createNewFile()){
                System.out.println("config.properties file not found...\nCreating new config.properties file");
                for(int x = 0; x < CONFIG_FIELDS.length; x++) config.setProperty(CONFIG_FIELDS[x], CONFIG_DEFAULTS[x]);
                while (config.getProperty(CONFIG_FIELDS[0],CONFIG_DEFAULTS[0]).equals("") || config.getProperty(CONFIG_FIELDS[1],CONFIG_DEFAULTS[1]).equals("") || config.getProperty(CONFIG_FIELDS[2],CONFIG_DEFAULTS[2]).equals("")){
                    quarryInfoAlert(config);
                }
                config.store(new FileOutputStream(f),null);
            }
            else {
                System.out.println("config.properties file found!");
                config.load(new FileInputStream(f));
                for(int x = 0; x < CONFIG_FIELDS.length; x++){
                    if(config.getProperty(CONFIG_FIELDS[x]) == null) config.setProperty(CONFIG_FIELDS[x], CONFIG_DEFAULTS[x]);
                }
                while (config.getProperty(CONFIG_FIELDS[0],CONFIG_DEFAULTS[0]).equals("") || config.getProperty(CONFIG_FIELDS[1],CONFIG_DEFAULTS[1]).equals("") || config.getProperty(CONFIG_FIELDS[2],CONFIG_DEFAULTS[2]).equals("")) {
                    System.out.println("config.properties file is missing critical information!!");
                    quarryInfoAlert(config);
                    config.store(new FileOutputStream(f), null);
                }
            }
            sc = new SelfCredentials(config);
            System.out.println();

            //Get Credentials from file
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Sheets.Builder(HTTP_TRANSPORT, Spreadsheet.JSON_FACTORY, Spreadsheet.getCredentials(HTTP_TRANSPORT)).setApplicationName("Danganronpa Client Tool").build();
            //--List Building--//
            System.out.println("\nLoading Roles");
            buildList(sc.getRolesRange());
            System.out.println("Loading Players");
            buildList(sc.getPlayerRange());
            System.out.println("Loading Tags");
            buildList(sc.getTagsRange());
            System.out.println("Loading Game Modes");
            buildList(sc.getGameModeRange());
            //Setup the bot
            System.out.println("Booting Discord...\n");
            jda = new JDABuilder(AccountType.BOT).setToken(sc.getDiscordToken()).setBulkDeleteSplittingEnabled(false)
                    .setAutoReconnect(true).addEventListener(new Capsule(sc.getGuildID(),sc.getPrefix())).build();
            //Audio
            gong = new Media(Main.class.getResource("/Media/timer.wav").toURI().toString());
        } catch (GeneralSecurityException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        //Other initialisations
        gamemodeBox.getItems().addAll(GAME_MODES);
        makePlayerTree(playerTree);
        roleTree.setRoot(new TreeItem<>());
        playingTree.setRoot(new TreeItem<>());
        modsTree.setRoot(new TreeItem<>());
        studentTreeView.setRoot(new TreeItem<>());
        //Dynamic Custom game field updates & add role Menus
        System.out.println("\nAdding Custom Fields and Updating Menus");
        for(String item: RESOURCES){
            //Custom game Fields
            TextField addable = new TextField();
            addable.setPromptText("# "+item+((item.equals("Chain"))?(" Pairs"):("")));
            addable.setFont(new Font(15));
            addable.setAlignment(Pos.CENTER);
            VBox.setMargin(addable, new Insets(2,10,2,0));
            customGameField.add(addable);
            customGameVBox.getChildren().add(customGameVBox.getChildren().size()-2,addable);
            //Role Menus
            Menu add = new Menu(item);
            add.setMnemonicParsing(false);
            ArrayList<Role> roles = Objects.requireNonNull(getSubArray(item, ROLES, false));
            for(int x = 0; x < roles.size(); x++){
                int cur = x;
                MenuItem role = new MenuItem(roles.get(x).toString());
                role.setMnemonicParsing(false);
                if(item.equals("Chain")) role.setOnAction(event -> menuAddRole(roles.get(cur), roles.get((cur%2 == 0)?(cur+1):(cur-1))));
                else role.setOnAction(event -> menuAddRole(roles.get(cur)));
                add.getItems().add(role);
            }
            addRoleMenu.getItems().add(add);
        }
        //"Dynamic" Score Updating Fields
        System.out.println("Fixing Scores Tab");
        for(int x = 0; x < Player.WIN_TYPES.length; x++){
            TextField addable = new TextField();
            addable.setPromptText("# "+Player.WIN_TYPES[x]);
            addable.setAlignment(Pos.CENTER);
            HBox.setMargin(addable, new Insets(0,5,0,5));
            playerWinsFields.add(addable);
            winsHBox.getChildren().add(addable);
        }
        //Update add tag Menu
        for(Tag item: TAGS){
            MenuItem add = new MenuItem(item.toString());
            add.setOnAction(event -> menuAddTag(item));
            tagMenu.getItems().add(add);
        }
        //Repeating Buttons
        System.out.println("Building Buttons");
        buildButtons("RNG", rngFlow, 4,6,8,10,12,20);
        buildButtons("TIME", timeFlow, 1,2,4,6,8,10);
        //Clear tag
        MenuItem add = new MenuItem("Clear");
        add.setOnAction(event -> menuAddTag(new Tag()));
        tagMenu.getItems().add(add);
        statusUpdate();
        System.out.println("Finished Initialisation\n");
    }
    private void quarryInfoAlert(Properties config) {
        // Create the custom dialog.
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("First Run Setup");
        dialog.setHeaderText("Please enter the following credentials for the program to run");
        dialog.initModality(Modality.APPLICATION_MODAL);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/Media/logo.png").toString()));

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField serverID = new TextField(), sheetID = new TextField();
        serverID.setPromptText("guildID");
        serverID.setText(config.getProperty("guildID"));
        sheetID.setPromptText("sheetID");
        sheetID.setText(config.getProperty("sheetID"));
        PasswordField botID = new PasswordField();
        botID.setPromptText("discordToken");
        botID.setText(config.getProperty("discordToken"));

        grid.add(new Label("Google Sheet ID:"), 0, 0);
        grid.add(sheetID, 1, 0);
        grid.add(new Label("Discord Server ID:"),0,1);
        grid.add(serverID, 1, 1);
        grid.add(new Label("Bot Token:"), 0, 2);
        grid.add(botID, 1, 2);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        serverID.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(serverID::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> ((dialogButton == loginButtonType)?(new ArrayList<>(Arrays.asList(serverID.getText(), sheetID.getText(), botID.getText()))):(null)));

        dialog.showAndWait().ifPresent(ret -> {
            config.setProperty("guildID",ret.get(0));
            config.setProperty("sheetID",ret.get(1));
            config.setProperty("discordToken",ret.get(2));
        });
    }

    //--Update Spreadsheet--//
    public static void update(Member m){
        Player s = new Player(m);
        for(Player plr : PLAYERS) if(plr.getID().equals(s.getID())) return;
        PLAYERS.add(s);
        updateSheet(s);
    }
    public static void checkNewUsers(String guild_id){
        ArrayList<Player> temp = new ArrayList<>();
        boolean found = false;
        for(Member m: jda.getGuildById(guild_id).getMembers()){
            if(m.getUser().isBot()) continue;
            for(Player s: PLAYERS) if(s.getID().equals(m.getUser().getId())) found = true;
            if(!found) temp.add(new Player(m));
            found = false;
        }
        if(temp.isEmpty()) return;
        PLAYERS.addAll(temp);
        updateSheet(temp.toArray(new Player[0]));
    }
    private static void updateSheet(Player...newPlayers){
        try{
            List<List<Object>> sheet = service.spreadsheets().values().get(sc.getSheetID(), sc.getPlayerRange()).execute().getValues(), toAppend = new ArrayList<>();
            for(Player s: newPlayers) toAppend.add(addPlayer(s));
            List<ValueRange> data = new ArrayList<ValueRange>() {{ add(new ValueRange().setRange("Players!A" + (2 + sheet.size()) + ":P").setValues(toAppend));}};
            BatchUpdateValuesRequest body = new BatchUpdateValuesRequest().setValueInputOption("USER_ENTERED").setData(data);
            BatchUpdateValuesResponse result = service.spreadsheets().values().batchUpdate(sc.getSheetID(), body).execute();
            System.out.printf("\n(%d cells updated.) %d new users joined the killing game!\n", result.getTotalUpdatedCells(), toAppend.size());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //--Game Stuff--//
    public void startGame(){
        StringBuilder sb = new StringBuilder(), fileOut = new StringBuilder();
        ArrayList<TreeItem<Player>> playing = new ArrayList<>(playingTree.getRoot().getChildren());
        Collections.shuffle(playing);
        //Guaranteed that both are same length
        for(int x = 0; x < students.size(); x++) students.get(x).setPlayer(playing.get(x).getValue());
        for(Student s: students){
            sb.setLength(0);
            sb.append("**The Ultimate ").append(s.getRole()).append("**:\n").append(s.getRole().getDescription()).append("\n\n");
            for(Tag t: s.getTags()) {
                if(!t.isEmpty()) sb.append("**").append(t.getName()).append("**:\n").append(t.getDescription()).append("\n\n");
            }
            sb.append("*Questions and Answers*:\nIf you have any questions about your role, please come ask one of your moderators!");
            fileOut.append(s.toString()).append("\r\n");
            sendDM(s.getID(), sb.toString());
        }

        //Sends selected mods the list of those playing with their roles
        for(TreeItem<Player> tis: modsTree.getRoot().getChildren()) sendDM(tis.getValue().getID(), "```\n"+fileOut.toString()+"```");

        //generates file with all player's roles as backup
        try{
            String[] headerText = new String[]{"Here?","Player Name","Tag","Role","Extra Notes","","Day 1","Day 2","Day 3","Day 4","Day 5","Day 6","Day 7","Day 8"};
            FileOutputStream output = new FileOutputStream(new File("./Game.xls").getAbsoluteFile());
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Game ("+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM, HH-mm"))+")");

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont(), bodyFont = workbook.createFont();
            headerFont.setFontName("TimesRoman");
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short)14);
            bodyFont.setFontName("TimesRoman");
            bodyFont.setFontHeightInPoints((short)12);

            CellStyle headStyle = workbook.createCellStyle(), bodyStyle = workbook.createCellStyle();
            headStyle.setFont(headerFont);
            bodyStyle.setFont(bodyFont);

            HSSFRow rowHead = sheet.createRow(0);
            for(int x = 0; x < headerText.length; x++) {
                Cell created = rowHead.createCell(x);
                created.setCellValue(headerText[x]);
                created.setCellStyle(headStyle);
            }

            for(int x = 0; x < students.size(); x++){
                HSSFRow row = sheet.createRow(x+1);
                String[] data = new String[]{
                        "✓",
                        students.get(x).getPlayer().toString(),
                        students.get(x).getFullTagsAsString(),
                        students.get(x).getRole().toString()
                };
                for(int y = 0; y < data.length;  y++){
                    Cell created = row.createCell(y);
                    created.setCellValue(data[y]);
                    created.setCellStyle(bodyStyle);
                }
            }
            for(int i = 0; i < headerText.length; i++) sheet.autoSizeColumn(i);

            workbook.write(output);
            output.close();
            workbook.close();
            System.out.println("\nSpreadsheet Created\n");
        } catch(IOException e) {
            e.printStackTrace();
        }
        confirmButton.setDisable(true);
        scoreUpdaterTab.setDisable(false);
        TreeItem<Student> root = new TreeItem<>();
        root.setExpanded(true);
        for(Student s: students) root.getChildren().add(new TreeItem<>(s));
        studentTreeView.setRoot(root);
    }
    public void selectPlayer(){
        toUpdate = studentTreeView.getSelectionModel().getSelectedItems().get(0).getValue();
        discordNameTextField.setText(toUpdate.getPlayer().getName());
        effectiveNameTextField.setText(toUpdate.getPlayer().getEffectiveName());
        lossesTextField.setText(""+toUpdate.getPlayer().getLosses());
        killsTextField.setText(""+toUpdate.getPlayer().getKills());
        performanceTextField.setText(""+toUpdate.getPlayer().getPerformance());
        leftTextField.setText(""+toUpdate.getPlayer().getLeft());
        for(int x = 0; x < toUpdate.getPlayer().getWins().length; x++) playerWinsFields.get(x).setText(""+toUpdate.getPlayer().getSingleWin(x));
    }
    public void updatePlayer(){
        if(toUpdate == null) return;
        toUpdate.getPlayer().setEffectiveName(effectiveNameTextField.getText());
        toUpdate.getPlayer().setLosses(Integer.parseInt(lossesTextField.getText()));
        toUpdate.getPlayer().setKills(Integer.parseInt(killsTextField.getText()));
        toUpdate.getPlayer().setPerformance(Integer.parseInt(performanceTextField.getText()));
        toUpdate.getPlayer().setLeft(Integer.parseInt(leftTextField.getText()));
        int[] newWins = new int[playerWinsFields.size()];
        for(int x = 0; x < newWins.length; x++) newWins[x] = Integer.parseInt(playerWinsFields.get(x).getText());
        toUpdate.getPlayer().setWins(newWins);
        updatePlayerInSheet(toUpdate.getPlayer());
    }
    private void updatePlayerInSheet(Player s){
        try{
            List<List<Object>> sheet = service.spreadsheets().values().get(sc.getSheetID(), sc.getPlayerRange()).execute().getValues(), toAppend = new ArrayList<>();
            int index = 0;
            for(int x = 0; x < sheet.size(); x++){
                if(sheet.get(x).get(0).toString().equals(s.getID())){
                    index = x;
                    break;
                }
            }
            toAppend.add(addPlayer(s));
            final String range = "Players!A"+(2+index)+":P"+(2+index);
            ValueRange data = new ValueRange().setValues(toAppend);
            UpdateValuesResponse result = service.spreadsheets().values().update(sc.getSheetID(), range, data).setValueInputOption("USER_ENTERED").execute();
            System.out.printf("\n(%d cells updated.) a user's scores were updated successfully!\n", result.getUpdatedCells());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //--Dice Rolls--//
    public void customRNG(){
        if(fieldHasDigits(customRollField) && customRollField.getText().length() < 1) return;
        preRNG(Integer.parseInt(customRollField.getText()));
    }
    private void preRNG(int x){
        rollLabel.setText((x <= 0)?("0"):(Integer.toString((int) (Math.random()*x +1))));
    }

    //--Countdown Timers--//
    public void customTimer(){
        if(fieldHasDigits(customTimeField)) startTimer(time(0, Integer.parseInt(customTimeField.getText())));
        else if(customTimeField.getText().matches("[0-9]+([:])[0-9]+")) {
            String[] items = customTimeField.getText().split(":");
            startTimer(time(Integer.parseInt(items[0]), Integer.parseInt(items[1])));
        }
    }
    public void stopTimer(){
        timer.stop();
        curTime = 0;
        timerLabel.setText(displayTime());
        pauseResumeTimer();
    }
    public void pauseResumeTimer(){
        if(timer.getStatus() == Animation.Status.STOPPED && curTime == 0) playToggle.setSelected(false);
        playToggle.setText((playToggle.isSelected())?("\u23F8"):("▶"));
        if(playToggle.isSelected()) timer.play();
        else timer.pause();
    }
    private void startTimer(int timeSec){
        if(playToggle.isSelected()) playToggle.setSelected(false);
        curTime = timeSec;
        if(timer.getStatus() == Animation.Status.RUNNING) timer.stop();
        MediaPlayer timeEnd = new MediaPlayer(gong);
        timeEnd.setVolume(0.5);
        timerLabel.setText(displayTime());
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if(curTime-- == 1) timeEnd.play();
            timerLabel.setText(displayTime());
        }));
        timer.setCycleCount(timeSec);
    }
    private int time(int minutes){
        return time(minutes, 0);
    }
    private int time(int minutes, int seconds){
        return minutes*60 + seconds;
    }
    private String displayTime(){
        int sec = curTime%60;
        return curTime/60+":"+((sec < 10)?("0"):(""))+sec;
    }

    //--Role Generation--//
    public void customGame(){
        ArrayList<String> heir = new ArrayList<>();
        for(int x = 0; x < customGameField.size(); x++){
            if(!fieldHasDigits(customGameField.get(x))) continue;
            ArrayList<Role> size = getSubArray(RESOURCES.get(x), ROLES,false);
            if(size == null) continue;
            int len = Integer.parseInt(customGameField.get(x).getText())*((x == 0)?(2):(1));
            if(len > size.size()){
                len = size.size();
                customGameField.get(x).setText(""+size.size());
            }
            for(int y = 0; y < len; y++) heir.add(RESOURCES.get(x));
        }
        startGenerate(heir.size(), heir);
    }
    public void standardGame(){
        int len = 12;
        if(fieldHasDigits(standardGameField)) len = Integer.parseInt(standardGameField.getText());
        else standardGameField.setText(""+len);
        startGenerate(len, new ArrayList<>(Arrays.asList(sc.getHierarchy())));
    }
    public void clearRoles(){
        roleTree.getRoot().getChildren().clear();
        students.clear();
        statusUpdate();
    }
    public void menuRequest(ContextMenuEvent event){
        editRolesMenu.show(roleTree, event.getScreenX(), event.getScreenY());
        removeRoleMenuItem.setDisable(roleTree.getSelectionModel().isEmpty());
        tagMenu.setDisable(roleTree.getSelectionModel().isEmpty());
        event.consume();
    }
    public void menuRemoveRole(){
        students.remove(roleTree.getRoot().getChildren().indexOf(roleTree.getSelectionModel().getSelectedItem()));
        roleTree.getRoot().getChildren().remove(roleTree.getSelectionModel().getSelectedItem());
        statusUpdate();
    }
    private void startGenerate(int len, ArrayList<String> hierarchy){
        if(gamemodeBox.getValue() == null) gamemodeBox.setValue(GAME_MODES.get(0));
        if(len < gamemodeBox.getValue().getCalculatedTagsSize(len)) return;
        if(len > 0 && len <= ROLES.size()) generateGame(hierarchy, len, gamemodeBox.getValue().performActions(len));
    }
    private void generateGame(ArrayList<String> heir, int n, ArrayList<Tag> tags){
        students.clear();
        ArrayList<Role> roleSelection = new ArrayList<>(), tempHolder = freshCopy();
        for(int ran, x = 0; x < n; x++) {
            ArrayList<Role> sub;
            String type = heir.get(x%heir.size());
            //Sub Array of tempHolder to get the list withing the category
            if(type.equals("Chain") && n - x <=1){
                type = "Random";
                sub = getSubArray("Chain", tempHolder, true);
            }
            else sub = getSubArray(type , tempHolder, false);
            if(sub == null) continue;
            //Add to list
            ran = (int)(Math.random()*sub.size());
            addRole(roleSelection, tempHolder, sub.get(ran));
            //case for chain
            if(type.equals("Chain")){
                addRole(roleSelection, tempHolder, sub.get((ran%2 == 0)?(ran+1):(ran-1)));
                x++;
            }
        }
        //Create Students
        Collections.shuffle(tags);
        for(int x = 0; x < n; x++){
            if(tags.get(x).getName().equals("")) students.add(new Student(roleSelection.get(x)));
            else students.add(new Student(roleSelection.get(x),tags.get(x)));
        }

        //Generate Tree
        TreeItem<String> root = new TreeItem<>();
        for(Student s : students) {
            TreeItem<String> temp = makeBranch(s.getTagsAsString()+s.getRole() ,root);
            makeBranch(s.getRole().getDescription(), temp);
        }
        root.setExpanded(true);
        roleTree.setRoot(root);
        roleTree.setCellFactory(item -> {
            TreeCell<String> treeCell = new TreeCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) setText(item);
                    else setText("");
                }
            };
            treeCell.prefWidthProperty().bind(roleTree.widthProperty().subtract(5.0));
            return treeCell;
        });
        statusUpdate();
    }
    private TreeItem<String> makeBranch(String title, TreeItem<String> parent){
        TreeItem<String> temp = new TreeItem<>(title);
        parent.getChildren().add(temp);
        return temp;
    }
    private void menuAddRole(Role...roles){
        for(Role r: roles){
            TreeItem<String> temp = makeBranch(r.toString(), roleTree.getRoot());
            makeBranch(r.getDescription(),temp);
            students.add(new Student(r));
        }
        if(gamemodeBox.getValue() == null || !gamemodeBox.getValue().getName().equals("Custom")) gamemodeBox.setValue(gamemodeBox.getItems().get(gamemodeBox.getItems().size()-1));
        statusUpdate();
    }
    private void menuAddTag(Tag t){
        Student s = students.get(roleTree.getRoot().getChildren().indexOf(roleTree.getSelectionModel().getSelectedItem()));
        s.addTag(t);
        roleTree.getSelectionModel().getSelectedItem().setValue(s.getTagsAsString()+s.getRole());
        statusUpdate();
    }

    //--Player Building--//
    public void refreshPlayers(){
        PLAYERS.sort(Comparator.comparing(t -> t.getEffectiveName().toLowerCase()));
        TreeItem<Player> newRoot = new TreeItem<>();
        for(Player s: PLAYERS) newRoot.getChildren().add(new TreeItem<>(s));
        for(int x = 0; x < newRoot.getChildren().size(); x++){
            for(TreeItem<Player> s: playingTree.getRoot().getChildren()){
                if(newRoot.getChildren().get(x).getValue().getID().equals(s.getValue().getID())){
                    newRoot.getChildren().remove(x);
                    x -= 1;
                    break;
                }
            }
        }
        newRoot.setExpanded(true);
        playerTree.setRoot(newRoot);
        statusUpdate();
    }
    public void pushToPlaying(){
        fromOneToAnother(playerTree, playingTree);
    }
    public void removeFromPlaying(){
        fromOneToAnother(playingTree, playerTree);
    }
    public void addModerators(){
        fromOneToAnother(playerTree, modsTree);
    }
    public void removeMods(){
        fromOneToAnother(modsTree, playerTree);
    }
    public void popCreated(){
        //Push back
        playerTree.getRoot().getChildren().addAll(playingTree.getRoot().getChildren());
        playerTree.getRoot().getChildren().addAll(modsTree.getRoot().getChildren());
        //Pop remaining
        playingTree.getRoot().getChildren().clear();
        modsTree.getRoot().getChildren().clear();
        //Sort list and update
        refreshPlayers();
        statusUpdate();
    }
    private void fromOneToAnother(TreeView<Player> from, TreeView<Player> to){
        ObservableList<TreeItem<Player>> temp = from.getSelectionModel().getSelectedItems();
        if(!temp.isEmpty() && !from.getRoot().getChildren().isEmpty()) {
            to.getRoot().getChildren().addAll(temp);
            from.getRoot().getChildren().removeAll(temp);
        }
        treeViewSort(from);
        treeViewSort(to);
        statusUpdate();
    }
    private void treeViewSort(TreeView<Player> tree){
        tree.getRoot().getChildren().sort(Comparator.comparing(t -> t.getValue().getEffectiveName().toLowerCase()));
    }
    private void makePlayerTree(TreeView<Player> tree){
        PLAYERS.sort(Comparator.comparing(t -> t.getEffectiveName().toLowerCase()));
        TreeItem<Player> root = new TreeItem<>();
        for(Player s: PLAYERS) root.getChildren().add(new TreeItem<>(s));
        root.setExpanded(true);
        tree.setRoot(root);
        tree.setCellFactory(item -> {
            TreeCell<Player> treeCell = new TreeCell<Player>() {
                @Override
                protected void updateItem(Player item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) setText(item.toString());
                    else setText("");
                }
            };
            treeCell.prefWidthProperty().bind(tree.widthProperty().subtract(5.0));
            return treeCell;
        });
    }
    private void statusUpdate(){
        double stat = 0;
        String text = "Status: ";
        if(roleTree.getRoot().getChildren().isEmpty()) text += "(R?) ";
        else stat += 0.35;
        if(playingTree.getRoot().getChildren().isEmpty()) text += "(P?) ";
        else stat += 0.35;
        if(!playingTree.getRoot().getChildren().isEmpty() && !roleTree.getRoot().getChildren().isEmpty()){
            if(playingTree.getRoot().getChildren().size() == roleTree.getRoot().getChildren().size()) {
                text += "Ready";
                stat += 0.3;
                confirmButton.setDisable(false);
            }
            else {
                text += "(Sync...)";
                double max = Math.max(playingTree.getRoot().getChildren().size(), roleTree.getRoot().getChildren().size());
                double temp = Math.abs(playingTree.getRoot().getChildren().size() - roleTree.getRoot().getChildren().size());
                stat += (0.3 * (1-(temp/max)));
                confirmButton.setDisable(true);
            }
        }
        else confirmButton.setDisable(true);
        statusLabel.setText(text);
        dmProgress.setProgress(stat);
        scoreUpdaterTab.setDisable(true);
    }

    //--Menu Bar--//
    private void getGithub(boolean silent){
        try{
            HttpURLConnection httpCon = (HttpURLConnection) new URL("https://api.github.com/repos/"+GITHUB_URL).openConnection();
            httpCon.addRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
            StringBuilder responseSB = new StringBuilder(), sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) responseSB.append("\n").append(line);
            in.close();
            Arrays.stream(responseSB.toString().split("\"tag_name\":")).skip(1).map(l -> l.split(",")[0]).forEach(sb::append);
            String version = sb.toString().split("\"")[1];
            if(!version.equals(Main.VERSION))alertNewUpdate(version);
            else if(!silent) alertNoUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getAbout(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About DMMT");
        alert.setHeaderText("What is The Danganronpa Murder Mystery Tool?");
        alert.setContentText("I dunno...");
        try{
            File f = new File(Main.class.getResource("/Info/about.txt").getPath());
            FileInputStream fis = new FileInputStream(f);
            byte[] data = new byte[(int)f.length()];
            if(fis.read(data) == 0) System.out.println("Data gathered");
            fis.close();
            alert.setContentText(new String(data, StandardCharsets.UTF_8));
        }catch (IOException e){
            e.printStackTrace();
        }
        alert.showAndWait();
    }
    public void getHelp(){
        openLink("Shadow-Spade/Danganronpa-Murder-Mystery-Tool/blob/master/README.md");
    }
    private void alertNewUpdate(String version){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Check for Updates");
        alert.setHeaderText("A new version ("+version+") of the program is available for download on Github");
        alert.setContentText("Would you like to Update?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) openLink(GITHUB_URL);
    }
    private void alertNoUpdate(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Check for Updates");
        alert.setHeaderText("Your version is up to date!");
        alert.showAndWait();
    }
    private void openLink(String url){
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI("https://github.com/"+url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    //--Helpers--//
    private static List<Object> addPlayer(Player s){
        return Arrays.asList(
                s.getID(), s.getName(), s.getEffectiveName(),
                "=SUM(INDIRECT(\"RC[1]\", FALSE),INDIRECT(\"RC[2]\", FALSE))",
                "=SUM(INDIRECT(\"RC[6]\", FALSE):INDIRECT(\"RC[11]\", FALSE))",
                s.getLosses(), s.getKills(), s.getPerformance(), s.getLeft(),
                "",
                s.getSingleWin(0), s.getSingleWin(1), s.getSingleWin(2),
                s.getSingleWin(3), s.getSingleWin(4), s.getSingleWin(5)
        );
    }
    private ArrayList<Role> freshCopy(){
        ArrayList<Role> ret = new ArrayList<>();
        for(Role r: ROLES) ret.add(new Role(r));
        return ret;
    }
    private ArrayList<Role> getSubArray(String type, ArrayList<Role> list, boolean allBut){
        int first = -1, last = -1;
        for(int x = 0; x < list.size(); x++){
            if(!list.get(x).getType().equals(type) && !allBut) continue;
            if(list.get(x).getType().equals(type) && allBut) continue;
            if(first == -1) first = x;
            last = x;
        }
        if(first == -1) return null;
        return new ArrayList<>(list.subList(first, last+1));
    }
    private boolean fieldHasDigits(TextField field){
        return field.getText().matches("[0-9]+");
    }
    private void addRole(ArrayList<Role> roles, ArrayList<Role> temp, Role role){
        temp.remove(role);
        roles.add(role);
    }
    private void sendDM(String ID, String message){
        jda.getGuildById(sc.getGuildID()).getMemberById(ID).getUser().openPrivateChannel().queue((ch) -> ch.sendMessage(message).queue());
    }
    private void buildList(String range){
        try {
            List<List<Object>> sheetVals = service.spreadsheets().values().get(sc.getSheetID(), range).execute().getValues();
            if (sheetVals != null && !sheetVals.isEmpty()) {
                for (List row: sheetVals) {
                    if(!row.isEmpty()) {
                        if(range.equals(sc.getRolesRange())) {
                            /* Role format:  [Role Type, Name, Description]
                             * Player info:  [Discord ID, Discord Name, Effective Name]
                             * Player stats: [Losses, Kills, Performance, Left Games]
                             * Player wins:  [Graduation, Mastermind, Evil, Despair, Cult, Vampire]
                             * */
                            ROLES.add(new Role(row.get(0).toString(), row.get(1).toString(), row.get(2).toString()));
                            if(!RESOURCES.contains(row.get(0).toString())) RESOURCES.add(row.get(0).toString());
                        }
                        else if(range.equals(sc.getPlayerRange())) {
                            PLAYERS.add(new Player(
                                    //--Player Info--//
                                    row.get(0).toString(), row.get(1).toString(), row.get(2).toString(),
                                    //--Player Stats--//
                                    Integer.parseInt(row.get(5).toString()), Integer.parseInt(row.get(6).toString()), Integer.parseInt(row.get(7).toString()), Integer.parseInt(row.get(8).toString()),
                                    //--Win Types--//
                                    Integer.parseInt(row.get(10).toString()), Integer.parseInt(row.get(11).toString()), Integer.parseInt(row.get(12).toString()),
                                    Integer.parseInt(row.get(13).toString()), Integer.parseInt(row.get(14).toString()), Integer.parseInt(row.get(15).toString())
                            ));
                        }
                        else if(range.equals(sc.getTagsRange())){
                            TAGS.add(new Tag(row.get(0).toString(), row.get(1).toString(), row.get(2).toString()));
                        }
                        else if(range.equals(sc.getGameModeRange())){
                            GAME_MODES.add(new GameMode(row.get(0).toString(), new ArrayList<Object>(row.subList(1,row.size()))));
                        }
                        else System.out.println("Something happened?");
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private void buildButtons(String type, FlowPane append, int...data){
        for(int d: data){
            Button but = new Button(""+d);
            if(type.equals("RNG")) {
                but.setFont(Font.font("System", FontWeight.BOLD, 42));
                but.setOnMousePressed(event -> preRNG(d));
            }
            else if(type.equals("TIME")) {
                but.setText(but.getText()+((d == 1)?(" Minute"):(" Minutes")));
                but.setFont(Font.font("System", FontWeight.BOLD, 25));
                but.setOnMousePressed(event -> startTimer(time(d)));
            }
            else System.out.println("Action Not Applied");
            append.getChildren().add(but);
        }
    }
}