import AST.Program;
import AST.Visitor.ASTVisitor;
import Parser.parser;
import Scanner.*;
import java.io.*;
import java.util.*;

import Parser.sym;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
    This class shows one way to use JUnit for testing your compiler.

    NOTE: The single provided test case here is not designed for MiniJava!
    Use this as a starting point, but you will want to create tests
    that fit the MiniJava grammar and match whatever output format you choose
    (e.g. your chosen token names, parse table formats, etc).
    In later phases of the project, you may find it helpful to write test
    cases for Minijava.java itself rather than the underlying modules as is
    shown here.
*/
public class TestParser {

    public static final String TEST_FILES_LOCATION = "test/resources/MiniJavaParser/";
    public static final String TEST_FILES_INPUT_EXTENSION = ".java";
    public static final String TEST_FILES_EXPECTED_EXTENSION = ".expected";

    private final ByteArrayOutputStream newOut = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    /*
        You may be able to reuse this private helper method for your own
        testing of the MiniJava scanner.
    */

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(newOut));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private void runScannerTestCase(String testCaseName) {
        try {
            FileInputStream input = new FileInputStream(TEST_FILES_LOCATION + testCaseName + TEST_FILES_INPUT_EXTENSION);
            String expected = Files.readString(Paths.get(TEST_FILES_LOCATION, testCaseName + TEST_FILES_EXPECTED_EXTENSION), Charset.defaultCharset());

            ComplexSymbolFactory sf = new ComplexSymbolFactory();
            Reader in = new BufferedReader(new InputStreamReader(input));
            scanner s = new scanner(in, sf);
            parser p = new parser(s, sf);
            Symbol root;
            root = p.parse();
            Program program = (Program) root.value;
            program.accept(new ASTVisitor());
            assertEquals(expected,newOut.toString());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testBasic() {
        runScannerTestCase("Basic");
    }

    @Test
    public void testIncorrect() {
        runScannerTestCase("Incorrect");
    }

}