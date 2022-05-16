package AST;

import AST.Visitor.Visitor;
import Semantics.TypeInfo;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Type extends ASTNode {

    public TypeInfo typeInfo;
    public Type(TypeInfo type, Location pos) {
        super(pos);
        typeInfo = type;
    }
    public abstract void accept(Visitor v);
}
