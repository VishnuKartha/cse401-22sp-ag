package AST;

import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

abstract public class ASTNode {
  // Line number in source file.
  public final int line_number;
  public int ind_depth;

  // Constructor
  public ASTNode(Location pos) {
    this.line_number = pos.getLine();
    this.ind_depth =0;
  }
  public void increment_depth() {
    ind_depth++;
  }
  public void decrrement_depth() {
    ind_depth--;
  }
}
