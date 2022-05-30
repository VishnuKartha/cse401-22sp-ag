import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.GlobalSymbolTable;
import Types.ClassType;
import Types.MethodType;
import Types.MiniJavaType;

import java.util.HashMap;
import java.util.Map;

public class CodeGenVisitor implements Visitor {

    private StringBuilder sb;
    private StringBuilder vtable;
    private GlobalSymbolTable gT;
    private String classScope;
    private String methodScope;

    private Map<String,Integer> labelsUsed;
    private int stackVariables;


    public CodeGenVisitor(GlobalSymbolTable gst){
        gT = gst;
        sb = new StringBuilder();
        vtable= new StringBuilder();
        vtable.append("\t\t.data\n");
        this.labelsUsed = new HashMap<>();
        stackVariables = 0;
    }

    public String getCodeGen(){
        return sb.toString() + vtable.toString();
    }

    @Override
    public void visit(Program n) {
        sb.append("\t").append(".text").append("\n");
        sb.append("\t").append(".globl _asm_main").append("\n");
        sb.append("\n");
        n.m.accept(this);
        for(int i = 0; i < n.cl.size(); i++){
            n.cl.get(i).accept(this);
        }
    }



    @Override
    public void visit(MainClass n) {
        sb.append("_asm_main:\n");
        prologue();
        sb.append("\n");
        n.s.accept(this);
        epilogue();
        sb.append("\n");
        vtable.append(n.i1.s).append("$$:\t.quad 0\n");
        vtable.append("\n");
    }

    @Override
    public void visit(ClassDeclSimple n) {
        classScope = n.i.s;
        for(int i =0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }

        vtable.append(n.i.s).append("$$:\t.quad 0\n");
        for(int i = 0; i < n.ml.size(); i++){
            vtable.append("\t\t.quad ").append(n.i.s).append("$").append(n.ml.get(i).i.s).append("\n");
        }

        classScope = null;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        classScope = n.i.s;
        vtable.append(n.i.s).append("$$:\t.quad").append(n.j.s).append("$$\n");
        for(int i =0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }
        classScope = null;

    }

    @Override
    public void visit(VarDecl n) {}

    @Override
    public void visit(MethodDecl n) {
        methodScope = n.i.s;

        sb.append(classScope).append("$").append(methodScope).append(":\n");
        prologue();
        // Local Variables
        gen("subq",8*n.vl.size(),"%rsp");
        stackVariables += n.vl.size();
        for(int i =0; i < n.sl.size(); i++){
            n.sl.get(i).accept(this);
        }
        n.e.accept(this);
        epilogue();

        methodScope = null;
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
        String else_ = generateLabel("else");
        String end_if = generateLabel("end_if");
        n.e.accept(this);


        gen("cmpq", 0, "%rax");
        gen("je", else_);


        n.s1.accept(this);
        gen("jmp", end_if);
        gen(else_ + ":");
        n.s2.accept(this);
        gen(end_if + ":");
    }

    @Override
    public void visit(While n) {
        String end_while = generateLabel("end_while");
        String test = generateLabel("test_while");
        String body = generateLabel("body_while");


        gen(test + ":");


        n.e.accept(this);
        gen("cmpq", 0, "%rax");
        gen("je", end_while);

        gen(body + ":");

        n.s.accept(this);
        gen("jmp", test);
        gen(end_while + ":");

    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        gen("movq","%rax","%rdi");
        gen("call","_put");

    }

    @Override
    public void visit(Assign n) {
        MiniJavaType id;

        n.e.accept(this); // Value is in %rax

        // See if it is Local Var
        if(gT.classTables.get(classScope).methodTables.get(methodScope).vars.containsKey(n.i.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).vars.get(n.i.s);
            gen("movq", "%rax", "-" + (8 + 8*id.offset) + "(%rbp)");
        }else if(gT.classTables.get(classScope).methodTables.get(methodScope).params.containsKey(n.i.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).params.get(n.i.s);
            gen("movq", "%rax", (16 + 8 * id.offset) + "(%rbp)");
        }else{
            id = gT.classTables.get(classScope).fields.get(n.i.s);
            gen("movq", "%rax", (8 + 8 * id.offset) + "(%rdi)");
        }


    }

    @Override
    public void visit(ArrayAssign n) {
        // the offset of the variable
        MiniJavaType id;
        if(gT.classTables.get(classScope).methodTables.get(methodScope).vars.containsKey(n.i.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).vars.get(n.i.s);
        }else if(gT.classTables.get(classScope).methodTables.get(methodScope).params.containsKey(n.i.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).params.get(n.i.s);
        }else{
            id = gT.classTables.get(classScope).fields.get(n.i.s);
        }
        int offset = id.offset;

        String negativeSign = (offset <= 0) ? "" : "-";
        offset = Math.abs(offset);


        n.e1.accept(this);
        gen("pushq","%rax");
        n.e2.accept(this);
        gen("popq","%rdx");

    }

    @Override
    public void visit(And n) {
        String endAnd = generateLabel("end");
        String storeTrue = generateLabel("storeTrueAnd");
        String storeFalse = generateLabel("storeFalseAnd");


        n.e1.accept(this);
        gen("cmpq", 0, "%rax");
        gen("je", storeFalse);
        n.e2.accept(this);
        gen("cmpq", 0, "%rax");
        gen("je", storeFalse);
        gen("jmp", storeTrue);



        gen(storeFalse+":");
        gen("movq",0,"%rax");

        gen("jmp", endAnd);



        gen(storeTrue+":");
        gen("movq",1,"%rax");


        gen(endAnd+":");




    }

    @Override
    public void visit(LessThan n) {
//        String storeFalse = generateLabel("storeFalse");
        String storeTrue = generateLabel("storeTrueLessThan");
        String endLessThan = generateLabel("endLessThan");



        n.e1.accept(this);
        gen("pushq","%rax");
        n.e2.accept(this);
        gen("popq","%rdx");
        gen("cmpq","%rdx","%rax");

        gen("jl" ,storeTrue);

//        gen(storeFalse + ":");
        gen("movq",0,"%rax");
        gen("jmp",endLessThan);

        gen(storeTrue + ":");
        gen("movq",1,"%rax");
        gen(endLessThan + ":");




    }

    @Override
    public void visit(Plus n) {
        n.e1.accept(this);
        gen("pushq","%rax");
        n.e2.accept(this);
        gen("popq","%rdx");
        gen("addq","%rdx","%rax");
        sb.append("\n");
    }

    @Override
    public void visit(Minus n) {
        n.e2.accept(this);
        gen("pushq","%rax");
        n.e1.accept(this);
        gen("popq","%rdx");
        gen("subq","%rdx","%rax");
        sb.append("\n");
    }

    @Override
    public void visit(Times n) {
        n.e2.accept(this);
        gen("pushq","%rax");
        n.e1.accept(this);
        gen("popq","%rdx");
        gen("imulq","%rdx","%rax");
        sb.append("\n");
    }

    @Override
    public void visit(ArrayLookup n) {
        String successfullBoundsCheck = generateLabel("ArrayLookupSuccessfullBoundsCheck");
        String unsuccessfullBoundsCheck = generateLabel("ArrayLookupUnsuccessfullBoundsCheck");
        String endArrayLookUp = generateLabel("endArrayLookUp");


        n.e1.accept(this);
        gen("pushq", "%rax");
        stackVariables++;
        n.e2.accept(this); // rax has the index of the array
        gen("popq", "%rdx"); // rdx has the address of array
        stackVariables--;
        gen("cmpq","0","(%rdx)");
        gen("jl",unsuccessfullBoundsCheck);
        gen("cmpq","%rax","(%rdx)");
        gen("jg",unsuccessfullBoundsCheck);
        gen("jmp",successfullBoundsCheck);
        gen(unsuccessfullBoundsCheck+":");
        gen("call","_ArrayOutofBoundsError");

        gen(successfullBoundsCheck+":");
        gen("movq", "8(%rdx,%rax,8)", "%rax");

        gen(endArrayLookUp+":");

    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);
        gen("movq", "(%rax)", "%rax");
    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);

        ClassType ct = (ClassType) n.e.type;
        MethodType mt = gT.classTables.get(ct.type).methods.get(n.i.s);
        stackVariables += n.el.size();
        if(stackVariables%2 != 0){
            gen("subq", "$8", "%rsp");
        }
        for(int i = n.el.size() - 1; i >= 0 ; i--){
            n.el.get(i).accept(this);
            gen("pushq", "%rax");
        }
        int offset = gT.classTables.get(ct.type).methods.get(n.i.s).offset;
        gen("movq","0(%rdi)","%rax");
        gen("call", "*" + (8 + 8*offset) + "(%rax)");
        sb.append("\n");
    }

    @Override
    public void visit(IntegerLiteral n) {
        gen("movq",n.i,"%rax");
    }

    @Override
    public void visit(True n) {
        gen("movq",1,"%rax");

    }

    @Override
    public void visit(False n) {
        gen("movq",0,"%rax");

    }

    @Override
    public void visit(IdentifierExp n) {
        MiniJavaType id;
        if(gT.classTables.get(classScope).methodTables.get(methodScope).vars.containsKey(n.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).vars.get(n.s);
            gen("movq",  "-" + (8 + 8*id.offset) + "(%rbp)", "%rax");
        }else if(gT.classTables.get(classScope).methodTables.get(methodScope).params.containsKey(n.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).params.get(n.s);
            gen("movq", (16 + 8 * id.offset) + "(%rbp)", "%rax");
        }else{
            id = gT.classTables.get(classScope).fields.get(n.s);
            gen("movq", (8 + 8 * id.offset) + "(%rdi)", "%rax");
        }
    }

    @Override
    public void visit(This n) {
        gen("movq", "%rdi", "%rax");
    }

    @Override
    public void visit(NewArray n) {
        n.e.accept(this);
        // num elements of array stored in rax
        gen("pushq", "%rax"); // save the array len
        stackVariables++;
        gen("addq",1, "%rax"); // gets space to hold the length of the array information
        gen("mulq" , 8, "%rax"); // 8 bytes per element
        if(stackVariables%2 != 0){
            gen("subq", "$8", "%rsp");
        }
        gen("call","_mjcalloc"); // address of allocated state stored in %rax
        gen("popq", "%rdx"); // get the array len information into rdx
        stackVariables--;
        gen("movq", "%rdx", "(%rax)"); // store  the length of the array in first 8 bytes of array space
    }

    @Override
    public void visit(NewObject n) {
        int num_vars = gT.classTables.get(n.i.s).fields.size();
        gen("movq",(8 + 8*num_vars),"%rdi");
        if(stackVariables%2 != 0){
            gen("subq", "$8", "%rsp");
        }
        gen("call","_mjcalloc");
        gen("leaq",n.i.s + "$$(%rip),%rdx");
        gen("movq","%rdx","0(%rax)");
        gen("movq","%rax","%rdi");
    }

    @Override
    public void visit(Not n) {

        n.e.accept(this);
        gen("xor", 1, "%rax");

    }

    @Override
    public void visit(Identifier n) {

    }

    private void prologue(){
        sb.append("\tpushq\t%rbp\n");
        sb.append("\tmovq\t%rsp,%rbp\n");
    }

    private void epilogue(){
        sb.append("\tmovq\t%rbp,%rsp\n");
        sb.append("\tpopq\t%rbp\n");
        sb.append("\tret\n");
    }

    private String generateLabel(String name) {
        int numUsed = this.labelsUsed.getOrDefault(name,0);
        this.labelsUsed.put(name,numUsed+1);
        return name + numUsed+1;
    }
    private void gen(String s) {
        sb.append(s + "\n");
    }

    private void gen(String instruction, String src, String dst) {
        gen("\t" + instruction + "\t" + src + "," + dst);
    }

    private void gen(String instruction, int num, String dst) {
        gen("\t" + instruction + "\t" + "$" + num + "," + dst);
    }

    private void gen(String instruction, String arg) {
        gen("\t" + instruction + "\t" + arg);
    }

}
