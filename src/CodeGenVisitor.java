import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.GlobalSymbolTable;

public class CodeGenVisitor implements Visitor {

    private StringBuilder sb;
    private GlobalSymbolTable gT;
    private String classScope;
    private String methodScope;
    private int stackSpace;

    public CodeGenVisitor(GlobalSymbolTable gst){
        gT = gst;
        sb = new StringBuilder();
        stackSpace = 0;
    }

    public String getCodeGen(){
        return sb.toString();
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
    }

    @Override
    public void visit(ClassDeclSimple n) {

    }

    @Override
    public void visit(ClassDeclExtends n) {

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
        stackSpace+=8;
        sb.append("\tmovq\t%rsp,%rbp\n");
    }

    private void epilogue(){
        sb.append("\tmovq\t%rbp,%rsp\n");
        sb.append("\tpopq\t%rbp\n");
        stackSpace-=8;
        sb.append("\tret\n");

    }
}