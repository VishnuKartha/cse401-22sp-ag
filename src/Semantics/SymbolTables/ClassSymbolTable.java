package Semantics.SymbolTables;

import Types.MethodType;
import Types.MiniJavaType;

import java.util.HashMap;

public class ClassSymbolTable {
    public HashMap<String, MiniJavaType> fields;
    public HashMap<String, MethodType> methods;
    public HashMap<String, Integer> fieldOffsets;
    public HashMap<String, MethodSymbolTable> methodTables;
    public GlobalSymbolTable top;

    public ClassSymbolTable(GlobalSymbolTable parent){
        fields = new HashMap<>();
        methods = new HashMap<>();
        methodTables = new HashMap<>();
        fieldOffsets = new HashMap<>();
        top = parent;
    }

    public String toString(int depth){
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < depth; i++) sb.append("  ");
        sb.append("fields:\n");
        for(String s : fields.keySet()){
            for(int i =0; i < depth; i++) sb.append("  ");
            sb.append(s).append(" of type ").append(fields.get(s).toString()).append("\n");
        }
        for(int i =0; i < depth; i++) sb.append("  ");
        sb.append("methods:\n");
        for(String s : methodTables.keySet()){
            for(int i =0; i < depth; i++) sb.append("  ");
            sb.append(s).append("\n");
            sb.append(methodTables.get(s).toString(depth + 1));
        }
        return sb.toString();
    }
}
