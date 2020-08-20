package Danganronpa.Helpers.GameItems;

import java.util.List;

public class Role {
    private final String type, name, proxy, description;

    public Role(String type, String name, String proxy, String description){
        this.type = type;
        this.name = name;
        this.proxy = proxy;
        this.description = description;
    }
    public Role(Role role){
        this(role.getType(), role.getName(), role.getProxy(), role.getDescription());
    }
    public Role(List<Object> row){
        this(row.get(0).toString(), row.get(1).toString(), row.get(2).toString(), row.get(3).toString());
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
    public String getProxy(){
        return proxy;
    }

    @Override
    public String toString() {
        return getName();
    }
}
