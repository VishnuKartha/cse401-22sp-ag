package Semantics.TableBuilderVisitors;

import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.ClassSymbolTable;
import Semantics.SymbolTables.GlobalSymbolTable;
import Semantics.SymbolTables.MethodSymbolTable;
import Types.*;

import java.util.ArrayList;

public class ClassTableBuilder implements Visitor {

    private GlobalSymbolTable gT;
    private String classScope;
    private boolean typeError;
    public ClassTableBuilder(GlobalSymbolTable gst) {
        gT = gst;
    }

    public GlobalSymbolTable getGlobalTable(){
        return gT;
    }

    public boolean errorStatus(){
        return typeError;
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
        ClassSymbolTable ct = gT.classTables.get(n.i1.s);
        ct.methods.put("main", new MethodType(Undef.UNDEFINED, new ArrayList<>()));
        ct.methodTables.put("main", new MethodSymbolTable(ct));
    }

    @Override
    public void visit(ClassDeclSimple n) {

        classScope = n.i.s;
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.get(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.get(i).accept(this);
        }
        classScope = null;

    }

    @Override
    public void visit(ClassDeclExtends n) {


        if(!gT.classTypes.containsKey(n.j.s)){
            System.out.println(n.j.s + " has not been defined at line " + n.line_number);
        }
        classScope = n.i.s;
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.get(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.get(i).accept(this);
        }
        classScope = null;
    }

    @Override
    public void visit(VarDecl n) {
        ClassSymbolTable ct = gT.classTables.get(classScope);
        if(ct.fields.containsKey(n.i.s)){
            System.out.println("Class has fields with same name on line " + n.line_number);
            typeError = true;
            return;
        }
        if(n.t instanceof IdentifierType){
            // It is a class
            if(!gT.classTypes.containsKey(((IdentifierType) n.t).s)){
                System.out.println("Class does not exist on line " + n.line_number);
                typeError = true;
                return;
            }
            ct.fields.put(n.i.s, gT.classTypes.get(((IdentifierType) n.t).s));
        }else if(n.t instanceof IntegerType){
            ct.fields.put(n.i.s, PrimitiveType.INT);
        }else if(n.t instanceof BooleanType){
            ct.fields.put(n.i.s, PrimitiveType.BOOLEAN);
        }else if(n.t instanceof IntArrayType){
            ct.fields.put(n.i.s, new ArrayType(PrimitiveType.INT));
        }
    }

    @Override
    public void visit(MethodDecl n) {
        ClassSymbolTable ct = gT.classTables.get(classScope);
        if(ct.methods.containsKey(n.i.s)){
            System.out.println("Class has fields with same name on line " + n.line_number);
            typeError = true;
            return;
        }
        if(n.t instanceof IdentifierType){
            // It is a class
            if(!gT.classTypes.containsKey(((IdentifierType) n.t).s)){
                System.out.println("Return type does not exist on line " + n.line_number);
                typeError = true;
                return;
            }
            ct.methods.put(n.i.s, new MethodType(gT.classTypes.get(((IdentifierType) n.t).s), new ArrayList<>()));
        }else if(n.t instanceof IntegerType){
            ct.methods.put(n.i.s, new MethodType(PrimitiveType.INT, new ArrayList<>()));
        }else if(n.t instanceof BooleanType){
            ct.methods.put(n.i.s, new MethodType(PrimitiveType.BOOLEAN, new ArrayList<>()));
        }else if(n.t instanceof IntArrayType){
            ct.methods.put(n.i.s, new MethodType(new ArrayType(PrimitiveType.INT), new ArrayList<>()));
        }
        ct.methodTables.put(n.i.s, new MethodSymbolTable(ct));

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
