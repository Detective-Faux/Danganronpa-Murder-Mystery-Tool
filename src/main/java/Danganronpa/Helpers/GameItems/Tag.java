package Danganronpa.Helpers.GameItems;

import java.util.List;

public class Tag {
    private String name, label, description;

    public Tag(){
        this("","","");
    }
    public Tag(String name, String label, String description){
        this.name = name;
        this.label = label;
        this.description = description;
    }
    public Tag(List row){
        this(row.get(0).toString(), row.get(1).toString(), row.get(2).toString());
    }

    public String getName() {
        return name;
    }
    public String getLabel() {
        return label;
    }
    public String getDescription() {
        return description;
    }
    public boolean isEmpty() {
        return label.equals("") && name.equals("") && description.equals("");
    }

    @Override
    public String toString(){
        return (!getLabel().equals(""))?("["+getLabel()+"]"):("");
    }
}
