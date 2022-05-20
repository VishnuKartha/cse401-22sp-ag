package AST;

import AST.Visitor.Visitor;
import Types.MethodType;
import Types.MiniJavaType;
import java_cup.runtime.ComplexSymbolFactory.Location;

import java.util.ArrayList;
import java.util.List;

public class MethodDecl extends ASTNode {
  public Type t;
  public Identifier i;
  public FormalList fl;
  public VarDeclList vl;
  public StatementList sl;
  public Exp e;

  public MethodDecl(Type at, Identifier ai, FormalList afl, VarDeclList avl, 
                    StatementList asl, Exp ae, Location pos) {
    super(pos);
    t=at; i=ai; fl=afl; vl=avl; sl=asl; e=ae;
    List<MiniJavaType> params = new ArrayList<>();
    for(int i =0; i < fl.size(); i++){
      params.add(fl.get(i).i.type);
    }
  }
 
  public void accept(Visitor v) {
    v.visit(this);
  }
}
