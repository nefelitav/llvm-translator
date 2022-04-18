package src;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class DeclCollector extends GJDepthFirst<String, Void> {
    SymbolTable table;

    public DeclCollector(SymbolTable table) {
        this.table = table;
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
      this.table.addClass(className, newClass);
      String methodType = "void";
      String methodName = "main";
      String methodParam = "String[] " + n.f11.accept(this, argu);
      String methodVars = "";

      for (int i = 0; i < n.f14.size(); i++) {
        String method = n.f14.elementAt(i).accept(this, argu);
        if (method != null) {
            if (i == 0) {
                methodVars =  method;
            } else {
                methodVars = methodVars + "," + method;
            }
        }
      }
      newClass.addMethod(methodType, methodName, methodParam, methodVars, table);
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
      if (this.table.getClass(className) != null) {
        throw new Exception("Class already declared");
      }
      Class newClass = new Class(className);
      this.table.addClass(className, newClass);

      for (int i = 0; i < n.f3.size(); i++) {
        String field = n.f3.elementAt(i).accept(this, argu);
        if (field != null) {
            String fieldType = field.split(" ")[0];
            String fieldName = field.split(" ")[1];
            newClass.addField(fieldType, fieldName, table);
        }
      }
      
      for (int i = 0; i < n.f4.size(); i++) {
        String method = n.f4.elementAt(i).accept(this, argu);
        if (method != null) {
            String methodNameType = method.split(":")[0];
            String methodType = methodNameType.split(" ")[0];
            String methodName = methodNameType.split(" ")[1];
            String methodParams = method.split(":")[1];
            String methodVars = "";
            if (method.split(":").length == 3) {
                methodVars = method.split(":")[2];
            } 
            newClass.addMethod(methodType, methodName, methodParams, methodVars, table);
        }
      }
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
        Class parentClass = this.table.getClass(parent);
        if (parentClass == null) {
            throw new Exception("Parent class is not declared");
        }
        String className = n.f1.accept(this, argu);
        if (this.table.getClass(className) != null) {
            throw new Exception("Class already declared");
        }
        Class newClass = new Class(className);
        newClass.extending = parentClass;
        this.table.addClass(className, newClass);

        for (int i = 0; i < n.f5.size(); i++) {
            String field = n.f5.elementAt(i).accept(this, argu);
            if (field != null) {
                String fieldType = field.split(" ")[0];
                String fieldName = field.split(" ")[1];

                newClass.addField(fieldType, fieldName, table);
            }
        }
        
        for (int i = 0; i < n.f6.size(); i++) {
            String method = n.f6.elementAt(i).accept(this, argu);
            if (method != null) {
                String methodNameType = method.split(":")[0];
                String methodType = methodNameType.split(" ")[0];
                String methodName = methodNameType.split(" ")[1];
                String methodParams = method.split(":")[1];
                String methodVars = "";
                if (method.split(":").length == 3) {
                    methodVars = method.split(":")[2];
                } 
                
                newClass.addMethod(methodType, methodName, methodParams, methodVars, table);
            }
        }

        return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Void argu) throws Exception {
        String fieldType = n.f0.accept(this, argu);
        String fieldName = n.f1.accept(this, argu);
        return fieldType + " " + fieldName;
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
        String methodParams = n.f4.accept(this, argu);
        String methodVars = "";

        for (int i = 0; i < n.f7.size(); i++) {
            String method = n.f7.elementAt(i).accept(this, argu);
            if (method != null) {
                if (i == 0) {
                    methodVars =  method;
                } else {
                    methodVars = methodVars + "," + method;
                }
            }
        }

        return methodType + " " + methodName + ":" + methodParams + ":" + methodVars;
   }


      /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, Void argu) throws Exception {
        String formalParam = n.f0.accept(this, argu);
        String formalParamTail = n.f1.accept(this, argu);
        return formalParam + " " + formalParamTail;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, Void argu) throws Exception {
        String paramType = n.f0.accept(this, argu);
        String paramName = n.f1.accept(this, argu);
        return paramType + " " + paramName;
   }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public String visit(FormalParameterTail n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return "," + n.f1.accept(this, argu);
   }







   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public String visit(Type n, Void argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, Void argu) throws Exception {
       return "boolean[]";
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(IntegerArrayType n, Void argu) throws Exception {
        return "int[]";
   }

   /**
    * f0 -> "boolean"
    */
   public String visit(BooleanType n, Void argu) throws Exception {
        return "boolean";
   }

   /**
    * f0 -> "int"
    */
   public String visit(IntegerType n, Void argu) throws Exception {
        return "int";
   }
    
    /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Identifier n, Void argu) throws Exception {
      return n.f0.toString();
   }

}