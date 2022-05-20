package src;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class LLVMGenerator extends GJDepthFirst<String, Class> {
    SymbolTable table;
    String LLVMCode;
    Integer register;
    Integer label = 0;
    Method currMethod;
    
    public String typeInLLVM(String typeInMinijava) {
        switch(typeInMinijava) {
            case "int":
                return "i32";
            case "int[]":
                return "i32*";
            case "boolean":
                return "i1";
            case "boolean[]":
                return "i1*";
            default:
                return "i8*";
        }
    }

    public Integer fieldSize(String fieldType) {
        switch(fieldType) {
            case "int":
                return 4;
            case "boolean":
                return 1;
            default:
                return 8;
        }
    }

    public Integer objectSize(String object) {
        Variable lastField = null;

        for (Map.Entry<String, Variable> entry : this.table.getClass(object).fields.entrySet()) {
            if (lastField == null || entry.getValue().offset.compareTo(lastField.offset) > 0) {
                lastField = entry.getValue();
            }
        }
        Class parentClass = this.table.getClass(object).extending;
        while (parentClass != null && lastField == null) {
            for (Map.Entry<String, Variable> entry : parentClass.fields.entrySet()) {
                if (lastField == null || entry.getValue().offset.compareTo(lastField.offset) > 0) {
                    lastField = entry.getValue();
                }
            }
        }
        return lastField.offset + fieldSize(lastField.type) + 8;
    }

    public String isVar(String possibleVar, Class currClass) throws Exception {
        if (this.currMethod.variables.get(possibleVar) != null) { // first check if local variable, then field
            return this.currMethod.variables.get(possibleVar).type;
        } else if (this.currMethod.parameters.get(possibleVar) != null) {
            return this.currMethod.parameters.get(possibleVar).type;
        } else if (currClass.fields.get(possibleVar) != null) {
            return currClass.fields.get(possibleVar).type;
        } 
        if (currClass.extending != null) {
            Class temp = currClass.extending;
            while(temp != null) {
                if (temp.fields.get(possibleVar) != null) {
                    return temp.fields.get(possibleVar).type;
                }
                temp = temp.extending;
            }
        }
        return null;
    }

    public String Accumulator(String input, Class currClass) {
        String LLVMCodeAccumulation;
        if (input.equals("true")) {
            return 1;
        }
        if (input.equals("false")) {
            return 0;
        }
        if (StringUtils.isNumeric(input)) {
            return input;
        }

        if (currClass.extending != null) {
            Class temp = currClass.extending;
            while(temp != null) {
                if (temp.fields.get(input) != null) {
                    LLVMCodeAccumulation +=	"\n\t%_" + (this.register+1)  + " = getelementptr i8, i8* %this, i32 8\n"; // offset
                    LLVMCodeAccumulation += "\t%_" + (this.register+2) + " = bitcast i8* %_" + (this.register+1) + " to " + typeInLLVM(methodType) +"*\n";
                    LLVMCodeAccumulation += "\t%_" + (this.register+3) + " = load " + typeInLLVM(methodType) +", " + typeInLLVM(methodType) +"* %_" + (this.register+2) + "\n";
                    return LLVMCodeAccumulation;
                }
                temp = temp.extending;
            }
        }

        if ((String type = isVar(input)) != null) {
            LLVMCodeAccumulation += "%_" + this.register + " = load " + typeInLLVM(type) + ", " + typeInLLVM(type) + "* %" + input;
            this.register++;
            return LLVMCodeAccumulation;
        }

        return null;
    }

    public LLVMGenerator(SymbolTable t) {
        this.table = t;
        this.LLVMCode = "";
        this.register = 0;
        this.label = 0;
        this.currMethod = null;

        Set<String> classNames = t.table.keySet();
        outerloop : for (String className : classNames) {
            Set<String> methodNames = t.table.get(className).methods.keySet();
            Integer methodNum = t.table.get(className).methods.size();
            Integer parentMethodNum = 0;
            if (t.table.get(className).extending != null) {
                Set<String> parentMethodNames = t.table.get(className).extending.methods.keySet();
                for (String methodName : parentMethodNames) {
                    if (methodNames.contains(methodName)) {
                        continue;
                    }
                    parentMethodNum++;
                }
            }
            Integer allMethodsNum = parentMethodNum + methodNum;
            if (methodNames.contains("main")) {
                this.LLVMCode += "@." + className + "_vtable = global [0 x i8*] [";
            } else {
                this.LLVMCode += "@." + className + "_vtable = global [" + allMethodsNum + " x i8*] [";
            }

            Integer i = 0;
            for (String methodName : methodNames) {
                if (methodName == "main") {
                    this.LLVMCode += "]\n\n";
                    continue outerloop;
                }
                if (i == 0) {
                    this.LLVMCode += "\n";
                }
                this.LLVMCode += "\ti8* bitcast (i32 (i8*"; 
                Set<String> methodParams = t.table.get(className).methods.get(methodName).parameters.keySet();
                for (String methodParam : methodParams) {
                    this.LLVMCode += ", " + typeInLLVM(t.table.get(className).methods.get(methodName).parameters.get(methodParam).type);
                }
                
                this.LLVMCode += ")* @" + className + "." + methodName + " to i8*)";
                i++;
                if (i < methodNum) {
                    this.LLVMCode += ",\n";
                }
            }
            int j = 0;
            if (t.table.get(className).extending != null) {
                Set<String> parentMethodNames = t.table.get(className).extending.methods.keySet();
                for (String methodName : parentMethodNames) {
                    if (methodNames.contains(methodName)) {
                        continue;
                    }
                    if (i > 0) {
                        this.LLVMCode += ",";
                    }
                    if (j == 0) {
                        this.LLVMCode += "\n";
                    }

                    this.LLVMCode += "\ti8* bitcast (i32 (i8*"; 
                    Set<String> methodParams = t.table.get(className).extending.methods.get(methodName).parameters.keySet();
                    for (String methodParam : methodParams) {
                        this.LLVMCode += ", " + typeInLLVM(t.table.get(className).extending.methods.get(methodName).parameters.get(methodParam).type);
                    }
                    this.LLVMCode += ")* @" + t.table.get(className).extending.name + "." + methodName + " to i8*)";
                    j++;
                    if (j < parentMethodNum) {
                        this.LLVMCode += ",\n";
                    }
                }
            }
            this.LLVMCode += "\n]\n\n";
        }

        this.LLVMCode += "declare i8* @calloc(i32, i32)\n";
        this.LLVMCode += "declare i32 @printf(i8*, ...)\n";
        this.LLVMCode += "declare void @exit(i32)\n\n";
        this.LLVMCode += "@_cint = constant [4 x i8] c" + "\"%d\\0a\\00\"" + "\n";
        this.LLVMCode += "@_cOOB = constant [15 x i8] c" + "\"Out of bounds\\0a\\00\"" + "\n";
        this.LLVMCode += "@_cNSZ = constant [15 x i8] c" + "\"Negative size\\0a\\00\"" + "\n";
        this.LLVMCode += "\ndefine void @print_int(i32 %i) {\n\t%_str = bitcast [4 x i8]* @_cint to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n\tret void\n}\n\ndefine void @throw_oob() {\n\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n\ndefine void @throw_nsz() {\n\t%_str = bitcast [15 x i8]* @_cNSZ to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n";

        // System.out.println(this.LLVMCode);

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
   public String visit(MainClass n, Class argu) throws Exception {
        String LLVMCodeAccumulation = "define i32 @main() {\n";
        String className = n.f1.accept(this, argu);
        Class classInstance = this.table.getClass(className);
        this.currMethod = classInstance.methods.get("main");
        for (int i = 0; i < n.f14.size(); i++) {
          LLVMCodeAccumulation += n.f14.elementAt(i).accept(this, classInstance);
        }
        for (int i = 0; i < n.f15.size(); i++) {
          LLVMCodeAccumulation += n.f15.elementAt(i).accept(this, classInstance);
        }
        LLVMCodeAccumulation += "\tret i32 0\n";
        LLVMCodeAccumulation += "\n}";
        System.out.println(LLVMCodeAccumulation);
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
   public String visit(ClassDeclaration n, Class argu) throws Exception {
      String className = n.f1.accept(this, argu);
      Class classInstance = this.table.getClass(className);
      String LLVMCodeAccumulation = "";
      for (int i = 0; i < n.f4.size(); i++) {
        LLVMCodeAccumulation += n.f4.elementAt(i).accept(this, classInstance);
      }
      System.out.println(LLVMCodeAccumulation);
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
   public String visit(ClassExtendsDeclaration n, Class argu) throws Exception {
        String parent = n.f3.accept(this, argu);
        String className = n.f1.accept(this, argu);
        Class classInstance = this.table.getClass(className);
        String LLVMCodeAccumulation = "";
        for (int i = 0; i < n.f6.size(); i++) {
            LLVMCodeAccumulation += n.f6.elementAt(i).accept(this, classInstance);
        }
        System.out.println(LLVMCodeAccumulation);
        return null;
   }


   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Class argu) throws Exception {
        String fieldType = n.f0.accept(this, argu);
        String fieldName = n.f1.accept(this, argu);
        return  "\t%"+ fieldName + " = alloca " + typeInLLVM(fieldType) + "\n";
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
   public String visit(MethodDeclaration n, Class argu) throws Exception {
        this.register = 0;
        String methodType = n.f1.accept(this, argu);
        String methodName = n.f2.accept(this, argu);
        this.currMethod = argu.methods.get(methodName);
        String methodParams = n.f4.accept(this, argu);
        n.f10.accept(this, argu);
        String LLVMCodeAccumulation = "";

        if (methodParams == null) {
            methodParams = "i8* %this";
        }
        LLVMCodeAccumulation += "define "+ typeInLLVM(methodType) +" @"+ argu.name + "." + methodName +"(" + methodParams + ") {\n";
       
        String[] paramsList = methodParams.split(",");
        Integer i = 0;
        for (String param : paramsList) {

            if (i == 0) {
                i++;
                continue;
            }
            LLVMCodeAccumulation += "\t%" + ((param.trim()).split(" ")[1]).split("\\.")[1] + " = alloca " +  (param.trim()).split(" ")[0] + "\n";
            LLVMCodeAccumulation += "\tstore " + (param.trim()).split(" ")[0] + " %." + ((param.trim()).split(" ")[1]).split("\\.")[1] + ", " +  (param.trim()).split(" ")[0] + "* " + "%" + ((param.trim()).split(" ")[1]).split("\\.")[1] + "\n";
            i++;
        }
        
        for (i = 0; i < n.f7.size(); i++) {
           LLVMCodeAccumulation += n.f7.elementAt(i).accept(this, argu);
        }
        for (i = 0; i < n.f8.size(); i++) {
           LLVMCodeAccumulation += n.f8.elementAt(i).accept(this, argu);
        }
        Integer toReturn = this.register-1;

        LLVMCodeAccumulation += "\n\tret " + typeInLLVM(methodType)+ " %_" + toReturn;
        LLVMCodeAccumulation += "\n}\n";
        return LLVMCodeAccumulation;
   }
    
   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, Class argu) throws Exception {
        String formalParam = n.f0.accept(this, argu);
        String formalParamTail = n.f1.accept(this, argu);
        if (formalParam != null) {
            if (formalParamTail != null) {
                return "i8* %this, " + formalParam + formalParamTail;
            } else {
                return "i8* %this, " + formalParam;
            }
        }
        return null;
   }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public String visit(FormalParameterTail n, Class argu) throws Exception {
      return n.f0.accept(this, argu);
   }


   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public String visit(FormalParameterTerm n, Class argu) throws Exception {
        return "," + n.f1.accept(this, argu);
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, Class argu) throws Exception {
        String paramType = n.f0.accept(this, argu);
        String paramName = n.f1.accept(this, argu);
        return typeInLLVM(paramType) + " %." + paramName;
   }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public String visit(Type n, Class argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, Class argu) throws Exception {
       return "boolean[]";
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(IntegerArrayType n, Class argu) throws Exception {
        return "int[]";
   }

   /**
    * f0 -> "boolean"
    */
   public String visit(BooleanType n, Class argu) throws Exception {
        return "boolean";
   }

   /**
    * f0 -> "int"
    */
   public String visit(IntegerType n, Class argu) throws Exception {
        return "int";
   }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, Class argu) throws Exception {
        return n.f0.toString();
    }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Class argu) throws Exception {
        String identifier = n.f0.accept(this, argu);
        String expr = n.f2.accept(this, argu);
        Integer newObject = this.register;
        if (isVar(expr, argu) != null) {
            expr = "\t%_" + (this.register) + " = load " + typeInLLVM(isVar(identifier, argu)) + ", " + typeInLLVM(isVar(identifier, argu)) + "* %" + expr + "\n";
            this.register++;
        } else {
            newObject = this.register - 3;
        }
        Set<String> classFields = argu.fields.keySet();
        for (String field : classFields) {
            if (field.equals(identifier)) {
                expr += "\t%_" +(this.register)+" = getelementptr i8, i8* %this, i32 8\n";
                this.register++;
                expr += "\t%_" +(this.register)+" = bitcast i8* %_1 to i32*";
                return expr + "\n\tstore " + typeInLLVM(isVar(identifier, argu)) + " %_" + newObject + ", " + typeInLLVM(isVar(identifier, argu)) +"* %" + (this.register) + "\n";
            }
        }
        return expr + "\n\tstore " + typeInLLVM(isVar(identifier, argu)) + " %_" + newObject + ", " + typeInLLVM(isVar(identifier, argu)) +"* %" + identifier + "\n";
   }

//    /**
//     * f0 -> Identifier()
//     * f1 -> "["
//     * f2 -> Expression()
//     * f3 -> "]"
//     * f4 -> "="
//     * f5 -> Expression()
//     * f6 -> ";"
//     */
//    public String visit(ArrayAssignmentStatement n, Class argu) throws Exception {
//         String identifier = n.f0.accept(this, argu);
//         String expr = n.f2.accept(this, argu);
//         String expr2 = n.f5.accept(this, argu);
//         return null;
//    }

//    /**
//     * f0 -> "if"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     * f5 -> "else"
//     * f6 -> Statement()
//     */
//    public String visit(IfStatement n, Class argu) throws Exception {
//         String expr = n.f2.accept(this, argu);
//         String stmt = n.f4.accept(this, argu);
//         String elseStmt = n.f6.accept(this, argu);
//         String LLVMCodeAccumulation = "";
//         LLVMCodeAccumulation += "br i1 " + expr + ", label %if_then_" + (this.label) + ", label %if_else_" + (this.label) + "\n\n";

//         LLVMCodeAccumulation += "%if_then_" + (this.label) + ":\n"
//         LLVMCodeAccumulation += stmt + "\n"
//         LLVMCodeAccumulation += "br label %if_end_" + (this.label) + "\n";

//         LLVMCodeAccumulation += "%if_else_" + (this.label) + ":\n"
//         LLVMCodeAccumulation += elseStmt + "\n"
//         LLVMCodeAccumulation += "br label %if_end_" + (this.label) + "\n";

//         LLVMCodeAccumulation += "%if_end_" + (this.label) + ":\n"
//         this.label++;
//         return LLVMCodeAccumulation;
//    }

//    /**
//     * f0 -> "while"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     */
//    public String visit(WhileStatement n, Class argu) throws Exception {
//         String expr = n.f2.accept(this, argu);
//         String stmt = n.f4.accept(this, argu);
//         String LLVMCodeAccumulation = "";
//         LLVMCodeAccumulation += "br label %loopstart" + (this.label) + "\n";
//         LLVMCodeAccumulation += "loopstart" + (this.label) + " :\n";
//         LLVMCodeAccumulation += "%fin"+ (this.label) + " = " + expr;
//         LLVMCodeAccumulation += "br i1 %fin, label %next"+ (this.label) +", label %end"+ (this.label) + "\n"
//         LLVMCodeAccumulation += "next" + (this.label) + ":\n"
//         LLVMCodeAccumulation +=  "\t" + stmt;
//         LLVMCodeAccumulation += "br label %loopstart" + (this.label) + "\n\n";
//         LLVMCodeAccumulation += "end" + (this.label) + ":\n"
//         this.label++;
//         return LLVMCodeAccumulation;
//    }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public String visit(PrintStatement n, Class argu) throws Exception {
        String expr = n.f2.accept(this, argu);
        return expr + "\tcall void (i32) @print_int(i32 %_" + (this.register++) + ")\n";
   }

//    /**
//     * f0 -> Clause()
//     * f1 -> "&&"
//     * f2 -> Clause()
//     */
//    public String visit(AndExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         this.register++;
//         return "%_" + (this.register - 1) + " = and i32 " + first + ", " + second;
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "<"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(CompareExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         this.register++;
//         return "%_" + (this.register - 1) + " = icmp slt i32 " + first + ", " + second;
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "+"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(PlusExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         this.register++;
//         return "%_" + (this.register - 1) + " = add i32 " + first + ", " + second;
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "-"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(MinusExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         this.register++;
//         return "%_" + (this.register - 1) + " = sub i32 " + first + ", " + second;
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "*"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(TimesExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         this.register++;
//         return "%_" + (this.register - 1) + " = mul i32 " + first + ", " + second;
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "["
//     * f2 -> PrimaryExpression()
//     * f3 -> "]"
//     */
//    public String visit(ArrayLookup n, Class argu) throws Exception {
//        String expr = n.f0.accept(this, argu);
//        String expr2 = n.f2.accept(this, argu);
//        if (expr.equals("int[]")) {
//            if (expr2.equals("int")) {
//                return "int";
//            } else {
//                String type2 = isVar(expr2, argu);
//                if (type2.equals("int")) {
//                    return "int";
//                } else {
//                     throw new Exception("Index should be integer");
//                }
//            }
//        } else if (expr.equals("boolean[]")) {
//            if (expr2.equals("int")) {
//                return "boolean";
//            } else {
//                String type2 = isVar(expr2, argu);
//                if (type2.equals("int")) {
//                    return "boolean";
//                } else {
//                     throw new Exception("Index should be integer");
//                }
//            }
//        } else {
//            String type = isVar(expr, argu);
//             if (type.equals("int[]")) {
//                if (expr2.equals("int")) {
//                     return "int";
//                 } else {
//                     String type2 = isVar(expr2, argu);
//                     if (type2.equals("int")) {
//                         return "int";
//                     } else {
//                             throw new Exception("Index should be integer");
//                     }
//                 }
//             } else if (type.equals("boolean[]")) {
//                if (expr2.equals("int")) {
//                     return "boolean";
//                 } else {
//                     String type2 = isVar(expr2, argu);
//                     if (type2.equals("int")) {
//                         return "int";
//                     } else {
//                             throw new Exception("Index should be integer");
//                     }
//                 }
//             } else {
//                 throw new Exception("Cannot index a non array Class");
//             }
//        }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> "length"
//     */
//    public String visit(ArrayLength n, Class argu) throws Exception {
//         String expr = n.f0.accept(this, argu);
//         if (!expr.equals("int[]") && !expr.equals("boolean[]")) { // basic array type
//             String type = isVar(expr, argu);    // identifier of basic array type
//             if (!type.equals("int[]") && !type.equals("boolean[]")) {
//                 throw new Exception("Cannot find the length of a non array Class");
//             }
//         }
//         return "int";
//    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public String visit(MessageSend n, Class argu) throws Exception {
        String expr = n.f0.accept(this, argu);
        String method = n.f2.accept(this, argu);
        String args = n.f4.accept(this, argu);

        String LLVMCodeAccumulation = "";
        Integer first = this.register;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = load i8*, i8** %" + expr + "\n";
        this.register++;
        Integer second = this.register;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = bitcast i8* %_" + first +" to i8***" + "\n";
        this.register++;
        Integer third = this.register;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = load i8**, i8*** %_" + second + "\n";
        this.register++;
        Integer forth = this.register;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = getelementptr i8*, i8** %_" + third +", i32 0" + "\n";
        this.register++;
        Integer fifth = this.register;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = load i8*, i8** %_" + forth + "\n";
        this.register++;
        Integer sixth = this.register;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = bitcast i8* %_" + fifth + " to i32 (i8*,i32)*\n";
        this.register++;
        Integer seventh = this.register - 6;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = call i32 %_" + sixth + "(i8* %_" + seventh  + ", i32 " + args +")\n";
        return LLVMCodeAccumulation;
   }

   /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
   public String visit(ExpressionList n, Class argu) throws Exception {
       return n.f0.accept(this, argu) + n.f1.accept(this, argu);
   }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
   public String visit(ExpressionTail n, Class argu) throws Exception {
      String tail = "";
        for (int i = 0; i < n.f0.size(); i++) {
            String param = n.f0.elementAt(i).accept(this, argu);
            tail = tail + param;
        }
        return tail;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public String visit(ExpressionTerm n, Class argu) throws Exception {
       return "," + n.f1.accept(this, argu);
   }


   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Class argu) throws Exception {
      return n.f0.toString();
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, Class argu) throws Exception {
      return n.f0.toString();
   }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, Class argu) throws Exception {
      return n.f0.toString();
   }


//    /**
//     * f0 -> "new"
//     * f1 -> "boolean"
//     * f2 -> "["
//     * f3 -> Expression()
//     * f4 -> "]"
//     */
//    public String visit(BooleanArrayAllocationExpression n, Class argu) throws Exception {
//         String expr = n.f3.accept(this, argu);
//         String LLVMCodeAccumulation = "";
//         LLVMCodeAccumulation += "%_" + this.register + " = add i32 1, 2\n"
//         this.register++;
//         LLVMCodeAccumulation += "%_" + this.register + " = icmp sge i32 %_0, 1\n"
//         LLVMCodeAccumulation += "br i1 %_" + this.register + ", label %nsz_ok_"+ this.label+ ", label %nsz_err_"+ this.label+ "\n"
//         LLVMCodeAccumulation += "nsz_err_"+ this.label+ ":\n"
//         LLVMCodeAccumulation += "call void @throw_nsz()\n"
//         LLVMCodeAccumulation += "br label %nsz_ok_"+ this.label+ "\n"
//         LLVMCodeAccumulation += "nsz_ok_"+ this.label+ ":\n"
//         this.label++;
//         LLVMCodeAccumulation += (this.register) + " = call " + typeInLLVM("boolean[]") + " @calloc(i32 " + expr + ", i32 1)";
//         LLVMCodeAccumulation += "%_" + (this.register++) + " = bitcast i8* %_" + (this.register) + " to i8***";
//         LLVMCodeAccumulation += "%_" + (this.register++) + " = getelementptr [2 x i8*], [2 x i8*]* @." + argu.name + "_vtable, i32 0, i32 0";
//         LLVMCodeAccumulation += "store i8** %_" + (this.register++) + ", i8*** %_" + (this.register++) + "";
//         return LLVMCodeAccumulation;
//    }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(IntegerArrayAllocationExpression n, Class argu) throws Exception {
        String expr = n.f3.accept(this, argu); // register or number
        String LLVMCodeAccumulation = "";
        LLVMCodeAccumulation += "%_" + this.register + " = add i32 1, " + expr + "\n";
        this.register++;
        LLVMCodeAccumulation += "%_" + this.register + " = icmp sge i32 %_" + (this.register - 1) +", 1\n";
        LLVMCodeAccumulation += "br i1 %_" + (this.register - 1) + ", label %nsz_ok_"+ this.label+ ", label %nsz_err_"+ this.label+ "\n\n";
        LLVMCodeAccumulation += "nsz_err_"+ this.label+ ":\n";
        LLVMCodeAccumulation += "call void @throw_nsz()\n";
        LLVMCodeAccumulation += "br label %nsz_ok_"+ this.label+ "\n\n";
        LLVMCodeAccumulation += "nsz_ok_"+ this.label+ ":\n";
        LLVMCodeAccumulation += "%_" + (this.register) + " = call " + typeInLLVM("int[]") + " @calloc(i32 %_" + expr + ", i32 4)\n";

        this.label++;
        // LLVMCodeAccumulation += "%_" + (this.register++) + " = bitcast i8* %_" + (this.register) + " to i8***\n";
        // LLVMCodeAccumulation += "%_" + (this.register++) + " = getelementptr [2 x i8*], [2 x i8*]* @." + argu.name + "_vtable, i32 0, i32 0\n";
        // LLVMCodeAccumulation += "store i8** %_" + (this.register++) + ", i8*** %_" + (this.register++) + "\n";
        // return LLVMCodeAccumulation;
        System.out.println(LLVMCodeAccumulation);
        return null;
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public String visit(AllocationExpression n, Class argu) throws Exception {
        String type = n.f1.accept(this, argu);
        Integer prev = this.register;
        Integer next = this.register + 1;
        Integer nextnext = this.register + 2;
        String LLVMCodeAccumulation = "\t%_" + (prev) + " = call " + typeInLLVM(type) + " @calloc(i32 1, i32 " + objectSize(type) + ")\n";
        LLVMCodeAccumulation += "\t%_" + (next) + " = bitcast i8* %_" + (prev) + " to i8***\n";
        LLVMCodeAccumulation += "\t%_" + (nextnext) + " = getelementptr [2 x i8*], [2 x i8*]* @." + type + "_vtable, i32 0, i32 0\n";
        LLVMCodeAccumulation += "\tstore i8** %_" + (nextnext) + ", i8*** %_" + (next) + "";
        this.register += 3;
		return LLVMCodeAccumulation;
   }

//    /**
//     * f0 -> "!"
//     * f1 -> Clause()
//     */
//    public String visit(NotExpression n, Class argu) throws Exception {
//         String clause = n.f1.accept(this, argu);
//         this.register++;
//         return "%_"+ (this.register-1) +" = xor i1 1, " + clause;
//    }

//     /**
//     * f0 -> "this"
//     */
//    public String visit(ThisExpression n, Class argu) throws Exception {
//         this.register += 2;
//         return "%_"+ (this.register-2) + " = bitcast i8* %this to i8***\n" + (this.register-1) + " = load i8**, i8***" + (this.register-2) + "\n";
//    }

//    /**
//     * f0 -> "("
//     * f1 -> Expression()
//     * f2 -> ")"
//     */
//    public String visit(BracketExpression n, Class argu) throws Exception {
//         String expr = n.f1.accept(this, argu);
//         if (expr.equals("int") || expr.equals("boolean") || expr.equals("int[]") || expr.equals("boolean[]") ) { // basic type
//             return expr;
//         }
//         if (this.table.getClass(expr) != null) {       // class instance
//             return this.table.getClass(expr).name;
//         } 
//         return isVar(expr, argu);   // identifier of basic type  // otherwise exception
//    }
}
