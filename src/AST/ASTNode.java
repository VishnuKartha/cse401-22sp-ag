package AST;

import AST.Visitor.Visitor;
import Types.MiniJavaType;
import java_cup.runtime.ComplexSymbolFactory.Location;

abstract public class ASTNode {
  // Line number in source file.
  public final int line_number;
  public int ind_depth;

  public MiniJavaType type;

  // Constructor
  public ASTNode(Location pos) {
    this.line_number = pos.getLine();
    this.ind_depth =0;
  }
  public void set_depth(int d) {
    ind_depth = d;
  }
}
