package Semantics;

import AST.*;
import AST.Visitor.Visitor;
import Types.ClassType;
import Types.MethodType;
import Types.MiniJavaType;
import Types.Undef;

public class InheritanceVisitor implements Visitor {

    private SymbolTable globalTable;
    private SymbolTable topLevel;

    public InheritanceVisitor(SymbolTable global){
        globalTable = global;
        topLevel = global;
    }

    public SymbolTable getGlobalTable(){
        return globalTable;
    }
    @Override
    public void visit(Program n) {
        for(int i =0; i < n.cl.size(); i++){
            n.cl.get(i).accept(this);
        }
    }

    @Override
    public void visit(MainClass n) {

    }

    @Override
    public void visit(ClassDeclSimple n) {

    }

    @Override
    public void visit(ClassDeclExtends n) {
        globalTable = globalTable.pointers.get(n.i.s);
        String superClassname = n.j.s;
        SymbolTable superTable = topLevel.pointers.get(superClassname);
        if(superTable == null){
            return;
        }
        while(superClassname != null){
            for(String c : superTable.table.keySet()){
                if(!globalTable.table.containsKey(c)){
                    if(superTable.get(c).type instanceof MethodType){
                        MethodType mt = (MethodType) superTable.get(c).type;
                        // MainClass method
                        if(mt.returnType == Undef.UNDEFINED){
                            continue;
                        }
                        globalTable.addMapping(c,superTable.get(c));
                    }
                }else{
                    // Method overriding
                    if(superTable.get(c).type instanceof MethodType){
                        MethodType mtb = (MethodType) superTable.get(c).type;
                        MethodType mta = (MethodType) globalTable.get(c).type;
                        if(!mtb.assignable(mta)){
                            System.out.println(c + " cannot be assigned to super class method on line " + n.line_number);
                            globalTable.table.get(c).type = Undef.UNDEFINED;
                        }
                    }

                }
            }
            ClassType superC = (ClassType) topLevel.table.get(superClassname).type;
            superClassname = superC.superType;
            superTable = topLevel.pointers.get(superClassname);
        }

        globalTable = globalTable.prevScope;

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
}
