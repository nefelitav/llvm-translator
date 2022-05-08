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
        for(String file : args) {
            try {

                System.out.println();
                System.out.println("********************************");
                System.out.println(file.split("/")[(file.split("/")).length-1]);
                System.out.println("********************************");
                System.out.println();
                
                fis = new FileInputStream(file);

                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();
                System.err.println("Program parsed successfully.");

                SymbolTable table = new SymbolTable();

                DeclCollector declCollector = new DeclCollector(table);
                try {
                    root.accept(declCollector, null);
                    TypeChecker typeChecker = new TypeChecker(table);
                    root.accept(typeChecker, null);
                    table.printTable();
                } catch(Exception e) {
                    System.out.println();
                    System.out.println(e);
                    System.out.println();
                }
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
}