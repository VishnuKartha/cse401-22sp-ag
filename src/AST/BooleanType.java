package AST;

import AST.Visitor.Visitor;
import Types.PrimitiveType;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class BooleanType extends Type {
  public BooleanType(Location pos) {
    super(pos);
    super.type = new PrimitiveType(PrimitiveType.BOOLEAN);
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
