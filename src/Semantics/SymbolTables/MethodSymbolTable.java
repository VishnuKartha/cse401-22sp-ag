package Semantics.SymbolTables;

import Types.MethodType;
import Types.MiniJavaType;

import java.util.HashMap;

public class MethodSymbolTable {
    public HashMap<String, MiniJavaType> params;
    public HashMap<String, MiniJavaType> vars;
    public ClassSymbolTable top;

    public MethodSymbolTable(ClassSymbolTable parent){
        params = new HashMap<>();
        vars = new HashMap<>();
        top = parent;
    }
}
