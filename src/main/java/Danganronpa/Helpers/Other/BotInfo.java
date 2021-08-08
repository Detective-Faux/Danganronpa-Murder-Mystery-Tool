package Danganronpa.Helpers.Other;

public class BotInfo {
    public static final String REPOSITORY = "/Shadow-Spade/Danganronpa-Murder-Mystery-Tool";
    public static final String GITHUB = "https://github.com"+ REPOSITORY;
    public static final String API_LATEST = "https://api.github.com/repos"+ REPOSITORY +"/releases/latest";
    public static final String VERSION_MAJOR = "0";
    public static final String VERSION_MINOR = "8";
    public static final String VERSION_REVISION = "1";
    public static final String VERSION = String.format("%s.%s.%s",VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION);
}