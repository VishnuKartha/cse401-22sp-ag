package Semantics.TableBuilderVisitors;

import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.GlobalSymbolTable;
import Semantics.SymbolTables.MethodSymbolTable;
import Types.*;

public class MethodTableBuilder implements Visitor {

    private GlobalSymbolTable gT;
    private String classScope;
    private String methodScope;
    private boolean typeError;
    private int offset;

    public MethodTableBuilder(GlobalSymbolTable gst){
        gT = gst;
        offset = 0;
    }

    public GlobalSymbolTable getGlobalTable(){
        return gT;
    }

    public boolean errorStatus(){
        return typeError;
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
        classScope = n.i.s;
        for(int i =0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }
        classScope = null;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        classScope = n.i.s;
        for(int i =0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }
        classScope = null;

    }

    @Override
    public void visit(VarDecl n) {
        MethodSymbolTable mt = gT.classTables.get(classScope).methodTables.get(methodScope);
        if(mt.vars.containsKey(n.i.s) || mt.params.containsKey(n.i.s)){
            System.err.println("Semantic Error: Variable has already been declared " + n.line_number);
            typeError = true;
            return;
        }
        if(n.t instanceof IdentifierType){
            // It is a class
            if(!gT.classTypes.containsKey(((IdentifierType) n.t).s)){
                System.err.println("Semantic Error: Type does not exist on line " + n.line_number);
                typeError = true;
                return;
            }
            mt.vars.put(n.i.s, gT.classTypes.get(((IdentifierType) n.t).s));
            mt.offsets.put(n.i.s, offset);
        }else if(n.t instanceof IntegerType){
            mt.vars.put(n.i.s, new PrimitiveType(PrimitiveType.INT));
            mt.offsets.put(n.i.s, offset);
        }else if(n.t instanceof BooleanType){
            mt.vars.put(n.i.s, new PrimitiveType(PrimitiveType.BOOLEAN));
            mt.offsets.put(n.i.s, offset);
        }else if(n.t instanceof IntArrayType){
            mt.vars.put(n.i.s, new ArrayType(new PrimitiveType(PrimitiveType.INT)));
            mt.offsets.put(n.i.s, offset);
        }
    }

    @Override
    public void visit(MethodDecl n) {
        methodScope = n.i.s;
        for(int i =0; i < n.fl.size(); i++){
            offset = i;
            n.fl.get(i).accept(this);
        }
        offset = 0;
        for(int i =0; i < n.vl.size(); i++){
            offset = i;
            n.vl.get(i).accept(this);
        }
        offset = 0;
        methodScope = null;
    }

    @Override
    public void visit(Formal n) {
        MethodSymbolTable mt = gT.classTables.get(classScope).methodTables.get(methodScope);
        MethodType mType = gT.classTables.get(classScope).methods.get(methodScope);
        if(mt.params.containsKey(n.i.s)){
            System.err.println("Semantic Error: Id has already been declared " + n.line_number);
            typeError = true;
            return;
        }
        if(n.t instanceof IdentifierType){
            // It is a class
            if(!gT.classTypes.containsKey(((IdentifierType) n.t).s)){
                System.err.println("Semantic Error: Type does not exist on line " + n.line_number);
                typeError = true;
                return;
            }
            mt.params.put(n.i.s, gT.classTypes.get(((IdentifierType) n.t).s));
            mType.params.add(gT.classTypes.get(((IdentifierType) n.t).s));
            mt.offsets.put(n.i.s, offset);
        }else if(n.t instanceof IntegerType){
            PrimitiveType pt = new PrimitiveType(PrimitiveType.INT);
            mt.params.put(n.i.s, new PrimitiveType(PrimitiveType.INT));
            mType.params.add(pt);
            mt.offsets.put(n.i.s, offset);
        }else if(n.t instanceof BooleanType){
            PrimitiveType pt = new PrimitiveType(PrimitiveType.BOOLEAN);
            mt.params.put(n.i.s, pt);
            mType.params.add(pt);
            mt.offsets.put(n.i.s, offset);
        }else if(n.t instanceof IntArrayType){
            PrimitiveType pt = new PrimitiveType(PrimitiveType.INT);
            mt.params.put(n.i.s, new ArrayType(pt));
            mType.params.add(new ArrayType(pt));
            mt.offsets.put(n.i.s, offset);
        }
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
