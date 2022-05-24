import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.GlobalSymbolTable;

public class CodeGenVisitor implements Visitor {

    private StringBuilder sb;
    private StringBuilder vtable;
    private GlobalSymbolTable gT;
    private String classScope;
    private String methodScope;
    private int stackSpace;


    public CodeGenVisitor(GlobalSymbolTable gst){
        gT = gst;
        sb = new StringBuilder();
        vtable= new StringBuilder();
        vtable.append("\t\t.data\n");
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
    public void visit(VarDecl n) {

    }

    @Override
    public void visit(MethodDecl n) {
        methodScope = n.i.s;

        sb.append(classScope).append("$").append(methodScope).append(":\n");
        prologue();
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

    }

    @Override
    public void visit(While n) {

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

    }

    @Override
    public void visit(IntegerLiteral n) {
        sb.append("\tmovq\t$").append(n.i).append(",%rax\n");
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

    private void prologue(){
        sb.append("\tpushq\t%rbp\n");
        sb.append("\tmovq\t%rsp,%rbp\n");
    }

    private void epilogue(){
        sb.append("\tmovq\t%rbp,%rsp\n");
        sb.append("\tpopq\t%rbp\n");
        sb.append("\tret\n");

    }
}
