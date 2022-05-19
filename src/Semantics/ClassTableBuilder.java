package Semantics;

import AST.*;
import AST.Visitor.Visitor;
import Types.*;

import java.util.ArrayList;
import java.util.Map;

public class ClassTableBuilder implements Visitor {

    public SymbolTable globalTable;
    private SymbolTable topLevel;
    public SymbolTable getGlobal(){
        return globalTable;
    }
    public ClassTableBuilder(SymbolTable table) {
        this.globalTable = table;
        topLevel = table;
    }

    public SymbolTable getGlobalTable(){
        return globalTable;
    }
    @Override
    public void visit(Program n) {
        n.m.accept(this);
        for(int i = 0; i < n.cl.size(); i++){
            n.cl.get(i).accept(this);
        }
    }

    @Override
    public void visit(MainClass n) {

        globalTable = globalTable.pointers.get(n.i1.s);

        ClassType class_type = (ClassType) globalTable.get(n.i1.s).type;
        SymbolTable class_t = class_type.classTable;
        SymbolTable method_t = new SymbolTable("main",class_t);


        MethodType method_type = new MethodType("main", Undef.UNDEFINED,
                new ArrayList<>());
        class_t.addMapping("main", new SymbolTable.Mapping("main", n.i1.s, method_type));
        class_t.addPointer("main", method_t);
        method_t.addMapping(n.i2.s, new SymbolTable.Mapping(n.i2.s, "main", Undef.UNDEFINED));
        globalTable = globalTable.prevScope;
    }

    @Override
    public void visit(ClassDeclSimple n) {

        globalTable = globalTable.pointers.get(n.i.s);
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.get(i).accept(this);
        }
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.get(i).accept(this);
        }
        globalTable = globalTable.prevScope;

    }

    @Override
    public void visit(ClassDeclExtends n) {

        SymbolTable.Mapping sup = globalTable.get(n.j.s);
        if(sup.type == null){
            System.out.println(n.j.s + " has not been defined at line " + n.line_number);
        }

        globalTable = globalTable.pointers.get(n.i.s);
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.get(i).accept(this);
        }
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.get(i).accept(this);
        }
        globalTable = globalTable.prevScope;

    }

    @Override
    public void visit(VarDecl n) {
        if(n.i.type instanceof ClassType){
            ClassType ct = (ClassType) n.i.type;
            SymbolTable.Mapping sT = topLevel.get(ct.type);
            n.i.type = sT.type;
        }
        globalTable.addMapping(n.i.s, new SymbolTable.Mapping(n.i.s, globalTable.name, n.i.type));
        globalTable.addPointer(n.i.s, null);
    }

    @Override
    public void visit(MethodDecl n) {
        SymbolTable methodtable = new SymbolTable(n.i.s,globalTable);
        globalTable = methodtable;
        for(int i =0; i < n.fl.size(); i++){
            n.fl.get(i).accept(this);
        }
        for(int i =0; i < n.vl.size(); i++){
            n.vl.get(i).accept(this);
        }
        MethodType mt = (MethodType) n.i.type;
        if(mt.returnType instanceof ClassType){
            ClassType ct = (ClassType) mt.returnType;
            SymbolTable.Mapping sT = topLevel.get(ct.type);
            mt.returnType = sT.type;
        }
        globalTable = globalTable.prevScope;
        globalTable.addMapping(methodtable.name, new SymbolTable.Mapping(methodtable.name, globalTable.name, n.i.type));
        globalTable.addPointer(methodtable.name, methodtable);

    }

    @Override
    public void visit(Formal n) {
        if(n.i.type instanceof ClassType){
            ClassType ct = (ClassType) n.i.type;
            SymbolTable.Mapping sT = topLevel.get(ct.type);
            n.t.type = sT.type;
            n.i.type = sT.type;
        }
        globalTable.addMapping(n.i.s, new SymbolTable.Mapping(n.i.s, globalTable.name, n.i.type));
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


}
