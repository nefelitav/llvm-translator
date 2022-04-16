import syntaxtree.*;
import src.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java [MainClassName] [file1] [file2] ... [fileN]");
            System.exit(1);
        }
        FileInputStream fis = null;
        try {
            ArrayList<src.Class> classes = new ArrayList<src.Class>();
            fis = new FileInputStream(args[0]);

            MiniJavaParser parser = new MiniJavaParser(fis);
            Goal root = parser.Goal();
            System.err.println("Program parsed successfully.");

            // SymbolTable table = new SymbolTable();

            DeclCollector declCollector = new DeclCollector(classes);
            root.accept(declCollector, null);



            // TypeChecker typeChecker = new TypeChecker(table, classes);
            // root.accept(typeChecker, null);

            // table.printData();
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