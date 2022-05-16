package AST;

import AST.Visitor.Visitor;
import Semantics.TypeInfo;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IdentifierType extends Type {
  public String s;

  public IdentifierType(String as, Location pos) {
    super(TypeInfo.CLASS, pos);
    s=as;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
