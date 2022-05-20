package Types;

import Semantics.SymbolTables.GlobalSymbolTable;

public class PrimitiveType extends MiniJavaType {

    public static final PrimitiveType INT = new PrimitiveType("int");
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean");
    private String type;

    private PrimitiveType(String t){
        type = t;
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
        return type;
    }
}
