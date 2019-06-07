package Danganronpa.Helpers.Info;

import java.util.Properties;

public class SelfCredentials {
    private Properties config;
    public SelfCredentials(Properties config){
        this.config = config;
    }

    public String getGuildID() {
        return config.getProperty("guildID");
    }
    public void setGuildID(String guildID) {
        this.config.setProperty("guildID", guildID);
    }

    public String getSheetID() {
        return config.getProperty("sheetID");
    }
    public void setSheetID(String sheetID) {
        this.config.setProperty("sheetID", sheetID);
    }

    public String getDiscordToken() {
        return config.getProperty("discordToken");
    }
    public void setDiscordToken(String discordToken) {
        this.config.setProperty("discordToken",discordToken);
    }

    public String getPlayerRange() {
        return config.getProperty("playersRange");
    }
    public void setPlayerRange(String playersRange) {
        this.config.setProperty("playersRange",playersRange);
    }

    public String getRolesRange() {
        return config.getProperty("rolesRange");
    }
    public void setRolesRange(String rolesRange) {
        this.config.setProperty("rolesRange",rolesRange);
    }

    public String getTagsRange() {
        return config.getProperty("tagsRange");
    }
    public void setTagsRange(String tagsRange) {
        this.config.setProperty("tagsRange",tagsRange);
    }

    public String getGameModeRange() {
        return config.getProperty("gameModesRange");
    }
    public void setGameModeRange(String gameModesRange) {
        this.config.setProperty("gameModesRange",gameModesRange);
    }

    public String[] getHierarchy() {
        return config.getProperty("hierarchy").split(",");
    }
    public void setHierarchy(String[] hierarchy) {
        StringBuilder sb = new StringBuilder();
        for(int x = 0; x < hierarchy.length; x++) sb.append(hierarchy[x]).append((x == hierarchy.length-1)?(""):(","));
        this.config.setProperty("hierarchy",sb.toString());
    }

    public String getPrefix() {
        return config.getProperty("prefix");
    }
    public void setPrefix(String prefix) {
        this.config.setProperty("prefix",prefix);
    }
}
