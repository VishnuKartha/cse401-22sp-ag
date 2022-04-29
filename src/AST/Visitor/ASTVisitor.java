package AST.Visitor;

import AST.*;

import javax.swing.plaf.nimbus.State;

// Sample print visitor from MiniJava web site with small modifications for UW CSE.
// HP 10/11

public class ASTVisitor implements Visitor {

    // Display added for toy example language.  Not used in regular MiniJava
    public void visit(Display n) {
    }

    // MainClass m;
    // ClassDeclList cl;
    public void visit(Program n) {
        System.out.println("Program");
        MainClass m = n.m;
        m.set_depth(1);
        m.accept(this);
        for ( int i = 0; i < n.cl.size(); i++ ) {
            ClassDecl curr_node = n.cl.get(i);
            curr_node.set_depth(1);
            curr_node.accept(this);
        }
    }

    // Identifier i1,i2;
    // Statement s;
    public void visit(MainClass n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("MainClass " + n.i1.toString());
        Identifier i1 = n.i1;
        i1.set_depth(n.ind_depth + 1);
        Identifier i2 = n.i2;
        i2.set_depth(n.ind_depth + 1);
        System.out.println();
        Statement s = n.s;
        s.set_depth(n.ind_depth + 1);
        s.accept(this);
        System.out.println();
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("Class " + n.i.toString());
        for(int i =0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("fields:");
        for (int i = 0; i < n.vl.size(); i++ ) {
            VarDecl vd = n.vl.get(i);
            vd.set_depth(n.ind_depth + 2);
            vd.accept(this);
            System.out.println();
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            MethodDecl md = n.ml.get(i);
            md.set_depth(n.ind_depth + 1);
            n.ml.get(i).accept(this);
            System.out.println();
        }
        System.out.println();
    }

    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("Class " + n.i.toString());
        System.out.println(" extends " + n.j.toString());
        for(int i =0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("fields:");
        for (int i = 0; i < n.vl.size(); i++ ) {
            VarDecl vd = n.vl.get(i);
            vd.set_depth(n.ind_depth + 2);
            vd.accept(this);
            System.out.println();
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            MethodDecl md = n.ml.get(i);
            md.set_depth(n.ind_depth + 1);
            n.ml.get(i).accept(this);
        }
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("MethodDecl " + n.i.toString());
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.print("returns ");
        n.t.accept(this);
        System.out.println();
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("parameters:");
        for ( int i = 0; i < n.fl.size(); i++ ) {
            Formal f = n.fl.get(i);
            f.set_depth(n.ind_depth + 2);
            f.accept(this);
            System.out.println();
        }
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("variables:");
        for ( int i = 0; i < n.vl.size(); i++ ) {
            VarDecl vd = n.vl.get(i);
            vd.set_depth(n.ind_depth + 2);
            n.vl.get(i).accept(this);
            System.out.println();
        }
        for ( int i = 0; i < n.sl.size(); i++ ) {
            Statement s = n.sl.get(i);
            s.set_depth(n.ind_depth + 1);
            s.accept(this);
        }
        System.out.println();
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.print("Return ");
        n.e.accept(this);
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
    }

    public void visit(IntArrayType n) {
        System.out.print("int[]");
    }

    public void visit(BooleanType n) {
        System.out.print("boolean");
    }

    public void visit(IntegerType n) {
        System.out.print("int");
    }

    // String s;
    public void visit(IdentifierType n) {
        System.out.print(n.s);
    }

    // StatementList sl;
    public void visit(Block n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        for ( int i = 0; i < n.sl.size(); i++ ) {
            Statement s = n.sl.get(i);
            s.set_depth(n.ind_depth + 1);
            s.accept(this);
            System.out.println();
        }

    }

    // Exp e;
    // Statement s1,s2;
    public void visit(If n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("if ");
        n.e.accept(this);
        Statement s1 = n.s1;
        s1.set_depth(n.ind_depth + 1);
        System.out.println();
        s1.accept(this);
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("else ");
        Statement s2 = n.s2;
        s2.set_depth(n.ind_depth + 1);
        System.out.println();
        s2.accept(this);
    }

    // Exp e;
    // Statement s;
    public void visit(While n) {
        System.out.print("while ");
        n.e.accept(this);
        System.out.print("");
        n.s.accept(this);
    }

    // Exp e;
    public void visit(Print n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("Print ");
        Exp e = n.e;
        e.set_depth(n.ind_depth + 1);
        e.accept(this);
    }

    // Identifier i;
    // Exp e;
    public void visit(Assign n) {
        n.i.accept(this);
        System.out.print(" = ");
        n.e.accept(this);
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {
        n.i.accept(this);
        System.out.print("[");
        n.e1.accept(this);
        System.out.print("] = ");
        n.e2.accept(this);
        System.out.print(";");
    }

    // Exp e1,e2;
    public void visit(And n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" && ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(LessThan n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" < ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(Plus n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" + ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(Minus n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" - ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(Times n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" * ");
        n.e2.accept(this);
        System.out.print(")");
    }

    // Exp e1,e2;
    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        System.out.print("[");
        n.e2.accept(this);
        System.out.print("]");
    }

    // Exp e;
    public void visit(ArrayLength n) {
        n.e.accept(this);
        System.out.print(".length");
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        n.e.accept(this);
        System.out.print(".");
        n.i.accept(this);
        System.out.print("(");
        for ( int i = 0; i < n.el.size(); i++ ) {
            n.el.get(i).accept(this);
            if ( i+1 < n.el.size() ) { System.out.print(", "); }
        }
        System.out.print(")");
    }

    // int i;
    public void visit(IntegerLiteral n) {
        System.out.print(n.i);
    }

    public void visit(True n) {
        System.out.print("true");
    }

    public void visit(False n) {
        System.out.print("false");
    }

    // String s;
    public void visit(IdentifierExp n) {
        System.out.print(n.s);
    }

    public void visit(This n) {
        System.out.print("this");
    }

    // Exp e;
    public void visit(NewArray n) {
        System.out.print("new int [");
        n.e.accept(this);
        System.out.print("]");
    }

    // Identifier i;
    public void visit(NewObject n) {
        System.out.print("new ");
        System.out.print(n.i.s);
        System.out.print("()");
    }

    // Exp e;
    public void visit(Not n) {
        System.out.print("!");
        n.e.accept(this);
    }

    // String s;
    public void visit(Identifier n) {
        System.out.print(n.s);
    }
}
