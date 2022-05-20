package Semantics.SymbolTables;

import Types.ClassType;

import java.util.HashMap;

public class GlobalSymbolTable {

    public HashMap<String, ClassType> classTypes;
    public HashMap<String, ClassSymbolTable> classTables;

    public GlobalSymbolTable(){
        classTypes = new HashMap<>();
        classTables = new HashMap<>();
    }

}
