package Semantics;

import AST.*;
import AST.Visitor.Visitor;
import Types.ClassType;
import Types.MiniJavaType;

import java.util.Map;

public class GlobalTableBuilder implements Visitor {

    public SymbolTable globalTable;

    public SymbolTable getGlobal(){
        return globalTable;
    }

    @Override
    public void visit(Program n) {
        globalTable = new SymbolTable("global", null);
        n.m.accept(this);
        for(int i = 0; i < n.cl.size(); i++){
            n.cl.get(i).accept(this);
        }
    }

    @Override
    public void visit(MainClass n) {
        createClassTable(n.i1.s,n.i1.type);
    }

    @Override
    public void visit(ClassDeclSimple n) {
        createClassTable(n.i.s, n.i.type);
    }

    @Override
    public void visit(ClassDeclExtends n) {
        createClassTable(n.i.s, n.i.type);
    }

    @Override
    public void visit(VarDecl n) {

    }

    @Override
    public void visit(MethodDecl n) {

    }

    @Override
    public void visit(Formal n) {

    }

    @Override
    public void visit(IntArrayType n) {

    }

    @Override
    public void visit(BooleanType n) {

    }

    @Override
    public void visit(IntegerType n) {

    }

    @Override
    public void visit(IdentifierType n) {

    }

    @Override
    public void visit(Block n) {

    }

    @Override
    public void visit(If n) {

    }

    @Override
    public void visit(While n) {

    }

    @Override
    public void visit(Print n) {

    }

    @Override
    public void visit(Assign n) {

    }

    @Override
    public void visit(ArrayAssign n) {

    }

    @Override
    public void visit(And n) {

    }

    @Override
    public void visit(LessThan n) {

    }

    @Override
    public void visit(Plus n) {

    }

    @Override
    public void visit(Minus n) {

    }

    @Override
    public void visit(Times n) {

    }

    @Override
    public void visit(ArrayLookup n) {

    }

    @Override
    public void visit(ArrayLength n) {

    }

    @Override
    public void visit(Call n) {

    }

    @Override
    public void visit(IntegerLiteral n) {

    }

    @Override
    public void visit(True n) {

    }

    @Override
    public void visit(False n) {

    }

    @Override
    public void visit(IdentifierExp n) {

    }

    @Override
    public void visit(This n) {

    }

    @Override
    public void visit(NewArray n) {

    }

    @Override
    public void visit(NewObject n) {

    }

    @Override
    public void visit(Not n) {

    }

    @Override
    public void visit(Identifier n) {

    }

    private void createClassTable(String classId, MiniJavaType classType){
        SymbolTable classTable = new SymbolTable(classId, globalTable);
        ((ClassType) classType).classTable = classTable;

        globalTable.addMapping(classId, new SymbolTable.Mapping(classId, globalTable.name, classType));
        globalTable.addPointer(classId, classTable);
    }
}
