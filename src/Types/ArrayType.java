package Types;

public class ArrayType extends MiniJavaType{

    public MiniJavaType element;

    public ArrayType(MiniJavaType elementType){
        element = elementType;
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
    public boolean assignable(MiniJavaType o) {
        if(!(o instanceof ArrayType)){
            return false;
        }
        ArrayType other = (ArrayType) o;
        return element.assignable(other.element);
    }
}
