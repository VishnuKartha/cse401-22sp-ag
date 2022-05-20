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

    public String toString(int depth){
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < depth; i++) sb.append("  ");
        sb.append("Parameters:\n");
        for(String s : params.keySet()){
            for(int i =0; i < depth; i++) sb.append("  ");
            sb.append(s).append(" of type ").append(params.get(s).toString()).append("\n");
        }
        for(int i =0; i < depth; i++) sb.append("  ");
        sb.append("Local Variables:\n");
        for(String s : vars.keySet()){
            for(int i =0; i < depth; i++) sb.append("  ");
            sb.append(s).append(" of type ").append(vars.get(s).toString()).append("\n");
        }
        return sb.toString();

    }
}
