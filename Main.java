import syntaxtree.*;
import src.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(args[0]);
            MiniJavaParser parser = new MiniJavaParser(fis);
            Goal root = parser.Goal();
            System.err.println("Program parsed successfully.");
            Visitor eval = new Visitor();
            root.accept(eval, null);
        }
        catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        finally {
            try {
                if (fis != null) fis.close();
            }
            catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}


