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

    public String toString(int depth){
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < depth; i++) sb.append("\t");
        sb.append("fields:\n");
        for(String s : fields.keySet()){
            sb.append(s);
        }
        for(int i =0; i < depth; i++) sb.append("\t");
        sb.append("methods:\n");
        for(String s : methodTables.keySet()){
            for(int i =0; i < depth; i++) sb.append("\t");
            sb.append(methodTables.get(s).toString(depth + 1));
        }
        return sb.toString();
    }
}
