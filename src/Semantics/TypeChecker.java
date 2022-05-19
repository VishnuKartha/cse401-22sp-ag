package Semantics;

import AST.*;
import AST.Visitor.Visitor;
import Types.*;

public class TypeChecker implements Visitor {
    private SymbolTable globalTable;
    private SymbolTable topLevel;

    public TypeChecker(SymbolTable global){
       globalTable = global;
       topLevel = global;
    }
    @Override
    public void visit(Program n) {
        n.m.accept(this);
        for(int i =0; i < n.cl.size(); i++){
            n.cl.get(i).accept(this);
        }
    }

    @Override
    public void visit(MainClass n) {
        globalTable = globalTable.pointers.get(n.i1.s);
        n.s.accept(this);
        globalTable = globalTable.prevScope;
    }

    @Override
    public void visit(ClassDeclSimple n) {
        globalTable = globalTable.pointers.get(n.i.s);
        for(int i = 0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }
        globalTable = globalTable.prevScope;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        globalTable = globalTable.pointers.get(n.i.s);
        for(int i = 0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }
        globalTable = globalTable.prevScope;
    }

    @Override
    public void visit(VarDecl n) {

    }

    @Override
    public void visit(MethodDecl n) {
        MethodType mt = (MethodType) globalTable.get(n.i.s).type;
        globalTable = globalTable.pointers.get(n.i.s);
        for(int i =0; i < n.sl.size(); i++){
            n.sl.get(i).accept(this);
        }
        n.e.accept(this);
        if(!mt.returnType.assignable(n.e.type)){
            System.out.println("Return type does not match at line " + n.line_number);
        }
        globalTable = globalTable.prevScope;
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
        n.e.accept(this);
        n.i.type = n.e.type;
        if(globalTable.get(n.i.s).type.assignable(n.e.type)){
            System.out.println("Expression is not assignable on line " + n.line_number);
        }
    }

    @Override
    public void visit(ArrayAssign n) {
        n.e1.accept(this);
        if(!n.e1.type.typeEquals(PrimitiveType.INT)){
            System.out.println("Array index must be integer on line " + n.line_number);
        }
        n.e2.accept(this);
        ArrayType aT = (ArrayType) globalTable.get(n.i.s).type;
        if(!aT.element.assignable(n.e2.type)){
            System.out.println("Expression not assignable to element type on line " + n.line_number);
        }
    }

    @Override
    public void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.BOOLEAN) && n.e2.type.typeEquals(PrimitiveType.BOOLEAN))){
            System.out.println("Both expressions must be booleans on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            return;
        }
        n.type = PrimitiveType.BOOLEAN;
    }

    @Override
    public void visit(LessThan n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.BOOLEAN) && n.e2.type.typeEquals(PrimitiveType.BOOLEAN))){
            System.out.println("Both expressions must be booleans on line " + n.line_number);
            n.type = Undef.UNDEFINED;
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
            return;
        }
        n.type = PrimitiveType.INT;
    }

    @Override
    public void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.BOOLEAN) && n.e2.type.typeEquals(PrimitiveType.BOOLEAN))){
            System.out.println("Both expressions must be booleans on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            return;
        }
        n.type = PrimitiveType.BOOLEAN;
    }

    @Override
    public void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);
        if(!(n.e1.type.typeEquals(PrimitiveType.BOOLEAN) && n.e2.type.typeEquals(PrimitiveType.BOOLEAN))){
            System.out.println("Both expressions must be booleans on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            return;
        }
        n.type = PrimitiveType.BOOLEAN;
    }

    @Override
    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        if(!(n.e1.type instanceof ArrayType)){
            System.out.println("Expression is not an array at line " + n.line_number);
            n.type = Undef.UNDEFINED;
        }else{
            n.type = ((ArrayType) n.e1.type).element;
        }
        n.e2.accept(this);
        if(!n.e2.type.typeEquals(PrimitiveType.INT)){
            System.out.println("Array index must be an integer on line " + n.line_number);
            n.type = Undef.UNDEFINED;
        }

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
            return;
        }
        ClassType cT = (ClassType) n.e.type;
        SymbolTable classTable = topLevel.pointers.get(cT.type);
        SymbolTable.Mapping method = classTable.get(n.i.s);
        if(method == null){
            System.out.println("Method does not exist in class on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            return;
        }
        n.i.type = method.type;
        if(!(method.type instanceof MethodType)){
            System.out.println("Identifier is not a method on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            return;
        }
        MethodType mT = (MethodType) method.type;
        n.type = mT.returnType;

        if(mT.params.size() != n.el.size()){
            System.out.println("Numbers of arguments do not match on line " + n.line_number);
            return;
        }

        for(int i = 0; i < mT.params.size(); i++){
            n.el.get(i).accept(this);
            MiniJavaType t = n.el.get(i).type;
            if(!mT.params.get(i).assignable(t)){
                System.out.println("Argument types do not match on line " + n.line_number);
                n.type = Undef.UNDEFINED;
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
        SymbolTable.Mapping m = globalTable.get(n.s);
        if(m.type.typeEquals(Undef.UNDEFINED)){
            System.out.println("Identifier not declared on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            return;
        }
        n.type = m.type;
    }

    @Override
    public void visit(This n) {
        SymbolTable tempScope = globalTable;
        while(globalTable.prevScope != null){
            MiniJavaType t = globalTable.prevScope.table.get(globalTable.name).type;
            if(t instanceof ClassType){
                n.type = t;
                globalTable = tempScope;
                return;
            }
            globalTable = globalTable.prevScope;
        }
        globalTable = tempScope;
        n.type = Undef.UNDEFINED;
    }

    @Override
    public void visit(NewArray n) {
        n.e.accept(this);
        if(!n.e.type.typeEquals(PrimitiveType.INT)){
            System.out.println("Expression must be integer type on line "+ n.line_number);
            return;
        }
        // only int arrays
        n.type = new ArrayType(PrimitiveType.INT);
    }

    @Override
    public void visit(NewObject n) {
        SymbolTable.Mapping m = topLevel.get(n.i.s);
        if(m == null){
            System.out.println("Object does not exist on line " + n.line_number);
            return;
        }

        if(!(m.type instanceof ClassType)){
            System.out.println("Identifier is not a class on line" + n.line_number);
            n.type = Undef.UNDEFINED;
            return;
        }
        n.type = m.type;
    }

    @Override
    public void visit(Not n) {
        n.e.accept(this);
        if(!(n.e.type.typeEquals(PrimitiveType.BOOLEAN))){
            System.out.println("Expression must have type boolean on line " + n.line_number);
            n.type = Undef.UNDEFINED;
            return;
        }
        // boolean
        n.type = n.e.type;
    }

    @Override
    public void visit(Identifier n) {

    }
}
