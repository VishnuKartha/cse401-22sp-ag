
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

    private String currClassName;

    private String currMethodName;

    private final GlobalSymbolTable gst;

    private int stackSize;

    private boolean aligned;

    private final Map<String, Integer> labels;

    private final StringBuilder sb;

    private final StringBuilder vt;

    public CodeGenVisitor(GlobalSymbolTable gst) {
        this.gst = gst;
        stackSize = 0;
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
        gen("\t.globl _asm_main");
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            n.cl.get(i).accept(this);
        }
    }

    public void visit(MainClass n) {
        gen("_asm_main:");
        gen("pushq", "%rbp");
        gen("movq", "%rsp", "%rbp");
        n.s.accept(this);
        gen("movq", "%rbp", "%rsp");
        gen("popq", "%rbp");
        gen("ret", "");
        vt.append(n.i1.s).append("$$: .quad 0\n");
    }

    public void visit(ClassDeclSimple n) {
        currClassName = n.i.s;
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.get(i).accept(this);
        }
        vt.append(n.i.s).append("$$: .quad 0\n");
        for (int i = 0; i < n.ml.size(); i++) {
            vt.append("\t\t.quad ").append(currClassName).append("$").append(n.ml.get(i).i.s).append("\n");
        }
        currClassName = "";
    }

    public void visit(ClassDeclExtends n) {
        currClassName = n.i.s;
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
        currClassName = "";
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
        currMethodName = n.i.s;
        gen(currClassName + "$" + currMethodName + ":");
        gen("pushq", "%rbp");
        gen("movq", "%rsp", "%rbp");
        if (n.vl.size() > 0) {
            gen("subq", 8 * n.vl.size(), "%rsp");
        }
        stackSize += 8*n.vl.size();
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.get(i).accept(this);
        }
        n.e.accept(this);
        gen("movq", "%rbp", "%rsp");
        stackSize -= 8*n.vl.size();
        gen("popq", "%rbp");
        gen("ret", "");
        currMethodName = "";
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
        if (!labels.containsKey("else")) {
            labels.put("else", 0);
        }
        labels.put("else", labels.get("else") + 1);
        elseLabel = "else" + labels.get("else");
        gen("jne", elseLabel);
        n.s1.accept(this);
        String doneLabel;
        if (!labels.containsKey("done")) {
            labels.put("done", 0);
        }
        labels.put("done", labels.get("done") + 1);
        doneLabel = "done" + labels.get("done");
        gen("jmp", doneLabel);
        gen(elseLabel + ":");
        n.s2.accept(this);
        gen(doneLabel + ":");
    }
    public void visit(While n) {
        String whileLabel;
        if (!labels.containsKey("while")) {
            labels.put("while", 0);
        }
        labels.put("while", labels.get("while") + 1);
        whileLabel = "while" + labels.get("while");
        gen(whileLabel + ":");
        n.e.accept(this);
        gen("cmpq", 1, "%rax");
        String doneLabel;
        if (!labels.containsKey("done")) {
            labels.put("done", 0);
        }
        labels.put("done", labels.get("done") + 1);
        doneLabel = "done" + labels.get("done");
        gen("jne", doneLabel);
        n.s.accept(this);
        gen("jmp", whileLabel);
        gen(doneLabel + ":");
    }

    public void visit(Print n) {
        n.e.accept(this);
        gen("pushq", "%rdi");
        stackSize += 8;
        gen("movq", "%rax", "%rdi");
        if (stackSize % 16 != 0) {
            gen("pushq", "%rax");
            stackSize += 8;
            aligned = true;
        }
        gen("call", "_put");
        if (aligned) {
            gen("popq", "%rdx");
            stackSize -= 8;
            aligned = false;
        }
        gen("popq", "%rdi");
        stackSize -= 8;
    }

    public void visit(Assign n) {
        n.e.accept(this);
        MethodSymbolTable lst = gst.classTables.get(currClassName).methodTables.get(currMethodName);
        if (lst.vars.containsKey(n.i.s)) {
            int offset = lst.offsets.get(n.i.s);
            gen("movq", "%rax", "-" + (8 + 8*offset) + "(%rbp)");
            return;
        }else if(lst.params.containsKey(n.i.s)){
            int offset = lst.offsets.get(n.i.s);
            gen("movq", "%rax", (16 + 8*offset) + "(%rbp)");
            return;
        }
        ClassSymbolTable cst = gst.classTables.get(currClassName);
        if (cst.fields.containsKey(n.i.s)) {
            int offset = cst.fieldOffsets.get(n.i.s);
            gen("movq", "%rax", "-" + (8 + 8*offset) + "(%rdi)");
            return;
        }
        ClassType ct = gst.classTypes.get(currClassName);
        while (ct.superType != null) {
            if (gst.classTables.get(ct.superType).fields.containsKey(n.i.s)) {
                int offset = gst.classTables.get(ct.superType).fieldOffsets.get(n.i.s);
                gen("movq", "%rax", "-" + (8 + 8*offset) + "(%rdi)");
                return;
            }
            ct = gst.classTypes.get(ct.superType);
        }
    }

    public void visit(ArrayAssign n) {
        n.e1.accept(this);
        gen("pushq", "%rax");
        stackSize += 8;
        n.e2.accept(this);
        gen("popq", "%rdx");
        stackSize -= 8;
        MethodSymbolTable lst = gst.classTables.get(currClassName).methodTables.get(currMethodName);
        if (lst.vars.containsKey(n.i.s)) {
            int offset = lst.offsets.get(n.i.s);
            gen("movq", "-" + (8 + 8*offset) + "(%rbp)", "%rcx");
        }else if(lst.params.containsKey(n.i.s)){
            int offset = lst.offsets.get(n.i.s);
            gen("movq", (16 + 8*offset) + "(%rbp)", "%rcx");
        }else {
            ClassSymbolTable cst = gst.classTables.get(currClassName);
            if (cst.fields.containsKey(n.i.s)) {
                int offset = cst.fieldOffsets.get(n.i.s);
                gen("movq",  "-" + (8 + 8*offset) + "(%rdi)", "%rcx");
            } else {
                ClassType ct = gst.classTypes.get(currClassName);
                while (ct.superType != null) {
                    if (gst.classTables.get(ct.superType).fields.containsKey(n.i.s)) {
                        int offset = gst.classTables.get(ct.superType).fieldOffsets.get(n.i.s);
                        gen("movq", "-" + (8 + 8*offset) + "(%rdi)", "%rcx");
                        break;
                    }
                    ct = gst.classTypes.get(ct.superType);
                }
            }
        }
        gen("cmpq", "%rdx", "0(%rcx)");
        String arrayIndexOutOfBoundsLabel;
        if (!labels.containsKey("arrayIndexOutOfBounds")) {
            labels.put("arrayIndexOutOfBounds", 0);
        }
        labels.put("arrayIndexOutOfBounds", labels.get("arrayIndexOutOfBounds") + 1);
        arrayIndexOutOfBoundsLabel = "arrayIndexOutOfBounds" + labels.get("arrayIndexOutOfBounds");
        gen("jng", arrayIndexOutOfBoundsLabel);
        gen("cmpq", 0, "%rdx");
        gen("jl", arrayIndexOutOfBoundsLabel);
        gen("movq", "%rax", "8(%rcx,%rdx,8)");
        String doneLabel;
        if (!labels.containsKey("done")) {
            labels.put("done", 0);
        }
        labels.put("done", labels.get("done") + 1);
        doneLabel = "done" + labels.get("done");
        gen("jmp", doneLabel);
        gen(arrayIndexOutOfBoundsLabel + ":");
        if (stackSize % 16 != 0) {
            gen("pushq", "%rax");
            stackSize += 8;
            aligned = true;
        }
        gen("call", "_mjerror");
        if (aligned) {
            gen("popq", "%rdx");
            stackSize -= 8;
            aligned = false;
        }
        gen(doneLabel + ":");
    }

    public void visit(And n) {
        n.e1.accept(this);
        gen("cmpq", 1, "%rax");
        String setAndFalseLabel;
        if (!labels.containsKey("setAndFalse")) {
            labels.put("setAndFalse", 0);
        }
        labels.put("setAndFalse", labels.get("setAndFalse") + 1);
        setAndFalseLabel = "setAndFalse" + labels.get("setAndFalse");
        gen("jne", setAndFalseLabel);
        n.e2.accept(this);
        gen("cmpq", 1, "%rax");
        gen("jne", setAndFalseLabel);
        gen("movq", 1, "%rax");
        String doneLabel;
        if (!labels.containsKey("done")) {
            labels.put("done", 0);
        }
        labels.put("done", labels.get("done") + 1);
        doneLabel = "done" + labels.get("done");
        gen("jmp", doneLabel);
        gen(setAndFalseLabel + ":");
        gen("movq", 0, "%rax");
        gen(doneLabel + ":");
    }

    public void visit(LessThan n) {
        n.e1.accept(this);
        gen("pushq", "%rax");
        stackSize += 8;
        n.e2.accept(this);
        gen("popq", "%rdx");
        stackSize -= 8;
        gen("cmpq", "%rdx", "%rax");
        String setLessFalseLabel;
        if (!labels.containsKey("setLessFalse")) {
            labels.put("setLessFalse", 0);
        }
        labels.put("setLessFalse", labels.get("setLessFalse") + 1);
        setLessFalseLabel = "setLessFalse" + labels.get("setLessFalse");
        gen("jng", setLessFalseLabel);
        gen("movq", 1, "%rax");
        String doneLabel;
        if (!labels.containsKey("done")) {
            labels.put("done", 0);
        }
        labels.put("done", labels.get("done") + 1);
        doneLabel = "done" + labels.get("done");
        gen("jmp", doneLabel);
        gen(setLessFalseLabel + ":");
        gen("movq", 0, "%rax");
        gen(doneLabel + ":");
    }

    public void visit(Plus n) {
        n.e1.accept(this);
        gen("pushq", "%rax");
        stackSize += 8;
        n.e2.accept(this);
        gen("popq", "%rdx");
        stackSize -= 8;
        gen("addq", "%rdx", "%rax");
    }

    public void visit(Minus n) {
        n.e2.accept(this);
        gen("pushq", "%rax");
        stackSize += 8;
        n.e1.accept(this);
        gen("popq", "%rdx");
        stackSize -= 8;
        gen("subq", "%rdx", "%rax");
    }

    public void visit(Times n) {
        n.e1.accept(this);
        gen("pushq", "%rax");
        stackSize += 8;
        n.e2.accept(this);
        gen("popq", "%rdx");
        stackSize -= 8;
        gen("imulq", "%rdx", "%rax");
    }

    public void visit(ArrayLookup n) {
        n.e2.accept(this);
        gen("pushq", "%rax");
        stackSize += 8;
        n.e1.accept(this);
        gen("popq", "%rdx");
        stackSize -= 8;
        gen("cmpq", "%rdx", "0(%rax)");
        String arrayIndexOutOfBoundsLabel;
        if (!labels.containsKey("arrayIndexOutOfBounds")) {
            labels.put("arrayIndexOutOfBounds", 0);
        }
        labels.put("arrayIndexOutOfBounds", labels.get("arrayIndexOutOfBounds") + 1);
        arrayIndexOutOfBoundsLabel = "arrayIndexOutOfBounds" + labels.get("arrayIndexOutOfBounds");
        gen("jng", arrayIndexOutOfBoundsLabel);
        gen("cmpq", 0, "%rdx");
        gen("jl", arrayIndexOutOfBoundsLabel);
        gen("movq", "8(%rax,%rdx,8)", "%rax");
        String doneLabel;
        if (!labels.containsKey("done")) {
            labels.put("done", 0);
        }
        labels.put("done", labels.get("done") + 1);
        doneLabel = "done" + labels.get("done");
        gen("jmp", doneLabel);
        gen(arrayIndexOutOfBoundsLabel + ":");
        if (stackSize % 16 != 0) {
            gen("pushq", "%rax");
            stackSize += 8;
            aligned = true;
        }
        gen("call", "_mjerror");
        if (aligned) {
            gen("popq", "%rdx");
            stackSize -= 8;
            aligned = false;
        }
        gen(doneLabel + ":");
    }
    public void visit(ArrayLength n) {
        n.e.accept(this);
        gen("movq", "0(%rax)", "%rax");
    }

    public void visit(Call n) {
        gen("pushq", "%rdi");
        stackSize += 8;
        gen("movq", "%rdi", "%rbx");
        n.e.accept(this);
        gen("movq", "%rax", "%rdi");
        if ((stackSize + 8 * n.el.size()) % 16 != 0) {
            gen("pushq", "%rax");
            stackSize += 8;
            aligned = true;
        }
        for (int i = n.el.size() - 1; i >= 0; i--) {
            n.el.get(i).accept(this);
            if (n.el.get(i) instanceof This) {
                gen("pushq", "%rbx");
            } else {
                gen("pushq", "%rax");
            }
            stackSize += 8;
        }
        gen("movq", "(%rdi)", "%rax");
        ClassType ct = (ClassType) n.e.type;
        String className = ct.type;
        int offset = 0;
        if (gst.classTables.get(className).methods.containsKey(n.i.s)) {
            offset = gst.classTables.get(className).methods.get(n.i.s).offset;
        } else {
            while (ct.superType != null) {
                if (gst.classTables.get(ct.superType).methods.containsKey(n.i.s)) {
                    offset = gst.classTables.get(ct.superType).methods.get(n.i.s).offset;
                    break;
                }
                ct = gst.classTypes.get(ct.superType);
            }
        }
        gen("addq", (8 + 8*offset), "%rax");
        gen("call", "*(%rax)");
        for (int i = 0; i < n.el.size(); i++) {
            gen("popq", "%rdx");
            stackSize -= 8;
        }
        if (aligned) {
            gen("popq", "%rdx");
            stackSize -= 8;
            aligned = false;
        }
        gen("popq", "%rdi");
        stackSize -= 8;
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
        MethodSymbolTable lst = gst.classTables.get(currClassName).methodTables.get(currMethodName);
        if (lst.vars.containsKey(n.s)) {
            int offset = lst.offsets.get(n.s);
            gen("movq", "-" + (8+8*offset) + "(%rbp)", "%rax");
            return;
        }else if(lst.params.containsKey(n.s)){
            System.out.println(n.s);
            int offset = lst.offsets.get(n.s);
            gen("movq", (16 + 8*offset) + "(%rbp)", "%rax");
            return;
        }
        ClassSymbolTable cst = gst.classTables.get(currClassName);
        if (cst.fields.containsKey(n.s)) {
            int offset = cst.fieldOffsets.get(n.s);
            gen("movq",  (8 + 8*offset) + "(%rdi)", "%rax");
            return;
        }
        ClassType ct = gst.classTypes.get(currClassName);
        while (ct.superType != null) {
            if (gst.classTables.get(ct.superType).fields.containsKey(n.s)) {
                int offset = gst.classTables.get(ct.superType).fieldOffsets.get(n.s);
                gen("movq",  (8 + 8*offset) + "(%rdi)", "%rax");
                return;
            }
            ct = gst.classTypes.get(ct.superType);
        }
    }

    public void visit(This n) {
        gen("movq", "%rdi", "%rax");
    }

    public void visit(NewArray n) {
        n.e.accept(this);
        gen("pushq", "%rdi");
        stackSize += 8;
        gen("pushq", "%rax");
        stackSize += 8;
        gen("addq", 1, "%rax");
        gen("imulq", 8, "%rax");
        gen("movq", "%rax", "%rdi");
        if (stackSize % 16 != 0) {
            gen("pushq", "%rax");
            stackSize += 8;
            aligned = true;
        }
        gen("call", "_mjcalloc");
        if (aligned) {
            gen("popq", "%rdx");
            stackSize -= 8;
            aligned = false;
        }
        gen("popq", "%rdx");
        stackSize -= 8;
        gen("popq", "%rdi");
        stackSize -= 8;
        gen("movq", "%rdx", "0(%rax)");
    }

    public void visit(NewObject n) {
        ClassType ct = gst.classTypes.get(n.i.s);
        int vars = getNumFields(ct.type);
        gen("pushq", "%rdi");
        stackSize += 8;
        gen("movq", 8 + 8 * vars, "%rdi");
        if (stackSize % 16 != 0) {
            gen("pushq", "%rax");
            stackSize += 8;
            aligned = true;
        }
        gen("call", "_mjcalloc");
        if (aligned) {
            gen("popq", "%rdx");
            stackSize -= 8;
            aligned = false;
        }
        gen("popq", "%rdi");
        stackSize -= 8;
        gen("leaq", n.i.s + "$$(%rip)", "%rdx");
        gen("movq", "%rdx", "0(%rax)");
    }

    private int getNumFields(String className) {
        ClassType ct = gst.classTypes.get(className);
        if (ct.superType == null) {
            return gst.classTables.get(className).fields.size();
        }
        return gst.classTables.get(className).fields.size() + getNumFields(ct.superType);
    }

    public void visit(Not n) {
        n.e.accept(this);
        gen("cmpq", 1, "%rax");
        String nf;
        if (!labels.containsKey("setNotFalse")) {
            labels.put("setNotFalse", 0);
        }
        labels.put("setNotFalse", labels.get("setNotFalse") + 1);
        nf = "setNotFalse" + labels.get("setNotFalse");
        gen("je", nf);
        gen("movq", 1, "%rax");
        String doneLabel;
        if (!labels.containsKey("done")) {
            labels.put("done", 0);
        }
        labels.put("done", labels.get("done") + 1);
        doneLabel = "done" + labels.get("done");
        gen("jmp", doneLabel);
        gen(nf + ":");
        gen("movq", 0, "%rax");
        gen(doneLabel + ":");
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
