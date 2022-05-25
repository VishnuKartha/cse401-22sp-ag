package Types;

import Semantics.SymbolTables.GlobalSymbolTable;

public class ArrayType extends MiniJavaType{

    public MiniJavaType element;

    public ArrayType(MiniJavaType elementType){
        element = elementType;
        offset = 0;
    }

    @Override
    public boolean typeEquals(MiniJavaType o) {
        if(!(o instanceof ArrayType)){
            return false;
        }
        ArrayType other = (ArrayType) o;
        return element.typeEquals(other.element);
    }

    @Override
    public boolean assignable(MiniJavaType o, GlobalSymbolTable gst) {
        if(!(o instanceof ArrayType)){
            return false;
        }
        ArrayType other = (ArrayType) o;
        return element.assignable(other.element, gst);
    }

    public String toString(){
        return element.toString() + " array";
    }
}
