package AST;

import AST.Visitor.Visitor;
import Types.PrimitiveType;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IntegerType extends Type {
  public IntegerType(Location pos) {
    super(pos);
    super.type = PrimitiveType.INT;
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
