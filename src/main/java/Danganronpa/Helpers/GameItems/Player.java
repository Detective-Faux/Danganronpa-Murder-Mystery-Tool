package Danganronpa.Helpers.GameItems;

import net.dv8tion.jda.core.entities.Member;

import java.util.List;

public class Player{
    public static final String[] WIN_TYPES = new String[]{"Graduation","Mastermind","Evil","Despair","Cult","Vampire"};
    private String name, effectiveName, ID;
    private int losses, kills, performance, left;
    private int[] wins;

    public Player(Member member){
        this.name = member.getUser().getName();
        this.effectiveName = member.getEffectiveName();
        this.ID = member.getUser().getId();
        this.kills = 0;
        this.losses = 0;
        this.performance = 0;
        this.left = 0;
        this.wins = new int[6];
    }
    public Player(Player p){
        this.name = p.getName();
        this.effectiveName = p.getEffectiveName();
        this.ID = p.getID();
        this.kills = p.getKills();
        this.losses = p.getLosses();
        this.performance = p.getPerformance();
        this.wins = p.getWins();
        this.left = p.getLeft();
    }
    public Player(String ID, String name, String effectiveName, int losses, int kills, int performance, int left, int... wins) {
        this.ID = ID;
        this.name = name;
        this.effectiveName = effectiveName;
        this.kills = kills;
        this.losses = losses;
        this.performance = performance;
        this.left = left;
        this.wins = wins;
    }
    public Player(List row){
        this(
                //--Player Info--//
                row.get(0).toString(), row.get(1).toString(), row.get(2).toString(),
                //--Player Stats--//
                Integer.parseInt(row.get(5).toString()), Integer.parseInt(row.get(6).toString()), Integer.parseInt(row.get(7).toString()), Integer.parseInt(row.get(8).toString()),
                //--Win Types--//
                Integer.parseInt(row.get(10).toString()), Integer.parseInt(row.get(11).toString()), Integer.parseInt(row.get(12).toString()),
                Integer.parseInt(row.get(13).toString()), Integer.parseInt(row.get(14).toString()), Integer.parseInt(row.get(15).toString())
        );
    }

    public String getName() {
        return this.name;
    }
    public String getID() {
        return ID;
    }

    public int getLosses() {
        return losses;
    }
    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getKills() {
        return kills;
    }
    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getPerformance() {
        return performance;
    }
    public void setPerformance(int performance) {
        this.performance = performance;
    }

    public int getLeft() {
        return left;
    }
    public void setLeft(int left) {
        this.left = left;
    }

    public int[] getWins() {
        return wins;
    }
    public int getSingleWin(int x){
        return wins[x];
    }
    public void setWins(int[] wins) {
        this.wins = wins;
    }
    public int getTotalWins(){
        int ret = 0;
        for(int w : wins) ret += w;
        return ret;
    }
    public int getTotalGames(){
        return getTotalWins()+losses;
    }

    public String getEffectiveName() {
        return effectiveName;
    }
    public void setEffectiveName(String effectiveName) {
        this.effectiveName = effectiveName;
    }

    @Override
    public String toString() {
        return getEffectiveName();
    }
}
