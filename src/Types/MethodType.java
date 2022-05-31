package Types;

import AST.MethodDecl;
import Semantics.SymbolTables.GlobalSymbolTable;

import java.util.List;

public class MethodType extends MiniJavaType{

    public String method;
    public MiniJavaType returnType;
    public List<MiniJavaType> params;

    public MethodType(MiniJavaType rt, List<MiniJavaType> p){
        returnType = rt;
        params = p;
        offset = 0;
    }
    @Override
    public boolean typeEquals(MiniJavaType o) {
        if(!(o instanceof MethodType)){
            return false;
        }
        MethodType other = (MethodType) o;
        if(params.size() != other.params.size()){
            return false;
        }
        for(int i =0; i < params.size(); i++){
            if(!params.get(i).typeEquals(other.params.get(i))){
                return false;
            }
        }
        return returnType.typeEquals(other.returnType);
    }

    @Override
    public boolean assignable(MiniJavaType o, GlobalSymbolTable gT) {
        if(!(o instanceof MethodType)){
            return false;
        }
        MethodType other = (MethodType) o;
        if(params.size() != other.params.size()){
            return false;
        }
        for(int i =0; i < params.size(); i++){
            if(!params.get(i).assignable(other.params.get(i), gT)){
                return false;
            }
        }
        return returnType.assignable(other.returnType, gT);
    }

    public void addParam(MiniJavaType type){
        params.add(type);
    }

    public String toString(){
        return "Method of return type " + returnType.toString();
    }
}
