package src;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class DeclCollector extends GJDepthFirst<String, Void> {
    SymbolTable table;
    ArrayList<Class> classes;

    public DeclCollector(ArrayList<Class> classes) {
        this.table = table;
        this.classes = new ArrayList<Class>();
    }

    public printClasses() {
        for (Class c : this.classes)
        { 		      
            System.out.println(c.name); 		
        }
    }

    public boolean inList(String className) {
        for (Class c : this.classes)
        { 		      
            if (c.name == className) {
                return true;
            }	
        }
        return false;
    }

     /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
   public String visit(MainClass n, Void argu) throws Exception {
      String className = n.f1.accept(this, argu);
      Class newClass = new Class(className);
      this.classes.add(newClass);
      System.out.println("MainClass");
      return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public String visit(ClassDeclaration n, Void argu) throws Exception {
      String className = n.f1.accept(this, argu);
      if (inList(className)) {
            throw new Exception("Class already declared");
        }
      Class newClass = new Class(className);
      this.classes.add(newClass);
      System.out.println("ClassDeclaration");

      return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        String parent = n.f3.accept(this, argu);
        if (!inList(parent)) {
            throw new Exception("Parent class is not declared");
        }
        String className = n.f1.accept(this, argu);
        if (inList(className)) {
            throw new Exception("Class already declared");
        }
        Class newClass = new Class(className);
        this.classes.add(newClass);
        System.out.println("ClassExtendsDeclaration");
        return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Void argu) throws Exception {
        String varType = n.f0.accept(this, argu);
        String varName = n.f1.accept(this, argu);
        return null;
   }

   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   public String visit(MethodDeclaration n, Void argu) throws Exception {
        String methodType = n.f1.accept(this, argu);
        String methodName = n.f2.accept(this, argu);
        return null;
   }





    /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Identifier n, Void argu) throws Exception {
      return n.f0.toString();
   }

}