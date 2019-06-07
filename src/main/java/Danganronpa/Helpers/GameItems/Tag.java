package Danganronpa.Helpers.GameItems;

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
