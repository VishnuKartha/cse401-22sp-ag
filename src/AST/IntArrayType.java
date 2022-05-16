package AST;

import AST.Visitor.Visitor;
import Semantics.TypeInfo;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IntArrayType extends Type {
  public IntArrayType(Location pos) {
    super(TypeInfo.ARRAY, pos);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
