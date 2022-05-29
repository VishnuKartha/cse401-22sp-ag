package Types;

import Semantics.SymbolTables.GlobalSymbolTable;

import java.util.Objects;

public class PrimitiveType extends MiniJavaType {

    public static final String INT = "int";
    public static final String BOOLEAN = "boolean";
    private String type;

    public PrimitiveType(String t){
        type = t;
        offset = 0;
    }

    @Override
    public boolean typeEquals(MiniJavaType o) {
        if(!(o instanceof PrimitiveType)){
            return false;
        }
        PrimitiveType other = (PrimitiveType) o;
        return type.equals(other.type);
    }

    @Override
    public boolean assignable(MiniJavaType o, GlobalSymbolTable gst) {
        if(!(o instanceof PrimitiveType)){
            return false;
        }
        PrimitiveType other = (PrimitiveType) o;
        return type.equals(other.type);
    }

    public String toString(){
        return type;
    }
}
