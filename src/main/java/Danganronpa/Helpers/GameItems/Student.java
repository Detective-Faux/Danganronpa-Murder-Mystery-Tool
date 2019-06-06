package Danganronpa.Helpers.GameItems;

import java.util.ArrayList;
import java.util.Arrays;

public class Student {
    private Player player;
    private Role role;
    private ArrayList<Tag> tags;

    public Student(Player player, Role role, ArrayList<Tag> tags) {
        this.player = player;
        this.role = role;
        this.tags = tags;
    }
    public Student(Role role){
        this(null, role, new ArrayList<>());
    }
    public Student(Role role, Tag...tags){
        this(null, role, new ArrayList<>(Arrays.asList(tags)));
    }
    public Student(){
        this(null, null, new ArrayList<>());
    }

    public Player getPlayer() {
        return player;
    }
    public String getID(){
        return player.getID();
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }
    public String getTagsAsString() {
        StringBuilder sb = new StringBuilder();
        for(Tag t: tags) sb.append(t.toString());
        return (sb.toString().equals(""))?(""):(sb.append(": ").toString());
    }
    public String getFullTagsAsString() {
        StringBuilder sb = new StringBuilder();
        for(Tag t: tags) sb.append(t.getName()).append(" ");
        return sb.toString();
    }
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }
    public void addTag(Tag tag){
        if(!tags.isEmpty()) tags.clear();
        this.tags.add(tag);
    }

    @Override
    public String toString() {
        return ""+getPlayer()+": "+getTagsAsString()+getRole();
    }
}
