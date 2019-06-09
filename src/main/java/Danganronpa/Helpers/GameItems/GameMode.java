package Danganronpa.Helpers.GameItems;

import Danganronpa.Controller;

import java.util.ArrayList;
import java.util.List;

public class GameMode {
    private ArrayList<TagAction> pseudoList;
    private String name;

    public GameMode(String name, ArrayList<Object> pseudoTags) {
        this.name = name;
        this.pseudoList = new ArrayList<>();
        for(Object o: pseudoTags) this.pseudoList.add(new TagAction(o.toString()));
    }
    public GameMode(List row){
        this(row.get(0).toString(), new ArrayList<Object>(row.subList(1,row.size())));
    }

    public int getCalculatedTagsSize(int len){
        return performActions(len).size();
    }
    public String getName() {
        return name;
    }
    private ArrayList<TagAction> getPseudoList() {
        return pseudoList;
    }

    public ArrayList<Tag> performActions(int amount){
        ArrayList<Tag> getAll = new ArrayList<>();
        for(TagAction ta: getPseudoList()){
            if(getAll.size() == amount) return getAll;
            switch (ta.getAction()){
                case ALL:{
                    getAll.addAll(performance(ta,amount,1));
                    break;
                }
                case HALF:{
                    getAll.addAll(performance(ta,amount,2));
                    break;
                }
                case QUARTER:{
                    getAll.addAll(performance(ta,amount,4));
                    break;
                }
                case EMPTY: break;
                case NONE: {
                    if(ta.hasID()) getAll.add(Controller.TAGS.get(ta.getId()));
                    break;
                }
            }
        }
        for(int x = getAll.size(); x < amount; x++) getAll.add(new Tag());
        return getAll;
    }
    private ArrayList<Tag> performance(TagAction ta, int amount, int multiplier){
        ArrayList<Tag> ret = new ArrayList<>();
        if(ta.isBoth()) for(int x = 0; x < amount/multiplier; x++) ret.add(Controller.TAGS.get(ta.getId()));
        else for(int x = 0; x < Controller.TAGS.size()/multiplier; x++) ret.add(Controller.TAGS.get(x));
        return ret;
    }

    @Override
    public String toString() {
        return getName();
    }
}
