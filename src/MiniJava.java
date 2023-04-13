import Scanner.*;
import Parser.sym;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java.io.*;

public class MiniJava {
    public static void main(String[] args) {
        // args[0] = -S
        // args[1] = "<filename>.java"
        try {
            // create a scanner on the input file
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
            InputStream istream = new FileInputStream(args[1]);
            Reader in = new BufferedReader(new InputStreamReader(istream));
            scanner s = new scanner(in, sf);
            Symbol t = s.next_token();
            while (t.sym != sym.EOF) { 
                // print each token that we scan
                System.out.print(s.symbolToString(t) + " ");
                t = s.next_token();
            }
        } catch (Exception e) {
            // yuck: some kind of error in the compiler implementation
            // that we're not expecting (a bug!)
            System.err.println("Unexpected internal compiler error: " + 
                        e.toString());
            // print out a stack dump
            e.printStackTrace();
        }
    }
}