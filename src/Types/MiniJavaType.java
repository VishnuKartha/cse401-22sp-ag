package Types;

import Semantics.SymbolTables.GlobalSymbolTable;

public abstract class MiniJavaType {

    // Return true if type o is the same
    public abstract boolean typeEquals(MiniJavaType o);

    // Return true if o is assignable to this
    public abstract boolean assignable(MiniJavaType o, GlobalSymbolTable gt);

}
