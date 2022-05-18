package Types;

import Semantics.SymbolTable;

public class ClassType extends MiniJavaType{

    public String type;
    public String superType;

    public SymbolTable classTable;

    public ClassType(SymbolTable table, String t, String st){
        type = t;
        classTable = table;
        st = superType;
    }
    @Override
    public boolean typeEquals(MiniJavaType o) {
        if(!(o instanceof ClassType)){
            return false;
        }
        ClassType other = (ClassType) o;
        return type.equals(other.type);
    }

    @Override
    public boolean assignable(MiniJavaType o) {
        if(!(o instanceof ClassType)){
            return false;
        }
        ClassType other = (ClassType) o;
        boolean sameType = type.equals(other.type);
        if(sameType){
            return true;
        }
        String superClass = other.superType;
        while(superClass != null){
            if(superClass.equals(type)){
                return true;
            }
            SymbolTable.Mapping m = classTable.superClassTable.get(superClass);
            ClassType otherSuper = (ClassType) m.type;
            superClass = otherSuper.superType;
        }
        return false;
    }
}
