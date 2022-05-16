package AST;

import AST.Visitor.Visitor;
import Semantics.TypeInfo;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class BooleanType extends Type {
  public BooleanType(Location pos) {
    super(TypeInfo.BOOLEAN,pos);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
