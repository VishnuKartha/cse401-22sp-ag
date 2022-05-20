package Types;

import Semantics.SymbolTables.GlobalSymbolTable;

public class Undef extends MiniJavaType{

    public static final Undef UNDEFINED = new Undef("undefined");
    private String id;
    private Undef(String i){
        id = i;
    }
    @Override
    public boolean typeEquals(MiniJavaType o) {
       return o == this;
    }

    @Override
    public boolean assignable(MiniJavaType o, GlobalSymbolTable gst) {
        return o == this;
    }

    public String toString(){
        return id;
    }
}
