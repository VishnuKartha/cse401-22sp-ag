import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.GlobalSymbolTable;
import Types.ClassType;
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
    private int stackSpace;


    public CodeGenVisitor(GlobalSymbolTable gst){
        gT = gst;
        sb = new StringBuilder();
        vtable= new StringBuilder();
        vtable.append("\t\t.data\n");
        this.labelsUsed = new HashMap<>();
        stackSpace = 0;
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
        for(String m : gT.classTables.get(classScope).methods.keySet()){
            vtable.append("\t\t.quad ").append(n.i.s).append("$").append(m).append("\n");
        }

        classScope = null;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        for(int i =0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }

    }

    @Override
    public void visit(VarDecl n) {}

    @Override
    public void visit(MethodDecl n) {
        methodScope = n.i.s;

        sb.append(classScope).append("$").append(methodScope).append(":\n");
        prologue();
        sb.append("\tsubq\t").append(8*n.vl.size()).append(",%rsp\n\n");
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

        n.e.sense = false;
        n.e.target = else_;
        n.e.accept(this);

        n.e.accept(this);
        if(n.e instanceof True ||n.e instanceof False || n.e instanceof IdentifierExp || n.e instanceof Call || n.e instanceof Not) {
            gen("cmpq", 0, "%rax");
            gen("je", else_);
        }

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


        sb.append(test + ":");


        n.e.sense = false;
        n.e.target = end_while;

        n.e.accept(this);
        if(n.e instanceof True ||n.e instanceof False || n.e instanceof IdentifierExp || n.e instanceof Call || n.e instanceof Not) {
            gen("cmpq", 0, "%rax");
            gen("je", end_while);
        }
        gen(body + ":");

        n.s.accept(this);
        gen("jmp", test);
        gen(end_while + ":");

    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        sb.append("\tmovq\t").append("%rax,%rdi\n");
        sb.append("\tcall\t_put\n");
        sb.append("\n");

    }

    @Override
    public void visit(Assign n) {
        MiniJavaType id;
        if(gT.classTables.get(classScope).methodTables.get(methodScope).vars.containsKey(n.i.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).vars.get(n.i.s);
        }else if(gT.classTables.get(classScope).methodTables.get(methodScope).params.containsKey(n.i.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).params.get(n.i.s);
        }else{
            id = gT.classTables.get(classScope).fields.get(n.i.s);
        }
        int offset = id.offset;

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
        sb.append("\tpushq\t%rax\n");
        n.e2.accept(this);
        sb.append("\tpopq\t%rdx\n");


    }

    @Override
    public void visit(And n) {

    }

    @Override
    public void visit(LessThan n) {
        n.e1.accept(this);
        sb.append("\tpushq\t%rax\n");
        n.e2.accept(this);
        sb.append("\tpopq\t%rdx\n");
        sb.append("\tcmpq\t%rdx,%rax\n");
        if(n.sense) {
            gen("jl", n.target);
        } else {
            gen("jge", n.target);
        }
    }

    @Override
    public void visit(Plus n) {
        n.e1.accept(this);
        sb.append("\tpushq\t%rax\n");
        n.e2.accept(this);
        sb.append("\tpopq\t%rdx\n");
        sb.append("\taddq\t%rdx,%rax\n");
        sb.append("\n");
    }

    @Override
    public void visit(Minus n) {
        n.e2.accept(this);
        sb.append("\tpushq\t%rax\n");
        n.e1.accept(this);
        sb.append("\tpopq\t%rdx\n");
        sb.append("\tsubq\t%rdx,%rax\n");
        sb.append("\n");
    }

    @Override
    public void visit(Times n) {
        n.e1.accept(this);
        sb.append("\tpushq\t%rax\n");
        n.e2.accept(this);
        sb.append("\tpopq\t%rdx\n");
        sb.append("\timulq\t%rdx,%rax\n");
        sb.append("\n");
    }

    @Override
    public void visit(ArrayLookup n) {

    }

    @Override
    public void visit(ArrayLength n) {

    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);
        sb.append("\tmovq\t%rax,%rdi\n");
        sb.append("\tmovq\t0(%rdi),%rax\n");
        ClassType ct = (ClassType) n.e.type;
        int offset = gT.classTables.get(ct.type).methods.get(n.i.s).offset;
        sb.append("\tcall\t*").append(8 + 8*offset).append("(%rax)\n");
        sb.append("\n");
    }

    @Override
    public void visit(IntegerLiteral n) {
        sb.append("\tmovq\t$").append(n.i).append(",%rax\n");
    }

    @Override
    public void visit(True n) {
        sb.append("\tmovq\t$1,%rax\n");

    }

    @Override
    public void visit(False n) {
        sb.append("\tmovq\t$1,%rax\n");

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
        int num_vars = gT.classTables.get(n.i.s).fields.size();
        sb.append("\tmovq\t$").append(8 + 8*num_vars).append(",%rdi\n");
        sb.append("\tcall\t_mjcalloc\n");
        sb.append("\tleaq\t").append(n.i.s).append("$$(%rip),%rdx\n");
        sb.append("\tmovq\t%rdx,0(%rax)\n");
    }

    @Override
    public void visit(Not n) {
        n.e.sense = !n.sense;
        if (n.e instanceof True ||
                n.e instanceof False ||
                n.e instanceof IdentifierExp ||
                n.e instanceof Call) {
            gen("xor", 1, "%rax");

        }
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
        sb.append(s);
    }

    private void gen(String instruction, String src, String dst) {
        gen("\t" + instruction + "\t" + src + "," + dst);
    }

    private void gen(String instruction, int num, String dst) {
        gen("\t" + instruction + "\t" + "$" + num + "," + dst);
    }

    private void gen(String instruction, String dst) {
        gen("\t" + instruction + "\t" + dst);
    }
}
