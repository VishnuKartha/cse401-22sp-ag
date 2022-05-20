package Semantics.SymbolTables;

import Types.ClassType;

import java.util.HashMap;

public class GlobalSymbolTable {

    public HashMap<String, ClassType> classTypes;
    public HashMap<String, ClassSymbolTable> classTables;

    public int depth;

    public GlobalSymbolTable(){
        classTypes = new HashMap<>();
        classTables = new HashMap<>();
        depth = 0;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Global\n");
        for(String s : classTables.keySet()){
            sb.append("\t").append(s).append("\n");
            sb.append(classTables.get(s).toString(depth + 1));
        }

        return sb.toString();

    }

}
