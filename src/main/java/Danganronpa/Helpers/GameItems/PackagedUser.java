package Danganronpa.Helpers.GameItems;

import java.util.ArrayList;
import java.util.Arrays;

public class PackagedUser {
    private User user;
    private Role role;
    private ArrayList<Tag> tags;

    public PackagedUser(User user, Role role, ArrayList<Tag> tags) {
        this.user = user;
        this.role = role;
        this.tags = tags;
    }
    public PackagedUser(Role role){
        this(null, role, new ArrayList<>());
    }
    public PackagedUser(Role role, Tag...tags){
        this(null, role, new ArrayList<>(Arrays.asList(tags)));
    }
    public PackagedUser(){
        this(null, null, new ArrayList<>());
    }

    public User getUser() {
        return user;
    }
    public String getID(){
        return user.getID();
    }
    public void setUser(User user) {
        this.user = user;
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
        return ""+ getUser()+": "+getTagsAsString()+getRole();
    }
}
