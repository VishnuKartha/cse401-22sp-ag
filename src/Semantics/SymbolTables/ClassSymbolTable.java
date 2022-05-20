package Semantics.SymbolTables;

import Types.MethodType;
import Types.MiniJavaType;

import java.util.HashMap;

public class ClassSymbolTable {
    public HashMap<String, MiniJavaType> fields;
    public HashMap<String, MethodType> methods;
    public HashMap<String, MethodSymbolTable> methodTables;
    public GlobalSymbolTable top;

    public ClassSymbolTable(GlobalSymbolTable parent){
        fields = new HashMap<>();
        methods = new HashMap<>();
        methodTables = new HashMap<>();
        top = parent;
    }
}
