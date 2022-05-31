
import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.ClassSymbolTable;
import Semantics.SymbolTables.GlobalSymbolTable;
import Semantics.SymbolTables.MethodSymbolTable;
import Types.ClassType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CodeGenVisitor implements Visitor {

    private String classScope;

    private String methodScope;

    private final GlobalSymbolTable gst;

    private int stackSpace;

    private boolean aligned;

    private final Map<String, Integer> labels;

    private final StringBuilder sb;

    private final StringBuilder vt;

    public CodeGenVisitor(GlobalSymbolTable gst) {
        this.gst = gst;
        stackSpace = 0;
        aligned = false;
        labels = new HashMap<>();
        sb = new StringBuilder();
        vt = new StringBuilder();
        vt.append("\t.data\n");

    }

    public String codeGen(){
        return sb.toString() + vt.toString();
    }


    public void visit(Program n) {
        for (String className : gst.classTypes.keySet()) {
            ClassType ct = gst.classTypes.get(className);
            if (ct.superType == null) {
                extendedOffsets(ct.type, gst.classTables.get(ct.type).fields.size(), gst.classTables.get(ct.type).methods.size());
            }
        }
        gen("\t.text");
        gen("\t.globl asm_main");
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            n.cl.get(i).accept(this);
        }
    }

    public void visit(MainClass n) {
        gen("asm_main:");
        prologue();
        n.s.accept(this);
        epilogue();
        vt.append(n.i1.s).append("$$: .quad 0\n");
    }

    private void prologue(){
        gen("pushq", "%rbp");
        gen("movq", "%rsp", "%rbp");
    }

    private void epilogue(){
        gen("movq", "%rbp", "%rsp");
        gen("popq", "%rbp");
        gen("ret", "");
    }

    public void visit(ClassDeclSimple n) {
        classScope = n.i.s;
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.get(i).accept(this);
        }
        vt.append(n.i.s).append("$$: .quad 0\n");
        for (int i = 0; i < n.ml.size(); i++) {
            vt.append("\t\t.quad ").append(classScope).append("$").append(n.ml.get(i).i.s).append("\n");
        }
        classScope = "";
    }

    public void visit(ClassDeclExtends n) {
        classScope = n.i.s;
        for (int i = 0;i < n.ml.size(); i++) {
            n.ml.get(i).accept(this);
        }
        vt.append(n.i.s).append("$$: .quad ").append(n.j.s).append("$$").append("\n");
        ClassType ct = gst.classTypes.get(n.i.s);
        HashMap<Integer, String> methodOffsets = new HashMap<>();

        while(ct != null){
            ClassSymbolTable cst = gst.classTables.get(ct.type);
            for(String m : cst.methods.keySet()){
                int offset = cst.methods.get(m).offset;
                if(!methodOffsets.containsKey(offset)){
                    methodOffsets.put(offset, "\t\t.quad " + ct.type + "$" + m);
                }
            }
            ct = gst.classTypes.get(ct.superType);
        }

        int[] sortedOffsets = new int[methodOffsets.keySet().size()];
        int i = 0;
        for(int x : methodOffsets.keySet()){
            sortedOffsets[i] = x;
            i++;
        }
        Arrays.sort(sortedOffsets);
        for (int sortedOffset : sortedOffsets) {
            vt.append(methodOffsets.get(sortedOffset)).append("\n");
        }
        classScope = "";
    }

    private void extendedOffsets(String b, int fo, int mo){
        for(String c : gst.classTypes.keySet()){
            ClassType cta = gst.classTypes.get(c);
            if(cta.superType != null && cta.superType.equals(b)){
                ClassSymbolTable ct = gst.classTables.get(c);
                for(String f : ct.fields.keySet()){
                    ct.fieldOffsets.put(f, fo);
                    fo++;
                }
                for(String m : ct.methods.keySet()){
                    ClassType ctb = gst.classTypes.get(cta.type);
                    while(ctb.superType != null){
                        ctb = gst.classTypes.get(ctb.superType);
                        ClassSymbolTable csb = gst.classTables.get(ctb.type);
                        if(csb.methods.containsKey(m)){
                            ct.methods.get(m).offset = csb.methods.get(m).offset;
                            break;
                        }
                    }
                    if(ct.methods.get(m).offset == -1){
                        ct.methods.get(m).offset = mo;
                        mo++;
                    }
                }
                extendedOffsets(cta.type, fo, mo);
            }
        }
    }

    public void visit(VarDecl n) {
    }

    public void visit(MethodDecl n) {
        methodScope = n.i.s;
        gen(classScope + "$" + methodScope + ":");
        prologue();
        if (n.vl.size() > 0) {
            gen("subq", 8 * n.vl.size(), "%rsp");
            stackSpace += 8 * n.vl.size();
        }
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.get(i).accept(this);
        }
        stackSpace -= (8 * n.vl.size());
        n.e.accept(this);
        epilogue();
        methodScope = "";
    }

    public void visit(Formal n) {}
    public void visit(IntArrayType n) {}
    public void visit(BooleanType n) {}
    public void visit(IntegerType n) {}
    public void visit(IdentifierType n) {}

    public void visit(Block n) {
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.get(i).accept(this);
        }
    }

    public void visit(If n) {
        n.e.accept(this);
        gen("cmpq", 1, "%rax");
        String elseLabel;
        labels.put("else", labels.getOrDefault("else", 0) + 1);
        elseLabel = "else" + labels.get("else");
        gen("jne", elseLabel);
        n.s1.accept(this);
        String endLabel;
        labels.put("end", labels.getOrDefault("end",0) + 1);
        endLabel = "end" + labels.get("end");
        gen("jmp", endLabel);
        gen(elseLabel + ":");
        n.s2.accept(this);
        gen(endLabel + ":");
    }
    public void visit(While n) {
        String whileLabel;
        labels.put("while", labels.getOrDefault("while",0) + 1);
        whileLabel = "while" + labels.get("while");
        gen(whileLabel + ":");
        n.e.accept(this);
        gen("cmpq", 1, "%rax");
        String endLabel;
        labels.put("end", labels.getOrDefault("end",0)+1);
        endLabel = "end" + labels.get("end");
        gen("jne", endLabel);
        n.s.accept(this);
        gen("jmp", whileLabel);
        gen(endLabel + ":");
    }

    public void visit(Print n) {
        n.e.accept(this);
        gen("pushq", "%rdi");
        stackSpace += 8;
        gen("movq", "%rax", "%rdi");
        if (stackSpace % 16 != 0) {
            gen("pushq", "%rax");
            stackSpace += 8;
            aligned = true;
        }
        gen("call", "put");
        if (aligned) {
            gen("popq", "%rdx");
            stackSpace -= 8;
            aligned = false;
        }
        gen("popq", "%rdi");
        stackSpace -= 8;
    }

    public void visit(Assign n) {
        n.e.accept(this);
        MethodSymbolTable lst = gst.classTables.get(classScope).methodTables.get(methodScope);
        if (lst.vars.containsKey(n.i.s)) { // Check for local var
            int offset = lst.offsets.get(n.i.s);
            gen("movq", "%rax", "-" + (8 + 8*offset) + "(%rbp)");
            return;
        }else if(lst.params.containsKey(n.i.s)){ // Check for method param
            int offset = lst.offsets.get(n.i.s);
            gen("movq", "%rax", (16 + 8*offset) + "(%rbp)");
            return;
        }

        // Check for class fields
        ClassType ct = gst.classTypes.get(classScope);
        while (ct.type != null) {
            if (gst.classTables.get(ct.type).fields.containsKey(n.i.s)) {
                int offset = gst.classTables.get(ct.type).fieldOffsets.get(n.i.s);
                gen("movq", "%rax", (8 + 8*offset) + "(%rdi)");
                return;
            }
            ct = gst.classTypes.get(ct.superType);
        }
    }

    public void visit(ArrayAssign n) {
        n.e1.accept(this);
        gen("pushq", "%rax");
        stackSpace += 8;
        n.e2.accept(this);
        gen("popq", "%rdx");
        stackSpace -= 8;
        MethodSymbolTable lst = gst.classTables.get(classScope).methodTables.get(methodScope);
        if (lst.vars.containsKey(n.i.s)) { // Check for local var
            int offset = lst.offsets.get(n.i.s);
            gen("movq", "-" + (8 + 8*offset) + "(%rbp)", "%rcx");
        }else if(lst.params.containsKey(n.i.s)){ // Check for method param
            int offset = lst.offsets.get(n.i.s);
            gen("movq", (16 + 8*offset) + "(%rbp)", "%rcx");
        }else {
            // Checl for class fields
            ClassType ct = gst.classTypes.get(classScope);
            while (ct.type != null) {
                if (gst.classTables.get(ct.type).fields.containsKey(n.i.s)) {
                    int offset = gst.classTables.get(ct.type).fieldOffsets.get(n.i.s);
                    gen("movq", (8 + 8*offset) + "(%rdi)", "%rcx");
                    break;
                }
                ct = gst.classTypes.get(ct.superType);
            }

        }
        // Compare to size
        gen("cmpq", "%rdx", "0(%rcx)");
        labels.put("indexOutOfBoundsLabel", labels.getOrDefault("indexOutOfBoundsLabel", 0) + 1);
        String indexOutOfBoundsLabel = "indexOutOfBoundsLabel" + labels.get("indexOutOfBoundsLabel");
        gen("jng", indexOutOfBoundsLabel);
        gen("cmpq", 0, "%rdx");
        gen("jl", indexOutOfBoundsLabel);
        gen("movq", "%rax", "8(%rcx,%rdx,8)");
        labels.put("end", labels.getOrDefault("end", 0) + 1);
        indexOutofBoundsGen(indexOutOfBoundsLabel);
    }

    private void indexOutofBoundsGen(String indexOutOfBoundsLabel) {
        String endLabel = "end" + labels.get("end");
        gen("jmp", endLabel);
        gen(indexOutOfBoundsLabel + ":");
        if (stackSpace % 16 != 0) {
            gen("pushq", "%rax");
            stackSpace += 8;
            aligned = true;
        }
        gen("call", "mjerror");
        if (aligned) {
            gen("popq", "%rdx");
            stackSpace -= 8;
            aligned = false;
        }
        gen(endLabel + ":");
    }

    public void visit(And n) {
        n.e1.accept(this);
        gen("cmpq", 1, "%rax");
        labels.put("andFalse", labels.getOrDefault("andFalse", 0) + 1 );
        String andFalseLabel = "andFalse" + labels.get("andFalse");
        gen("jne", andFalseLabel);
        n.e2.accept(this);
        gen("cmpq", 1, "%rax");
        gen("jne", andFalseLabel);
        endLabelGen(andFalseLabel);
    }

    private void endLabelGen(String andFalseLabel) {
        gen("movq", 1, "%rax");
        labels.put("end", labels.getOrDefault("end", 0) + 1);
        String endLabel = "end" + labels.get("end");
        gen("jmp", endLabel);
        gen(andFalseLabel + ":");
        gen("movq", 0, "%rax");
        gen(endLabel + ":");
    }

    public void visit(LessThan n) {
        n.e1.accept(this);
        gen("pushq", "%rax");
        stackSpace += 8;
        n.e2.accept(this);
        gen("popq", "%rdx");
        stackSpace -= 8;
        gen("cmpq", "%rdx", "%rax");

        labels.put("lessThanFalse", labels.getOrDefault("lessThanFalse", 0) + 1);
        String ltFalseLabel = "lessThanFalse" + labels.get("lessThanFalse");
        gen("jng", ltFalseLabel );
        endLabelGen(ltFalseLabel);
    }

    public void visit(Plus n) {
        n.e1.accept(this);
        gen("pushq", "%rax");
        stackSpace += 8;
        n.e2.accept(this);
        gen("popq", "%rdx");
        stackSpace -= 8;
        gen("addq", "%rdx", "%rax");
    }

    public void visit(Minus n) {
        n.e2.accept(this);
        gen("pushq", "%rax");
        stackSpace += 8;
        n.e1.accept(this);
        gen("popq", "%rdx");
        stackSpace -= 8;
        gen("subq", "%rdx", "%rax");
    }

    public void visit(Times n) {
        n.e1.accept(this);
        gen("pushq", "%rax");
        stackSpace += 8;
        n.e2.accept(this);
        gen("popq", "%rdx");
        stackSpace -= 8;
        gen("imulq", "%rdx", "%rax");
    }

    public void visit(ArrayLookup n) {
        n.e2.accept(this);
        gen("pushq", "%rax");
        stackSpace += 8;
        n.e1.accept(this);
        gen("popq", "%rdx");
        stackSpace -= 8;
        // Compare to the size of array
        gen("cmpq", "%rdx", "0(%rax)");
        labels.put("arrayIndexOutOfBounds", labels.getOrDefault("arrayIndexOutOfBounds", 0) + 1);
        String indexOutOfBoundsLabel = "arrayIndexOutOfBounds" + labels.get("arrayIndexOutOfBounds");
        gen("jng", indexOutOfBoundsLabel);
        gen("cmpq", 0, "%rdx");
        gen("jl", indexOutOfBoundsLabel);
        gen("movq", "8(%rax,%rdx,8)", "%rax");
        labels.put("end", labels.getOrDefault("end", 0) + 1);
        indexOutofBoundsGen(indexOutOfBoundsLabel);
    }
    public void visit(ArrayLength n) {
        n.e.accept(this);
        gen("movq", "0(%rax)", "%rax");
    }

    public void visit(Call n) {
        gen("pushq", "%rdi");
        stackSpace += 8;
        // Put this is %rbx
        gen("movq", "%rdi", "%rbx");
        n.e.accept(this);
        gen("movq", "%rax", "%rdi");
        if ((stackSpace + 8 * n.el.size()) % 16 != 0) {
            gen("pushq", "%rax");
            stackSpace += 8;
            aligned = true;
        }
        for (int i = n.el.size() - 1; i >= 0; i--) {
            n.el.get(i).accept(this);
            // This in %rbx
            if (n.el.get(i) instanceof This) {
                gen("pushq", "%rbx");
            } else {
                gen("pushq", "%rax");
            }
            stackSpace += 8;
        }
        gen("movq", "(%rdi)", "%rax");
        ClassType ct = (ClassType) n.e.type;
        int offset = 0;
        // Find method offset
        while (ct.type != null) {
            if (gst.classTables.get(ct.type).methods.containsKey(n.i.s)) {
                offset = gst.classTables.get(ct.type).methods.get(n.i.s).offset;
                break;
            }
            ct = gst.classTypes.get(ct.superType);
        }
        gen("addq", (8 + 8*offset), "%rax");
        gen("call", "*(%rax)");
        for (int i = 0; i < n.el.size(); i++) {
            gen("popq", "%rdx");
            stackSpace -= 8;
        }
        if (aligned) {
            gen("popq", "%rdx");
            stackSpace -= 8;
            aligned = false;
        }
        gen("popq", "%rdi");
        stackSpace -= 8;
    }

    public void visit(IntegerLiteral n) {
        gen("movq", n.i, "%rax");
    }

    public void visit(True n) {
        gen("movq", 1, "%rax");
    }

    public void visit(False n) {
        gen("movq", 0, "%rax");
    }

    public void visit(IdentifierExp n) {
        MethodSymbolTable lst = gst.classTables.get(classScope).methodTables.get(methodScope);
        if (lst.vars.containsKey(n.s)) { // Check for local var
            int offset = lst.offsets.get(n.s);
            gen("movq", "-" + (8+8*offset) + "(%rbp)", "%rax");
            return;
        }else if(lst.params.containsKey(n.s)){ // Check for method param
            int offset = lst.offsets.get(n.s);
            gen("movq", (16 + 8*offset) + "(%rbp)", "%rax");
            return;
        }
        // Must be class field
        ClassType ct = gst.classTypes.get(classScope);
        while (ct.type != null) {
            if (gst.classTables.get(ct.type).fields.containsKey(n.s)) {
                int offset = gst.classTables.get(ct.type).fieldOffsets.get(n.s);
                gen("movq",  (8 + 8*offset) + "(%rdi)", "%rax");
                return;
            }
            ct = gst.classTypes.get(ct.superType);
        }
    }

    public void visit(This n) {
        // This always in %rdi
        gen("movq", "%rdi", "%rax");
    }

    public void visit(NewArray n) {
        n.e.accept(this);
        gen("pushq", "%rdi");
        stackSpace += 8;
        gen("pushq", "%rax");
        stackSpace += 8;
        gen("addq", 1, "%rax");
        gen("imulq", 8, "%rax");
        gen("movq", "%rax", "%rdi");
        // Space needed in %rdi
        spaceAllocGen();
        gen("popq", "%rdx");
        stackSpace -= 8;
        gen("popq", "%rdi");
        stackSpace -= 8;
        gen("movq", "%rdx", "0(%rax)");
    }

    public void visit(NewObject n) {
        ClassType ct = gst.classTypes.get(n.i.s);
        int vars = numFields(ct.type);
        gen("pushq", "%rdi");
        stackSpace += 8;
        gen("movq", 8 + 8 * vars, "%rdi");
        // Space needed in %rdi
        spaceAllocGen();
        gen("popq", "%rdi");
        stackSpace -= 8;
        gen("leaq", n.i.s + "$$(%rip)", "%rdx");
        gen("movq", "%rdx", "0(%rax)");
    }

    private void spaceAllocGen() {
        if (stackSpace % 16 != 0) {
            gen("pushq", "%rax");
            stackSpace += 8;
            aligned = true;
        }
        gen("call", "mjcalloc");
        if (aligned) {
            gen("popq", "%rdx");
            stackSpace -= 8;
            aligned = false;
        }
    }

    private int numFields(String className) {
        ClassType ct = gst.classTypes.get(className);
        if (ct.superType == null) {
            return gst.classTables.get(className).fields.size();
        }
        return gst.classTables.get(className).fields.size() + numFields(ct.superType);
    }

    public void visit(Not n) {
        n.e.accept(this);
        gen("cmpq", 1, "%rax");

        labels.put("notFalse", labels.getOrDefault("notFalse", 0) + 1);
        String nf = "notFalse" + labels.get("notFalse");
        gen("je", nf);
        endLabelGen(nf);
    }

    public void visit(Identifier n) {}

    private void gen(String s) {
        sb.append(s).append("\n");
    }

    private void gen(String instruction, String i1, String i2) {
        sb.append("\t").append(instruction).append(" ").append(i1).append(",").append(i2).append("\n");
    }

    private void gen(String instruction, int n, String i) {
        sb.append("\t").append(instruction).append(" $").append(n).append(",").append(i).append("\n");
    }

    private void gen(String instruction, String i) {
        sb.append("\t").append(instruction).append(" ").append(i).append("\n");
    }
}
