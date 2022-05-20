package Types;

import Semantics.SymbolTables.GlobalSymbolTable;

import java.util.HashSet;

public class ClassType extends MiniJavaType{

    public String type;
    public String superType;

    public ClassType(String t, String st){
        type = t;
        superType = st;
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
    public boolean assignable(MiniJavaType o, GlobalSymbolTable gt) {
        if(!(o instanceof ClassType)){
            return false;
        }
        ClassType other = (ClassType) o;
        boolean sameType = type.equals(other.type);
        if(sameType){
            return true;
        }
        HashSet<String> visited = new HashSet<>();
        visited.add(type);
        ClassType ct = gt.classTypes.get(superType);
        while(ct != null && !visited.contains(superType)){
            visited.add(ct.type);
            if(ct.type.equals(other.type)){
                return true;
            }
            ct = gt.classTypes.get(ct.superType);
        }
        return false;
    }

    public String toString(){
        String s = type;
        if(superType == null){
            return s;
        }
        return s + " extends " + superType;
    }
}
