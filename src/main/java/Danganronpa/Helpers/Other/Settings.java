package Danganronpa.Helpers.Other;

import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Settings {
    public static final Settings instance = new Settings();
    public static final String[] FIELDS = new String[]{"sheetID","username","password"};
    private final Image logo = new Image(Objects.requireNonNull(this.getClass().getResource("/Media/logo.png")).toString());
    private final Properties config;
    private String preferredGuild, discordToken, prefix, extraMessage, roleTitlePrefix;
    private String[] hierarchy;

    public Settings(){
        this.config = new Properties();
    }
    public static Settings getInst(){
        return instance;
    }

    public String getPlayerRange() {
        return config.getProperty("playersRange","Players!A2:P");
    }
    public String getRolesRange() {
        return config.getProperty("rolesRange","Roles!A2:D");
    }
    public String getTagsRange() {
        return config.getProperty("tagsRange","Tags!A2:C");
    }
    public String getGameModeRange() {
        return config.getProperty("gameModesRange","Game Modes!A2:Z");
    }
    public String getLoginRange(){
        return config.getProperty("loginRange","Login!A2:D");
    }
    public String getSettingsRange(){
        return config.getProperty("settingsRange","Settings!B1:B");
    }

    public String getSheetID() {
        return config.getProperty("sheetID","");
    }
    public void setSheetID(String sheetID) {
        this.config.setProperty("sheetID", sheetID);
    }
    public String getUsername() {
        return config.getProperty("username","");
    }
    public void setUsername(String username) {
        this.config.setProperty("username",username);
    }
    public String getPassword() {
        return config.getProperty("password","");
    }
    public void setPassword(String password) {
        this.config.setProperty("password",password);
    }

    public String[] getHierarchy() {
        return hierarchy;
    }
    public void setHierarchy(String hierarchy){
        this.hierarchy = hierarchy.split(";");
    }
    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    public String getPreferredGuild() {
        return preferredGuild;
    }
    public void setPreferredGuild(String preferredGuild) {
        this.preferredGuild = preferredGuild;
    }
    public String getDiscordToken() {
        return discordToken;
    }
    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
    }
    public String getExtraMessage() {
        return extraMessage;
    }
    public void setExtraMessage(String extraMessage) {
        this.extraMessage = extraMessage;
    }
    public String getRoleTitlePrefix() {
        return roleTitlePrefix;
    }
    public void setRoleTitlePrefix(String roleTitlePrefix) {
        this.roleTitlePrefix = roleTitlePrefix;
    }

    public Properties getConfig() {
        return config;
    }
    public String getProperty(String key){
        return config.getProperty(key);
    }
    public String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    public Image getLogo(){
        return logo;
    }

    public void addSettings(List<Object> settings){
        setDiscordToken(settings.get(0).toString());
        setPreferredGuild(settings.get(1).toString());
        setHierarchy(settings.get(2).toString());
        setPrefix(settings.get(3).toString());
        setExtraMessage(settings.get(4).toString());
        setRoleTitlePrefix(settings.get(5).toString());
    }
}
