package Danganronpa.Helpers.GameItems;

import java.util.ArrayList;
import java.util.Arrays;

public class SuperRole {
    private final Role role;
    private final ArrayList<Tag> tags;

    private SuperRole(Role role, ArrayList<Tag> tags){
        this.role = role;
        this.tags = tags;
    }

    public SuperRole(Role role){
        this(role, new ArrayList<>());
    }

    public SuperRole(Role role, Tag... tags){
        this(role, new ArrayList<>(Arrays.asList(tags)));
    }

    public Role getRole() {
        return role;
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

    public void addTags(Tag...tags){
        this.tags.addAll(Arrays.asList(tags));
    }

    @Override
    public String toString() {
        return getTagsAsString() + role.toString();
    }
}
