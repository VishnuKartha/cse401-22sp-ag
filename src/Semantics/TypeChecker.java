package Semantics;

import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.ClassSymbolTable;
import Semantics.SymbolTables.GlobalSymbolTable;
import Semantics.SymbolTables.MethodSymbolTable;
import Semantics.TableBuilderVisitors.GlobalTableBuilder;
import Types.*;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;

public class TypeChecker implements Visitor {
    private GlobalSymbolTable globalTable;
    private String classScope;
    private String methodScope;
    private boolean typeError;

    public TypeChecker(GlobalSymbolTable global){
       globalTable = global;
    }
    public boolean errorStatus(){return typeError;}
    @Override
    public void visit(Program n) {
        n.m.accept(this);
        for(int i =0; i < n.cl.size(); i++){
            n.cl.get(i).accept(this);
        }
    }

    @Override
    public void visit(MainClass n) {
        classScope = n.i1.s;
        methodScope = "main";
        n.s.accept(this);
        methodScope = null;
        classScope = null;
    }

    @Override
    public void visit(ClassDeclSimple n) {
        classScope = n.i.s;
        for(int i = 0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }
        classScope = null;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        classScope = n.i.s;
        // check Inheritance
        ClassType ct = globalTable.classTypes.get(classScope);
        HashSet<String> visited = new HashSet<>();
        visited.add(classScope);
        while(ct.superType != null && globalTable.classTypes.containsKey(ct.superType)){
            ct = globalTable.classTypes.get(ct.superType);
            // Found cycle
            if(visited.contains(ct.type)){
                System.out.println("Invalid inheritance structure");
                typeError = true;
                return;
            }
            visited.add(ct.type);
        }
        for(int i = 0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }
        classScope = null;
    }

    @Override
    public void visit(VarDecl n) {

    }

    @Override
    public void visit(MethodDecl n) {
        String temp = classScope;
        methodScope = n.i.s;
        // Check if we can override
        MethodType mta = globalTable.classTables.get(classScope).methods.get(methodScope);
        ClassType ct = globalTable.classTypes.get(classScope);
        boolean canOverride = true;
        if(ct.superType != null){
            // Might not have checked cyclicness
            HashSet<String> visited = new HashSet<>();
            visited.add(classScope);
            while(ct.superType != null && !visited.contains(ct.superType) && globalTable.classTypes.containsKey(ct.superType)){
                ct = globalTable.classTypes.get(ct.superType);
                if(globalTable.classTables.get(ct.type).methods.containsKey(methodScope)){
                    MethodType mtb = globalTable.classTables.get(ct.type).methods.get(methodScope);
                    if(mta.returnType.assignable(mtb.returnType, globalTable) && mta.params.size() == mtb.params.size()){
                        for(int i =0; i < mta.params.size(); i++){
                            if(!mta.params.get(i).typeEquals(mtb.params.get(i))){
                                canOverride = false;
                            }
                        }
                        if(canOverride){
                            classScope = ct.type;
                        }
                    }else{
                        canOverride = false;
                    }
                }
                visited.add(ct.type);
            }
        }
        for(int i =0; i < n.sl.size(); i++){
            n.sl.get(i).accept(this);
        }
        n.e.accept(this);
        MethodType mType = globalTable.classTables.get(classScope).methods.get(methodScope);
        if(!n.e.type.assignable(mType.returnType, globalTable)){
            System.out.println("Return type does not match at line " + n.line_number);
            typeError = true;
        }
        n.type = mType;
        methodScope = null;
        classScope = temp;
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
        for(int i =0; i < n.sl.size(); i++){
            n.sl.get(i).accept(this);
        }
    }

    @Override
    public void visit(If n) {
        n.e.accept(this);
        if(!n.e.type.typeEquals(PrimitiveType.BOOLEAN)){
            System.out.println("Expression must be type boolean on line " + n.line_number);
        }
        n.s1.accept(this);
        n.s2.accept(this);
    }

    @Override
    public void visit(While n) {
        n.e.accept(this);
        if(!n.e.type.typeEquals(PrimitiveType.BOOLEAN)){
            System.out.println("Expression must be type boolean on line " + n.line_number);
        }
        n.s.accept(this);
    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        if(!n.e.type.typeEquals(PrimitiveType.INT)){
            System.out.println("Can only print integer values on line " + n.line_number);
        }
    }

    @Override
    public void visit(Assign n) {
        MethodSymbolTable mt = globalTable.classTables.get(classScope).methodTables.get(methodScope);
        MiniJavaType l = inheritIdentifier(n.i.s);
        if(l == null){
            System.out.println("Id was never declared on line " + n.line_number);
            typeError = true;
            n.type = Undef.UNDEFINED;
            mt.vars.put(n.i.s, n.type);
        }
        n.e.accept(this);
        if(!n.e.type.assignable(l,globalTable)){
            System.out.println("Expression cannot be assigned on line " + n.line_number);
            typeError = true;
            return;
        }
        n.i.type = n.e.type;
    }

    @Override
    public void visit(ArrayAssign n) {
        MethodSymbolTable mt = globalTable.classTables.get(classScope).methodTables.get(methodScope);
        MiniJavaType l = inheritIdentifier(n.i.s);
        if(l == null){
            System.out.println("Id was never declared on line " + n.line_number);
            typeError = true;
            n.type = Undef.UNDEFINED;
            mt.vars.put(n.i.s, n.type);
        }
        if(!(l instanceof ArrayType)){
            System.out.println("Id is not an array on line " + n.line_number);
            typeError = true;
            return;
        }
        n.e1.accept(this);
        if(!n.e1.type.typeEquals(PrimitiveType.INT)){
            System.out.println("Array index must be integer on line " + n.line_number);
            typeError = true;
            return;
        }
        n.e2.accept(this);
        if(!n.e2.type.typeEquals(PrimitiveType.INT)){
            System.out.println("Array value must be type integer on line " + n.line_number);
            typeError = true;
        }
    }

    @Override
    public void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.BOOLEAN) && n.e2.type.typeEquals(PrimitiveType.BOOLEAN))){
            System.out.println("Both expressions must be booleans on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = true;
            return;
        }
        n.type = PrimitiveType.BOOLEAN;
    }

    @Override
    public void visit(LessThan n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.INT) && n.e2.type.typeEquals(PrimitiveType.INT))){
            System.out.println("Both expressions must be integers on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = true;
            return;
        }
        n.type = PrimitiveType.BOOLEAN;
    }

    @Override
    public void visit(Plus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.INT) && n.e2.type.typeEquals(PrimitiveType.INT))){
            System.out.println("Both expressions must be integers on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = true;
            return;
        }
        n.type = PrimitiveType.INT;
    }

    @Override
    public void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.INT) && n.e2.type.typeEquals(PrimitiveType.INT))){
            System.out.println("Both expressions must be integers on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = false;
            return;
        }
        n.type = PrimitiveType.INT;
    }

    @Override
    public void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.INT) && n.e2.type.typeEquals(PrimitiveType.INT))){
            System.out.println("Both expressions must be integers on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = true;
            return;
        }
        n.type = PrimitiveType.INT;
    }

    @Override
    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        if(!(n.e1.type instanceof ArrayType)){
            System.out.println("Expression is not an array at line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = true;
        }else{
            n.type = ((ArrayType) n.e1.type).element;
        }
        n.e2.accept(this);
        if(!n.e2.type.typeEquals(PrimitiveType.INT)){
            System.out.println("Array index must be an integer on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = true;
        }
        n.type = PrimitiveType.INT;

    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);
        if(n.e.type.typeEquals(Undef.UNDEFINED)){
            System.out.println("Array is undefined on line " + n.line_number);
            n.type = Undef.UNDEFINED;
        }
        if(!(n.e.type instanceof ArrayType)){
            System.out.println("Expression is not an array at line " + n.line_number);
            n.type = Undef.UNDEFINED;
        }else{
            n.type = PrimitiveType.INT;
        }

    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);
        // e has to be a class
        if(!(n.e.type instanceof ClassType)){
            System.out.println("Expression must be a class on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = true;
            return;
        }
        MethodType mt = inheritMethod(n.i.s, ((ClassType) n.e.type).type);
        if (mt == null){
            System.out.println("Method does not exist on line " + n.line_number);
            typeError = true;
            n.type = Undef.UNDEFINED;
            return;
        }
        n.type = mt.returnType;
        ArrayList<MiniJavaType> pl = new ArrayList<>();
        for(int i =0; i < n.el.size(); i++){
            n.el.get(i).accept(this);
            pl.add(n.el.get(i).type);
        }
        if(pl.size() != mt.params.size()){
            System.out.println("Incorrect aount of args on line " + n.line_number);
            typeError = true;
            return;
        }
        for(int i = 0; i < mt.params.size(); i++){
            if(!pl.get(i).assignable(mt.params.get(i),globalTable)){
                System.out.println("Incorrect parameter types on line " + n.line_number);
                typeError = true;
                return;
            }
        }


    }

    @Override
    public void visit(IntegerLiteral n) {
        n.type = PrimitiveType.INT;
    }

    @Override
    public void visit(True n) {
        n.type = PrimitiveType.BOOLEAN;
    }

    @Override
    public void visit(False n) {
        n.type = PrimitiveType.BOOLEAN;
    }

    @Override
    public void visit(IdentifierExp n) {
        MethodSymbolTable mt = globalTable.classTables.get(classScope).methodTables.get(methodScope);
        MiniJavaType t = inheritIdentifier(n.s);
        if(t == null){
            System.out.println("Id was never declared on line " + n.line_number);
            typeError = true;
            n.type = Undef.UNDEFINED;
            mt.vars.put(n.s, n.type);
            return;
        }
        n.type = t;
    }

    @Override
    public void visit(This n) {
        n.type = globalTable.classTypes.get(classScope);
    }

    @Override
    public void visit(NewArray n) {
        n.e.accept(this);
        if(!n.e.type.typeEquals(PrimitiveType.INT)){
            System.out.println("Expression must be integer type on line "+ n.line_number);
            typeError = true;
            return;
        }
        // only int arrays
        n.type = new ArrayType(PrimitiveType.INT);
    }

    @Override
    public void visit(NewObject n) {
        if(!globalTable.classTypes.containsKey(n.i.s)){
            System.out.println("Creating class that does not exist on line " + n.line_number);
            typeError = true;
            n.type = Undef.UNDEFINED;
            return;
        }
        n.type = globalTable.classTypes.get(n.i.s);
    }

    @Override
    public void visit(Not n) {
        n.e.accept(this);
        if(!(n.e.type.typeEquals(PrimitiveType.BOOLEAN))){
            System.out.println("Expression must have type boolean on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            typeError = true;
            return;
        }
        // boolean
        n.type = n.e.type;
    }

    @Override
    public void visit(Identifier n) {

    }

    private MiniJavaType inheritIdentifier(String id){
        ClassSymbolTable st = globalTable.classTables.get(classScope);
        MethodSymbolTable mt = st.methodTables.get(methodScope);
        if(mt.params.containsKey(id)){
            return mt.params.get(id);
        }
        if(mt.vars.containsKey(id)){
            return mt.vars.get(id);
        }
        if(st.fields.containsKey(id)){
            return st.fields.get(id);
        }
        // Couldn't find in current method scope
        ClassType ct = globalTable.classTypes.get(classScope);
        HashSet<String> visited= new HashSet<>();
        visited.add(ct.type);
        System.out.println(ct.type);
        while(ct.superType != null && !visited.contains(ct.superType) && globalTable.classTypes.containsKey(ct.superType)){
            ct = globalTable.classTypes.get(ct.superType);
            st = globalTable.classTables.get(ct.type);
            if(st.fields.containsKey(id)){
                return st.fields.get(id);
            }
            visited.add(ct.type);
        }
        return null;
    }

    private MethodType inheritMethod(String id, String c){
        ClassSymbolTable ct = globalTable.classTables.get(c);
        if(ct.methodTables.containsKey(id)){
            return ct.methods.get(id);
        }
        // Method not in current class
        ClassType cType = globalTable.classTypes.get(classScope);
        HashSet<String> visited = new HashSet<>();
        while(cType.superType != null && !visited.contains(cType.superType) && globalTable.classTypes.containsKey(cType.superType)){
            cType = globalTable.classTypes.get(cType.superType);
            ct = globalTable.classTables.get(cType.type);
            if(ct.methodTables.containsKey(id)){
                return ct.methods.get(id);
            }
            visited.add(cType.type);
        }
        return null;

    }

}