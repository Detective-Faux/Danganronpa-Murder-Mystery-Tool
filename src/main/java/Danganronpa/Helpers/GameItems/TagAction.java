package Danganronpa.Helpers.GameItems;

class TagAction {
    enum ActionType{
        NONE,
        EMPTY,
        QUARTER,
        HALF,
        ALL
    }

    private ActionType action;
    private int id;

    TagAction(String action){
        if(action.contains(":")){
            String[] items = action.split(":");
            this.action = findAction(items[0]);
            this.id = Integer.parseInt(items[1])-2;
        }
        else if(action.matches("[0-9]+")){
            this.action = ActionType.NONE;
            this.id = Integer.parseInt(action)-2;
        }
        else{
            this.action = findAction(action);
            this.id = -1;
        }
    }

    ActionType getAction() {
        return action;
    }
    int getId() {
        return id;
    }
    boolean hasID(){
        return id != -1;
    }
    private boolean hasAction(){
        return action != ActionType.NONE;
    }
    boolean isBoth(){
        return hasID() && hasAction();
    }

    private ActionType findAction(String name){
        for(ActionType at: ActionType.values()) if(at.name().equalsIgnoreCase(name)) return at;
        return ActionType.NONE;
    }
}