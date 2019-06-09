package Danganronpa.Helpers.GameItems;

import java.util.List;

public class Role {
    private String type, name, description;

    public Role(String type, String name, String description){
        this.type = type;
        this.name = name;
        this.description = description;
    }
    public Role(Role role){
        this(role.getType(), role.getName(), role.getDescription());
    }
    public Role(List row){
        this(row.get(0).toString(), row.get(1).toString(), row.get(2).toString());
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
