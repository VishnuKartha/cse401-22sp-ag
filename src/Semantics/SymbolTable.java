package Semantics;

import Types.MiniJavaType;

import java.util.HashMap;

public class SymbolTable {

    public String name;
    public HashMap<String, Mapping> table;
    public HashMap<String, SymbolTable> pointers;
    public SymbolTable prevScope;

    public SymbolTable(String n, SymbolTable prev){
        table = new HashMap<>();
        pointers = new HashMap<>();
        prevScope = prev;
        name = n;
    }

    public void addMapping(String s, Mapping m){
        if (pointers.containsKey(s)){
            System.out.println(s + " has already been declared.");
        }
        table.put(s, m);
    }

    public void addPointer(String s, SymbolTable st){
        if (pointers.containsKey(s)){
            System.out.println(s + " has already been declared.");
        }
        pointers.put(s, st);
    }

    public Mapping get(String s){
        SymbolTable t = this;
        while(t != null){
            Mapping m = t.table.get(s);
            if(m != null){
                return m;
            }
            t = t.prevScope;
        }
        return null;
    }
    public static class Mapping {
        public String id;
        public String owner;
        public MiniJavaType type;

        public Mapping(String i, String r, MiniJavaType t){
            id = i;
            owner = r;
            type = t;
        }
    }
}
