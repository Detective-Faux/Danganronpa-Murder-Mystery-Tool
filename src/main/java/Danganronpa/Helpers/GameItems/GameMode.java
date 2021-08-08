package Danganronpa.Helpers.GameItems;

import Danganronpa.Helpers.Other.SpreadsheetHandler;

import java.util.ArrayList;
import java.util.List;

public class GameMode {
    private final ArrayList<TagAction> pseudoList;
    private final String name;

    public GameMode(String name, List<Object> pseudoTags) {
        this.name = name;
        this.pseudoList = new ArrayList<>();
        for(Object o: pseudoTags) this.pseudoList.add(new TagAction(o.toString()));
    }
    public GameMode(List<Object> row){
        this(row.get(0).toString(), row.subList(1,row.size()));
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
                case EMPTY: break;
                case NONE: {
                    if(ta.hasID()) getAll.add(SpreadsheetHandler.TAGS.get(ta.getId()));
                    break;
                }
                default:{
                    getAll.addAll(performance(ta,amount));
                    break;
                }
            }
        }
        for(int x = getAll.size(); x < amount; x++) getAll.add(new Tag());
        return getAll;
    }
    private ArrayList<Tag> performance(TagAction ta, int amount){
        ArrayList<Tag> ret = new ArrayList<>();
        if(ta.isBoth()) for(int x = 0; x < amount/ta.getAction().multiplier; x++) ret.add(SpreadsheetHandler.TAGS.get(ta.getId()));
        else for(int x = 0; x < SpreadsheetHandler.TAGS.size()/ta.getAction().multiplier; x++) ret.add(SpreadsheetHandler.TAGS.get(x));
        return ret;
    }

    @Override
    public String toString() {
        return getName();
    }
}
