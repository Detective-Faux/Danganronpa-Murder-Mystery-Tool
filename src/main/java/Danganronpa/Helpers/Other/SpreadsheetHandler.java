package Danganronpa.Helpers.Other;

import Danganronpa.Helpers.GameItems.*;
import Danganronpa.Main;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

public class SpreadsheetHandler {
    public static final ArrayList<Tag> TAGS = new ArrayList<>();
    public static final ArrayList<GameMode> GAME_MODES = new ArrayList<>();
    public static final HashMap<String, ArrayList<Role>> ROLES = new HashMap<>();
    public static final DataFormat USER_FORMAT = new DataFormat("UserList");
    public static final ObservableList<Player> USER_LIST = FXCollections.observableArrayList();
    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetHandler.class);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final SpreadsheetHandler instance = new SpreadsheetHandler();
    private Sheets service;

    public SpreadsheetHandler(){
        final NetHttpTransport HTTP_TRANSPORT;
        try{
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Sheets.Builder(HTTP_TRANSPORT, SpreadsheetHandler.JSON_FACTORY, SpreadsheetHandler.getCredentials(HTTP_TRANSPORT)).setApplicationName("DMMT").build();
        }catch (GeneralSecurityException | IOException e){
            e.printStackTrace();
        }
    }
    public static SpreadsheetHandler getInstance() {
        return instance;
    }

    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        //Removes Unnecessary warning from the CMD log
        final java.util.logging.Logger buggyLogger = java.util.logging.Logger.getLogger(FileDataStoreFactory.class.getName());
        buggyLogger.setLevel(java.util.logging.Level.SEVERE);
        // Load client secrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/Info/credentials.json"))));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singletonList(SheetsScopes.SPREADSHEETS))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens"))).setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public List<List<Object>> getRange(String range){
        return getRange(range, "ROWS");
    }
    public List<List<Object>> getRange(String range, String MajorDimension){
        try {
            return service.spreadsheets().values().get(Settings.getInst().getSheetID(), range).setMajorDimension(MajorDimension).execute().getValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updateSingleEntry(String range, List<List<Object>> values){
        try {
            UpdateValuesResponse result = service.spreadsheets().values()
                    .update(Settings.getInst().getSheetID(), range, new ValueRange().setValues(values))
                    .setValueInputOption("USER_ENTERED").execute();
            LOGGER.debug("{} cells updated.", result.getUpdatedCells());
        } catch (IOException e){
            LOGGER.warn("Exception in 'Update Single Entry' Method!");
        }
    }
    public void updateBatchEntry(String range, List<List<Object>> values) {
        try{
            List<ValueRange> data = new ArrayList<ValueRange>() {{
                add(new ValueRange().setRange(range+"!A"+(2+ SpreadsheetHandler.getInstance().getRange(range).size())+":P").setValues(values));
            }};
            BatchUpdateValuesRequest body = new BatchUpdateValuesRequest().setValueInputOption("USER_ENTERED").setData(data);
            BatchUpdateValuesResponse result = service.spreadsheets().values().batchUpdate(Settings.getInst().getSheetID(), body).execute();
            LOGGER.debug("({} cells updated.) {} new users added to the Player Sheet!", result.getTotalUpdatedCells(), values.size());
        } catch (IOException e){
            LOGGER.warn("Exception in 'Update Batch Entry' Method!");
        }
    }
    public void appendSpreadsheet(String range, List<List<Object>> values){
        try{
            AppendValuesResponse result = service.spreadsheets().values()
                            .append(Settings.getInst().getSheetID(), range, new ValueRange().setValues(values))
                            .setValueInputOption("USER_ENTERED").execute();
            LOGGER.debug("({} cells updated.)", result.getUpdates().getUpdatedCells());
        } catch (IOException e){
            LOGGER.warn("Exception in 'Append Spreadsheet' Method!");
        }
    }

    public static void buildList(String range){
        LOGGER.info("Loading {}", range);
        List<List<Object>> sheetVals = SpreadsheetHandler.getInstance().getRange(range);
        if (!sheetVals.isEmpty()) {
            for (List<Object> row: sheetVals) {
                if(!row.isEmpty()) {
                    if(range.equals(Settings.getInst().getRolesRange())) {
                        String type = row.get(0).toString();
                        ROLES.putIfAbsent(type, new ArrayList<>());
                        ROLES.get(type).add(new Role(row));
                    }
                    else if(range.equals(Settings.getInst().getTagsRange())) TAGS.add(new Tag(row));
                    else if(range.equals(Settings.getInst().getGameModeRange())) GAME_MODES.add(new GameMode(row));
                    else if(range.equals(Settings.getInst().getPlayerRange())) USER_LIST.add(new Player(row));
                    else if(range.equals(Settings.getInst().getLoginRange())) LoginUtil.getInst().addUserToMaps(row);
                }
            }
        }
        //Add an "ALL" category and ignore Beta
        if(range.equals(Settings.getInst().getRolesRange())){
            ArrayList<Role> all = new ArrayList<>();
            for (String key: ROLES.keySet()){
                if(key.equalsIgnoreCase("Beta")) continue;
                all.addAll(ROLES.get(key));
            }
            all.sort(Comparator.comparing(Role::getName));
            ROLES.put("All", all);
        }
    }
}
