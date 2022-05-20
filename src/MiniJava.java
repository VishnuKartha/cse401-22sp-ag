import AST.Program;
import AST.Visitor.ASTVisitor;
import AST.Visitor.PrettyPrintVisitor;
import Parser.parser;
import Parser.sym;
import Scanner.scanner;
import Semantics.SymbolTables.GlobalSymbolTable;
import Semantics.TableBuilderVisitors.ClassTableBuilder;
import Semantics.TableBuilderVisitors.GlobalTableBuilder;
import Semantics.TableBuilderVisitors.MethodTableBuilder;
import Semantics.TypeChecker;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

import java.io.*;
import java.util.Objects;

public class MiniJava {

    public static void main(String[] args) throws FileNotFoundException {
        ComplexSymbolFactory sf = new ComplexSymbolFactory();
        Reader in = new BufferedReader(new FileReader(args[1]));
        scanner s = new scanner(in, sf);
        if(Objects.equals(args[0], "-S")){
            try {
                // create a scanner on the input file
                Symbol t = s.next_token();
                boolean valid = true;
                while (t.sym != sym.EOF) {
                    // print each token that we scan
                    if(t.sym == sym.error){
                        valid = false;
                    }
                    System.out.print(s.symbolToString(t) + " ");
                    t = s.next_token();
                }
                System.exit(valid ? 0 : 1);
            } catch (Exception e) {
                // yuck: some kind of error in the compiler implementation
                // that we're not expecting (a bug!)
                System.err.println("Unexpected internal compiler error: " +
                        e.toString());
                // print out a stack dump
                e.printStackTrace();
                System.exit(1);
            }
        }else if(Objects.equals(args[0], "-P")){
            try {
                // create a scanner on the input file
                parser p = new parser(s, sf);
                Symbol root;
                root = p.parse();
                @SuppressWarnings("unchecked")
                Program program = (Program) root.value;
                program.accept(new PrettyPrintVisitor());
                System.exit(0);
            } catch (Exception e) {
                System.err.println("Unexpected internal compiler error: " +
                        e.toString());
                e.printStackTrace();
                System.exit(1);
            }
        }else if(Objects.equals(args[0], "-A")){
            try {
                // create a scanner on the input file
                parser p = new parser(s, sf);
                Symbol root;
                root = p.parse();
                @SuppressWarnings("unchecked")
                Program program = (Program) root.value;
                program.accept(new ASTVisitor());
            } catch (Exception e) {
                System.err.println("Unexpected internal compiler error: " +
                        e.toString());
                e.printStackTrace();
            }
        }else if(Objects.equals(args[0], "-T")){
            try {
                // create a scanner on the input file
                parser p = new parser(s, sf);
                Symbol root;
                root = p.parse();
                @SuppressWarnings("unchecked")
                Program program = (Program) root.value;
                boolean error = false;
                GlobalTableBuilder gt = new GlobalTableBuilder();
                program.accept(gt);
                error = gt.errorStatus();
                GlobalSymbolTable gst = gt.getGlobal();
                ClassTableBuilder ct = new ClassTableBuilder(gst);
                program.accept(ct);
                gst = ct.getGlobalTable();
                MethodTableBuilder mt = new MethodTableBuilder(gst);
                program.accept(mt);
                gst = mt.getGlobalTable();
                program.accept(new TypeChecker(gst));
                System.out.println(gst.toString());
                System.exit(error ? 0 : 1);
            } catch (Exception e) {
                System.err.println("Unexpected internal compiler error: " +
                        e.toString());
                e.printStackTrace();
                System.exit(1);
            }
        }

    }
}
