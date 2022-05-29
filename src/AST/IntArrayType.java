package AST;

import AST.Visitor.Visitor;
import Types.ArrayType;
import Types.PrimitiveType;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class IntArrayType extends Type {
  public IntArrayType(Location pos) {
    super(pos);
    super.type = new ArrayType(new PrimitiveType(PrimitiveType.INT));
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
