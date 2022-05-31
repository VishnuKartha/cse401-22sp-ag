import AST.*;
import AST.Visitor.Visitor;
import Semantics.SymbolTables.ClassSymbolTable;
import Semantics.SymbolTables.GlobalSymbolTable;
import Semantics.SymbolTables.MethodSymbolTable;
import Types.ClassType;
import Types.MethodType;
import Types.MiniJavaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CodeGenVisitor implements Visitor {

    private StringBuilder sb;
    private StringBuilder vt;
    private GlobalSymbolTable gT;
    private String classScope;
    private String methodScope;
    private Map<String,Integer> labelsUsed;
    private int stackVariables;

    boolean aligned;


    public CodeGenVisitor(GlobalSymbolTable gst){
        gT = gst;
        sb = new StringBuilder();
        vt = new StringBuilder();
        vt.append("\t\t.data\n");
        this.labelsUsed = new HashMap<>();
        stackVariables = 0;
    }

    public String getCodeGen(){
        return sb.toString() + vt.toString();
    }

    @Override
    public void visit(Program n) {
        sb.append("\t").append(".text").append("\n");
        sb.append("\t").append(".globl _asm_main").append("\n");
        sb.append("\n");
        for(String c : gT.classTypes.keySet()){
            ClassType ct = gT.classTypes.get(c);
            if(ct.superType == null){
                extendedOffsets(ct.type, gT.classTables.get(c).fields.size(), gT.classTables.get(c).methods.size());
            }
        }
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
        vt.append(n.i1.s).append("$$:\t.quad 0\n\n");
    }

    @Override
    public void visit(ClassDeclSimple n) {
        classScope = n.i.s;
        for(int i =0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }

        vt.append(n.i.s).append("$$:\t.quad 0\n");
        for(int i = 0; i < n.ml.size(); i++){
            vt.append("\t\t.quad ").append(n.i.s).append("$").append(n.ml.get(i).i.s).append("\n");
        }
        classScope = null;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        classScope = n.i.s;
        vt.append(n.i.s).append("$$:\t.quad ").append(n.j.s).append("$$\n");
        for(int i =0; i < n.ml.size(); i++){
            n.ml.get(i).accept(this);
        }

        ClassType ct = gT.classTypes.get(n.i.s);
        HashMap<Integer, String> methodOffsets = new HashMap<>();

        while(ct != null){
            ClassSymbolTable cst = gT.classTables.get(ct.type);
            for(String m : cst.methods.keySet()){
                int offset = cst.methods.get(m).offset;
                if(!methodOffsets.containsKey(offset)){
                    methodOffsets.put(offset, "\t\t.quad " + ct.type + "$" + m + "\n");
                }
            }
            ct = gT.classTypes.get(ct.superType);
        }

        int[] sortedOffsets = new int[methodOffsets.keySet().size()];
        int i = 0;
        for(int x : methodOffsets.keySet()){
            sortedOffsets[i] = x;
            i++;
        }
        Arrays.sort(sortedOffsets);
        for (int sortedOffset : sortedOffsets) {
            vt.append(methodOffsets.get(sortedOffset));
        }
        classScope = null;

    }

    private void extendedOffsets(String b, int fo, int mo){
        for(String c : gT.classTypes.keySet()){
            ClassType cta = gT.classTypes.get(c);
            if(cta.superType != null && cta.superType.equals(b)){
                ClassSymbolTable ct = gT.classTables.get(c);
                for(String f : ct.fields.keySet()){
                    ct.fields.get(f).offset = fo;
                    fo++;
                }
                for(String m : ct.methods.keySet()){
                    ClassType ctb = gT.classTypes.get(cta.type);
                    while(ctb.superType != null){
                        ctb = gT.classTypes.get(ctb.superType);
                        ClassSymbolTable csb = gT.classTables.get(ctb.type);
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
        stackVariables = 0;

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
            // Class field
            id = gT.classTables.get(classScope).fields.get(n.i.s);
            gen("movq", "%rax", (8 + 8 * id.offset) + "(%rdi)");
        }


    }

    @Override
    public void visit(ArrayAssign n) {
        // the offset of the variable

        MiniJavaType id;

        String successfullBoundsCheck = generateLabel("InBounds");
        String unsuccessfullBoundsCheck = generateLabel("OutofBounds");
        String endArrayLookUp = generateLabel("endArrayLookUp");

        n.e1.accept(this); // index in rax
        stackVariables++;
        gen("pushq", "%rax"); // index on stack
        n.e2.accept(this); // value in rax
        gen("popq", "%rdx"); // Index in rcx
        stackVariables--;
        gen("cmpq","0","%rcx");

        gen("jl",unsuccessfullBoundsCheck);
        stackVariables--;

        MethodSymbolTable mt =  gT.classTables.get(classScope).methodTables.get(methodScope);
        if(mt.vars.containsKey(n.i.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).vars.get(n.i.s);
            gen("movq",  "-" + (8 + 8*id.offset) + "(%rbp)", "%rcx");
        }else if(mt.params.containsKey(n.i.s)){
            id = gT.classTables.get(classScope).methodTables.get(methodScope).params.get(n.i.s);
            gen("movq", (16 + 8 * id.offset) + "(%rbp)", "%rcx");
        }else{
            // Class field
            ClassSymbolTable ct = gT.classTables.get(classScope);
            if(ct.fields.containsKey(n.i.s)){
                int offset = ct.fields.get(n.i.s).offset;
                gen("movq", (8 + 8*offset) + "(%rdi)", "rcx");
            }else{
                ClassType cst = gT.classTypes.get(classScope);
                while(cst.superType != null){
                    if(gT.classTables.get(cst.superType).fields.containsKey(n.i.s)){
                        int offset = gT.classTables.get(cst.superType).fields.get(n.i.s).offset;
                        gen("movq", (8 + 8*offset) + "(%rdi)", "rcx");
                        break;
                    }
                    cst = gT.classTypes.get(cst.superType);
                }
            }
            id = gT.classTables.get(classScope).fields.get(n.i.s);
            gen("movq", "-" + (8 + 8*id.offset) + "(%rdi)", "%rdx");
        }

        gen("cmpq","%rdx","0(%rcx)");
        gen("jle",unsuccessfullBoundsCheck);
        gen("movq","%rax",  "8(%rcx,%rdx,8)" );
        gen("jmp",successfullBoundsCheck);
        gen(unsuccessfullBoundsCheck+":");
        if(stackVariables %16 != 0){
            gen("pushq", "%rax");
            stackVariables++;
            aligned = true;
        }
        gen("call","_mjerror");
        if(aligned){
            gen("popq", "%rdx");
            stackVariables--;
            aligned = false;
        }
        gen(successfullBoundsCheck+":");

        gen(endArrayLookUp+":");

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
//        A[i]

        n.e1.accept(this);
        gen("pushq", "%rax");
        stackVariables++;
        n.e2.accept(this); // rax has the index of the array
        gen("popq", "%rdx"); // rdx has the address of array
        stackVariables--;
        gen("cmpq","0","%rax");
        gen("jl",unsuccessfullBoundsCheck);
        gen("cmpq","%rax","(%rdx)");
        gen("jle",unsuccessfullBoundsCheck);
        gen("jmp",successfullBoundsCheck);
        gen(unsuccessfullBoundsCheck+":");
        gen("call","_mjerror");

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
        gen("pushq", "%rdi");
        gen("addq",1, "%rax"); // gets space to hold the length of the array information
        gen("imulq" , 8, "%rax"); // 8 bytes per element
        gen("movq", "%rax", "%rdi");
        gen("pushq", "%rax"); // save the array len
        stackVariables+=2;
        if(stackVariables%2 != 0){
            stackVariables++;
            gen("subq", "$8", "%rsp");
            aligned = true;

        }
        gen("call","_mjcalloc"); // address of allocated state stored in %rax
        if(aligned){
            gen("popq", "%rdx");
            stackVariables--;
            aligned = false;
        }

        gen("popq", "%rdx"); // get the array len information into rdx
        gen("popq", "%rdi");
        stackVariables-=2;
        gen("movq", "%rdx", "0(%rax)"); // store  the length of the array in first 8 bytes of array space
    }

    @Override
    public void visit(NewObject n) {
        int num_vars = gT.classTables.get(n.i.s).fields.size();
        gen("movq",(8 + 8*num_vars),"%rdi");

        if(stackVariables%2 != 0){
            stackVariables++;
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
