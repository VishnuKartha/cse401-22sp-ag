package AST;

import java.util.List;
import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class StatementList extends ASTNode {
   private List<Statement> list;

   public StatementList(Location pos) {
      super(pos);
      list = new ArrayList<Statement>();
   }

   public void add(Statement n) {
      list.add(0,n);
   }

   public Statement get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }
}
