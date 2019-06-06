package Danganronpa.Helpers.GameItems;

public class Role {
    private String name, type, description;

    public Role(String type, String name, String description){
        this.type = type;
        this.name = name;
        this.description = description;
    }
    public Role(Role role){
        this(role.getType(), role.getName(), role.getDescription());
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getName();
    }
}
