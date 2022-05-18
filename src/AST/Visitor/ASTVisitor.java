package AST.Visitor;

import AST.*;

import javax.swing.plaf.nimbus.State;
import java.beans.Expression;

// Sample print visitor from MiniJava web site with small modifications for UW CSE.
// HP 10/11

public class ASTVisitor implements Visitor {

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
        System.out.print("MainClass " + n.i1.toString() + " (line " + n.line_number + ")");
        Identifier i1 = n.i1;
        i1.set_depth(n.ind_depth + 1);
        Identifier i2 = n.i2;
        i2.set_depth(n.ind_depth + 1);
        System.out.println();
        Statement s = n.s;
        s.set_depth(n.ind_depth + 1);
        s.accept(this);
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("Class " + n.i.toString() + " (line " + n.line_number + ")");
        for(int i =0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("fields:");
        for (int i = 0; i < n.vl.size(); i++ ) {
            VarDecl vd = n.vl.get(i);
            vd.set_depth(n.ind_depth + 2);
            vd.accept(this);
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            MethodDecl md = n.ml.get(i);
            md.set_depth(n.ind_depth + 1);
            n.ml.get(i).accept(this);
        }
    }

    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("Class " + n.i.toString());
        System.out.println(" extends " + n.j.toString() + " (line " + n.line_number + ")");
        for(int i =0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("fields:");
        for (int i = 0; i < n.vl.size(); i++ ) {
            VarDecl vd = n.vl.get(i);
            vd.set_depth(n.ind_depth + 2);
            vd.accept(this);
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
        System.out.print(" " + n.i.s);
        System.out.println(" (line" + n.line_number + ")");
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("MethodDecl " + n.i.toString() + " (line" + n.line_number + ")");
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.print("returns ");
        n.t.accept(this);
        System.out.println();
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("params:");
        for ( int i = 0; i < n.fl.size(); i++ ) {
            Formal f = n.fl.get(i);
            f.set_depth(n.ind_depth + 2);
            f.accept(this);
        }
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("variables:");
        for ( int i = 0; i < n.vl.size(); i++ ) {
            VarDecl vd = n.vl.get(i);
            vd.set_depth(n.ind_depth + 2);
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.sl.size(); i++ ) {
            Statement s = n.sl.get(i);
            s.set_depth(n.ind_depth + 1);
            s.accept(this);
        }
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        Exp e = n.e;
        e.set_depth(n.ind_depth + 2);
        System.out.println("Return (line " + e.line_number + ")");
        e.accept(this);
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
        for ( int i = 0; i < n.sl.size(); i++ ) {
            Statement s = n.sl.get(i);
            s.set_depth(n.ind_depth);
            s.accept(this);
        }
    }

    // Exp e;
    // Statement s1,s2;
    public void visit(If n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("if (line " + n.line_number + ")");
        for(int i =0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("condition:");
        Exp e = n.e;
        e.set_depth(n.ind_depth + 1);
        e.accept(this);
        Statement s1 = n.s1;
        s1.set_depth(n.ind_depth + 1);
        s1.accept(this);
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("else");
        Statement s2 = n.s2;
        s2.set_depth(n.ind_depth + 1);
        s2.accept(this);
    }

    // Exp e;
    // Statement s;
    public void visit(While n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("while (line " + n.line_number + ")");
        for(int i =0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("condition:");
        Exp e = n.e;
        e.set_depth(n.ind_depth + 2);
        e.accept(this);
        Statement s = n.s;
        s.set_depth(n.ind_depth + 1);
        s.accept(this);
    }

    // Exp e;
    public void visit(Print n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("Print (line " + n.line_number + ")");
        Exp e = n.e;
        e.set_depth(n.ind_depth + 1);
        e.accept(this);
    }

    // Identifier i;
    // Exp e;
    public void visit(Assign n) {
        for(int i =0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("= (line " + n.line_number + ")");
        Identifier i = n.i;
        i.set_depth(n.ind_depth + 1);
        i.accept(this);
        Exp e = n.e;
        e.set_depth(n.ind_depth + 1);
        e.accept(this);
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("= (line " + n.line_number + ")");
        Identifier id = n.i;
        id.set_depth(n.ind_depth + 1);
        id.accept(this);
        for(int i = 0; i < n.ind_depth + 2; i++) System.out.print("  ");
        System.out.println("index");
        Exp e1 = n.e1;
        e1.set_depth(n.ind_depth + 3);
        e1.accept(this);
        Exp e2 = n.e2;
        e2.set_depth(n.ind_depth + 1);
        e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(And n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("&&");
        Exp e1 = n.e1;
        e1.set_depth(n.ind_depth + 1);
        e1.accept(this);
        Exp e2 = n.e2;
        e2.set_depth(n.ind_depth + 1);
        e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(LessThan n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("<");
        Exp e1 = n.e1;
        e1.set_depth(n.ind_depth + 1);
        e1.accept(this);
        Exp e2 = n.e2;
        e2.set_depth(n.ind_depth + 1);
        e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(Plus n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("+");
        Exp e1 = n.e1;
        e1.set_depth(n.ind_depth + 1);
        e1.accept(this);
        Exp e2 = n.e2;
        e2.set_depth(n.ind_depth + 1);
        e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(Minus n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("-");
        Exp e1 = n.e1;
        e1.set_depth(n.ind_depth + 1);
        e1.accept(this);
        Exp e2 = n.e2;
        e2.set_depth(n.ind_depth + 1);
        e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(Times n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("*");
        Exp e1 = n.e1;
        e1.set_depth(n.ind_depth + 1);
        e1.accept(this);
        Exp e2 = n.e2;
        e2.set_depth(n.ind_depth + 1);
        e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(ArrayLookup n) {
        Exp e1 = n.e1;
        e1.set_depth(n.ind_depth + 1);
        e1.accept(this);
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("index");
        Exp e2 = n.e2;
        e2.set_depth(n.ind_depth + 2);
        e2.accept(this);
    }

    // Exp e;
    public void visit(ArrayLength n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("length of");
        Exp e = n.e;
        e.set_depth(n.ind_depth + 1);
        e.accept(this);
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n) {
        Exp e = n.e;
        e.set_depth(n.ind_depth);
        e.accept(this);
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("call " + n.i.toString());
        for(int i = 0; i < n.ind_depth + 2; i++) System.out.print("  ");
        System.out.println("args:");
        for ( int i = 0; i < n.el.size(); i++ ) {
            e = n.el.get(i);
            e.set_depth(n.ind_depth+3);
            e.accept(this);
        }
    }

    // int i;
    public void visit(IntegerLiteral n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println(n.i);
    }

    public void visit(True n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("true");
    }

    public void visit(False n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("false");
    }

    // String s;
    public void visit(IdentifierExp n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println(n.s);
    }

    public void visit(This n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("this");
    }

    // Exp e;
    public void visit(NewArray n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("new int[]");
        for(int i = 0; i < n.ind_depth + 1; i++) System.out.print("  ");
        System.out.println("size");
        Exp e = n.e;
        e.set_depth(n.ind_depth + 1);
        e.accept(this);
    }

    // Identifier i;
    public void visit(NewObject n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.print("new ");
        System.out.println(n.i.s);
    }

    // Exp e;
    public void visit(Not n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println("!");
        Exp e = n.e;
        e.set_depth(n.ind_depth + 1);
        e.accept(this);
    }

    // String s;
    public void visit(Identifier n) {
        for(int i = 0; i < n.ind_depth; i++) System.out.print("  ");
        System.out.println(n.s);
    }
}
