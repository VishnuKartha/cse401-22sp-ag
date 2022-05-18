package Types;

import AST.MethodDecl;

import java.util.List;

public class MethodType extends MiniJavaType{

    public String method;
    public MiniJavaType returnType;
    public List<MiniJavaType> params;

    public MethodType(String m, MiniJavaType rt, List<MiniJavaType> p){
        method = m;
        returnType = rt;
        params = p;
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
    public boolean assignable(MiniJavaType o) {
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
        return returnType.assignable(other.returnType);
    }

    public void addParam(MiniJavaType type){
        params.add(type);
    }
}
