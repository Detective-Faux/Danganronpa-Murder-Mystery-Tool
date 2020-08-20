package Danganronpa.Helpers.GameItems;

import net.dv8tion.jda.api.entities.Member;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Player implements Serializable {
    private static final String[] GAME_INFO = new String[]{"Losses","Kills","Performance","Left","Graduation","Mastermind","Evil","Despair","Alternate","Other"};
    private final HashMap<String, Integer> gameInfo = new HashMap<>();
    private final String preferredName, ID;
    private String name;

    public Player(Member member){
        //Used when adding a new user in the selected guild
        this.ID = member.getId();
        this.name = member.getUser().getName();
        this.preferredName = member.getEffectiveName();
        for(String type: GAME_INFO) this.gameInfo.putIfAbsent(type, 0);
    }
    public Player(Object ID, Object name, Object preferredName, Object... gameInfo) {
        //Used to parse the Constructor below
        this.ID = ID.toString();
        this.name = name.toString();
        this.preferredName = preferredName.toString();
        for(int x = 0; x < gameInfo.length; x++) this.gameInfo.putIfAbsent(GAME_INFO[x],Integer.parseInt(gameInfo[x].toString()));
    }
    public Player(List<Object> row){
        //Used when Accessing the spreadsheet
        this(
                //--User Info: ID, Name, Preferred Name--//
                row.get(0), row.get(1), row.get(2),
                //--User Stats: Losses, Kills, Performance, Left--//
                row.get(5), row.get(6), row.get(7), row.get(8),
                //--Win Types: Graduation, Mastermind, Evil, Despair, Alternate, Other--//
                row.get(10), row.get(11), row.get(12), row.get(13), row.get(14), row.get(15)
        );
    }

    public List<Object> getPlayerSheetVariable(){
        return Arrays.asList(
                getID(), getName(), getPreferredName(),
                "=SUM(INDIRECT(\"RC[1]\", FALSE),INDIRECT(\"RC[2]\", FALSE))",
                "=SUM(INDIRECT(\"RC[6]\", FALSE):INDIRECT(\"RC[11]\", FALSE))",
                getGameInfo("Losses"), getGameInfo("Kills"), getGameInfo("Performance"), getGameInfo("Left"),
                "",
                getGameInfo("Graduation"), getGameInfo("Mastermind"), getGameInfo("Evil"),
                getGameInfo("Despair"), getGameInfo("Alternate"), getGameInfo("Other")
        );
    }

    public String getID() {
        return ID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getPreferredName() {
        return preferredName;
    }
    public int getGameInfo(String key){
        return gameInfo.get(key);
    }

    @Override
    public String toString() {
        if(getName().equals(getPreferredName())) return getName();
        return String.format("%s: (%s)",getName(), getPreferredName());
    }
}