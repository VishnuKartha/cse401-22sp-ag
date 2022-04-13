import Parser.sym;
import Scanner.scanner;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

import java.io.*;
import java.util.Objects;

public class MiniJava {

    public static void main(String[] args) {
        if(Objects.equals(args[0], "-S")){
            try {
                // create a scanner on the input file
                ComplexSymbolFactory sf = new ComplexSymbolFactory();
                Reader in = new BufferedReader(new FileReader(args[1]));
                scanner s = new scanner(in, sf);
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
        }

    }
}
