package Danganronpa.Helpers.GameItems;

public class PackagedUser {
    private final Player user;
    private final SuperRole superRole;

    public PackagedUser(Player user, SuperRole superRole){
        this.user = user;
        this.superRole = superRole;
    }

    public Player getUser() {
        return user;
    }
    public SuperRole getSuperRole() {
        return superRole;
    }

    @Override
    public String toString() {
        return ""+ getUser()+": "+getSuperRole();
    }
}
