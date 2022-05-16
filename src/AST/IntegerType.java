package AST;

import AST.Visitor.Visitor;
import Semantics.TypeInfo;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IntegerType extends Type {
  public IntegerType(Location pos) {
    super(TypeInfo.INT, pos);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
