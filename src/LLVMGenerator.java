package src;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class LLVMGenerator extends GJDepthFirst<String, Class> {
    SymbolTable table;
    Method currMethod;
    String LLVMCode;

    public String typeInLLVM(String typeInMinijava) {
        switch(typeInMinijava) {
            case "int":
                return "i32";
            case "boolean":
                return "i1";
            case "int[]":
                return "i1";
            default:
                return "i8*";
        }
    }

    public LLVMGenerator(SymbolTable t) {
        this.table = t;
        this.currMethod = null;
        this.LLVMCode = "";

        Set<String> classNames = t.table.keySet();
        // for each class
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
                this.LLVMCode += "@." + className + "_vtable = global [" + allMethodsNum.toString() + " x i8*] [";
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

        System.out.println(this.LLVMCode);

    }

//     /**
//     * f0 -> "class"
//     * f1 -> Identifier()
//     * f2 -> "{"
//     * f3 -> "public"
//     * f4 -> "static"
//     * f5 -> "void"
//     * f6 -> "main"
//     * f7 -> "("
//     * f8 -> "String"
//     * f9 -> "["
//     * f10 -> "]"
//     * f11 -> Identifier()
//     * f12 -> ")"
//     * f13 -> "{"
//     * f14 -> ( VarDeclaration() )*
//     * f15 -> ( Statement() )*
//     * f16 -> "}"
//     * f17 -> "}"
//     */
//    public String visit(MainClass n, Class argu) throws Exception {
//         String className = n.f1.accept(this, argu);
//         Class classInstance = this.table.getClass(className);
//         this.currMethod = classInstance.methods.get("main");
//         n.f11.accept(this, classInstance);
//         for (int i = 0; i < n.f14.size(); i++) {
//           String field = n.f14.elementAt(i).accept(this, classInstance);
//         }
//         for (int i = 0; i < n.f15.size(); i++) {
//           String method = n.f15.elementAt(i).accept(this, classInstance);
//         }
//         return null;
//    }

//    /**
//     * f0 -> "class"
//     * f1 -> Identifier()
//     * f2 -> "{"
//     * f3 -> ( VarDeclaration() )*
//     * f4 -> ( MethodDeclaration() )*
//     * f5 -> "}"
//     */
//    public String visit(ClassDeclaration n, Class argu) throws Exception {
//       String className = n.f1.accept(this, argu);
//       Class classInstance = this.table.getClass(className);
//       for (int i = 0; i < n.f3.size(); i++) {
//         String field = n.f3.elementAt(i).accept(this, classInstance);
//       }
//       for (int i = 0; i < n.f4.size(); i++) {
//         String method = n.f4.elementAt(i).accept(this, classInstance);
//       }
//       return null;
//    }

//    /**
//     * f0 -> "class"
//     * f1 -> Identifier()
//     * f2 -> "extends"
//     * f3 -> Identifier()
//     * f4 -> "{"
//     * f5 -> ( VarDeclaration() )*
//     * f6 -> ( MethodDeclaration() )*
//     * f7 -> "}"
//     */
//    public String visit(ClassExtendsDeclaration n, Class argu) throws Exception {
//         String parent = n.f3.accept(this, argu);
//         String className = n.f1.accept(this, argu);
//         Class classInstance = this.table.getClass(className);
//         for (int i = 0; i < n.f5.size(); i++) {
//             String field = n.f5.elementAt(i).accept(this, classInstance);
//         }
//         for (int i = 0; i < n.f6.size(); i++) {
//             String method = n.f6.elementAt(i).accept(this, classInstance);
//         }
//         return null;
//    }


//    /**
//     * f0 -> Type()
//     * f1 -> Identifier()
//     * f2 -> ";"
//     */
//    public String visit(VarDeclaration n, Class argu) throws Exception {
//         String fieldType = n.f0.accept(this, argu);
//         validType(fieldType);
//         String fieldName = n.f1.accept(this, argu);
//         return fieldType + " " + fieldName;
//    }

//    /**
//     * f0 -> "public"
//     * f1 -> Type()
//     * f2 -> Identifier()
//     * f3 -> "("
//     * f4 -> ( FormalParameterList() )?
//     * f5 -> ")"
//     * f6 -> "{"
//     * f7 -> ( VarDeclaration() )*
//     * f8 -> ( Statement() )*
//     * f9 -> "return"
//     * f10 -> Expression()
//     * f11 -> ";"
//     * f12 -> "}"
//     */
//    public String visit(MethodDeclaration n, Class argu) throws Exception {
//         String methodType = n.f1.accept(this, argu);
//         validType(methodType);
//         String methodName = n.f2.accept(this, argu);
//         this.currMethod = argu.methods.get(methodName);
//         String methodParams = n.f4.accept(this, argu);

//         for (int i = 0; i < n.f7.size(); i++) {
//             String method = n.f7.elementAt(i).accept(this, argu);
//         }
//         for (int i = 0; i < n.f8.size(); i++) {
//             String method = n.f8.elementAt(i).accept(this, argu);
//         }
//         String returnType = n.f10.accept(this, argu);
//         if (returnType.equals("int") || returnType.equals("boolean") || returnType.equals("int[]") || returnType.equals("boolean[]")) {
//             if (!returnType.equals(argu.methods.get(methodName).returnType)) {
//                 throw new Exception("Return type is different from Method type");
//             }
//         }
//         if (this.table.getClass(returnType) != null) {
//             if (!returnType.equals(argu.methods.get(methodName).returnType)) {
//                 throw new Exception("Return type is different from Method type");
//             }
//         }
//         if (!returnType.equals(argu.methods.get(methodName).returnType) && !(isVar(returnType, argu)).equals(argu.methods.get(methodName).returnType)) {
//             throw new Exception("Return type is different from Method type");
//         }

//         return null;
//    }
    
//    /**
//     * f0 -> FormalParameter()
//     * f1 -> FormalParameterTail()
//     */
//    public String visit(FormalParameterList n, Class argu) throws Exception {
//         String formalParam = n.f0.accept(this, argu);
//         String formalParamTail = n.f1.accept(this, argu);
//         return formalParam + formalParamTail;
//    }

//    /**
//     * f0 -> ( FormalParameterTerm() )*
//     */
//    public String visit(FormalParameterTail n, Class argu) throws Exception {
//       return n.f0.accept(this, argu);
//    }


//    /**
//     * f0 -> ","
//     * f1 -> FormalParameter()
//     */
//    public String visit(FormalParameterTerm n, Class argu) throws Exception {
//         return "," + n.f1.accept(this, argu);
//    }

//    /**
//     * f0 -> Type()
//     * f1 -> Identifier()
//     */
//    public String visit(FormalParameter n, Class argu) throws Exception {
//         String paramType = n.f0.accept(this, argu);
//         validType(paramType);
//         String paramName = n.f1.accept(this, argu);
//         return paramType + " " + paramName;
//    }

//     /**
//     * f0 -> ArrayType()
//     *       | BooleanType()
//     *       | IntegerType()
//     *       | Identifier()
//     */
//    public String visit(Type n, Class argu) throws Exception {
//       return n.f0.accept(this, argu);
//    }

//    /**
//     * f0 -> "boolean"
//     * f1 -> "["
//     * f2 -> "]"
//     */
//    public String visit(BooleanArrayType n, Class argu) throws Exception {
//        return "boolean[]";
//    }

//    /**
//     * f0 -> "int"
//     * f1 -> "["
//     * f2 -> "]"
//     */
//    public String visit(IntegerArrayType n, Class argu) throws Exception {
//         return "int[]";
//    }

//    /**
//     * f0 -> "boolean"
//     */
//    public String visit(BooleanType n, Class argu) throws Exception {
//         return "boolean";
//    }

//    /**
//     * f0 -> "int"
//     */
//    public String visit(IntegerType n, Class argu) throws Exception {
//         return "int";
//    }

//     /**
//     * f0 -> <IDENTIFIER>
//     */
//     public String visit(Identifier n, Class argu) throws Exception {
//         return n.f0.toString();
//     }

//    /**
//     * f0 -> Identifier()
//     * f1 -> "="
//     * f2 -> Expression()
//     * f3 -> ";"
//     */
//    public String visit(AssignmentStatement n, Class argu) throws Exception {
//         String identifier = n.f0.accept(this, argu);
//         String expr = n.f2.accept(this, argu);
//         if (identifier.equals(expr)) {
//             return null;
//         }
//         if (this.table.getClass(expr) != null) {
//             if (isVar(identifier, argu).equals(this.table.getClass(expr).name)) { // same class type
//                 return null;
//             }
//             Class temp = this.table.getClass(expr).extending;
//             while (temp != null) {
//                 if (temp.equals(isVar(identifier, argu))) { // parent and child
//                     return null;
//                 }
//                 temp = temp.extending;
//             }
//         }
//         String identifierType = isVar(identifier, argu);
//         Class temp = null;
//         if (this.table.getClass(expr) != null) {
//             temp = this.table.getClass(expr).extending;
//         }

//         if (identifierType.equals(expr)) {
//             return null;
//         } else if (temp != null) {
//             while (temp != null) {
//                 if (identifierType.equals(temp.name)) { // parent has this method
//                     return null;
//                 }
//                 temp = temp.extending;
//             }
//             throw new Exception("Cannot assign to a variable of different type");
//         } else {
//             String exprType = isVar(expr, argu);
//             if (identifierType.equals(exprType)) {
//                 return null;
//             } else {
//                 temp = this.table.getClass(exprType).extending;
//                 while (temp != null) {
//                     if (identifierType.equals(temp.name)) { // parent has this method
//                         return null;
//                     }
//                     temp = temp.extending;
//                 }
//                 throw new Exception("Cannot assign to a variable of different type");
//             }
//         }
//    }

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
//         if (!expr.equals("int") && !isVar(expr, argu).equals("int")) {
//             throw new Exception("Index should be integer");
//         }
//         String identifierType = isVar(identifier, argu);
//         if (!identifierType.equals("int[]") && !identifierType.equals("boolean[]")) {
//             throw new Exception("Cannot index a non array Class");
//         }
//         if (identifierType.equals(expr2+"[]")) {
//             return null;
//         }
//         String exprType = isVar(expr2, argu);
//         if (!exprType.equals(identifierType.replaceAll("[^A-Za-z0-9]",""))) {
//             throw new Exception("Types don't match");
//         }
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
//         if (!expr.equals("boolean") && !isVar(expr, argu).equals("boolean")) {
//             throw new Exception("If statement requires a boolean");
//         }
//         String stmt = n.f4.accept(this, argu);
//         String elseStmt = n.f6.accept(this, argu);
//         return null;
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
//         if (!expr.equals("boolean") && !isVar(expr, argu).equals("boolean")) {
//             throw new Exception("While statement requires a boolean");
//         }
//         String stmt = n.f4.accept(this, argu);
//         return null;
//    }

//    /**
//     * f0 -> "System.out.println"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> ";"
//     */
//    public String visit(PrintStatement n, Class argu) throws Exception {
//         String expr = n.f2.accept(this, argu);
//         if (expr.equals("int") || expr.equals("boolean")) {
//             return expr;
//         }
//         String type = isVar(expr, argu);
//         if (!type.equals("int") && !type.equals("boolean")) {
//             throw new Exception("Cannot print this type of data");
//         }
//         return type;
//    }

//    /**
//     * f0 -> Clause()
//     * f1 -> "&&"
//     * f2 -> Clause()
//     */
//    public String visit(AndExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first.equals("boolean")) {
//             if (second.equals("boolean")) {
//                 return "boolean";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2.equals("boolean")) {
//                     return "boolean";
//                 } else {
//                     throw new Exception("Cannot use logical operators on non boolean data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type.equals("boolean")) {
//                 return "boolean";
//             } else {
//                 throw new Exception("Cannot use logical operators on non boolean data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "<"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(CompareExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first.equals("int")) {
//             if (second.equals("int")) {
//                 return "boolean";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2.equals("int")) {
//                     return "boolean";
//                 } else {
//                     throw new Exception("Cannot use logical operators on non boolean data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type.equals("int")) {
//                 return "boolean";
//             } else {
//                 throw new Exception("Cannot use arithmetic operators on non integer data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "+"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(PlusExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first.equals("int")) {
//             if (second.equals("int")) {
//                 return "int";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2.equals("int")) {
//                     return "int";
//                 } else {
//                     throw new Exception("Cannot use logical operators on non boolean data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type.equals("int")) {
//                 return "int";
//             } else {
//                 throw new Exception("Cannot use arithmetic operators on non integer data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "-"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(MinusExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first.equals("int")) {
//             if (second.equals("int")) {
//                 return "int";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2.equals("int")) {
//                     return "int";
//                 } else {
//                     throw new Exception("Cannot use logical operators on non boolean data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type.equals("int")) {
//                 return "int";
//             } else {
//                 throw new Exception("Cannot use arithmetic operators on non integer data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "*"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(TimesExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first.equals("int")) {
//             if (second.equals("int")) {
//                 return "int";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2.equals("int")) {
//                     return "int";
//                 } else {
//                     throw new Exception("Cannot use arithmetic operators on non integer data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type.equals("int")) {
//                 return "int";
//             } else {
//                 throw new Exception("Cannot use arithmetic operators on non integer data");
//             }
//         }
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

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> Identifier()
//     * f3 -> "("
//     * f4 -> ( ExpressionList() )?
//     * f5 -> ")"
//     */
//    public String visit(MessageSend n, Class argu) throws Exception {
//         String expr = n.f0.accept(this, argu);
//         String method = n.f2.accept(this, argu);
//         String args = n.f4.accept(this, argu);

//         if (this.table.getClass(expr) == null) { // not a class name but a class identifier
//             if (this.table.getClass(isVar(expr, argu)) != null) {   // get corresponding class
//                 if (this.table.getClass(isVar(expr, argu)).methods.get(method) == null) {
//                     Class temp = this.table.getClass(isVar(expr, argu));
//                     while (temp != null) {
//                         if (temp.methods.get(method) != null) { // parent has this method
//                             checkArgs(args, temp.methods.get(method), argu);
//                             return temp.methods.get(method).returnType;
//                         }
//                         temp = temp.extending;
//                     }
//                     throw new Exception("No such class method");
                    
//                 } else if (this.table.getClass(isVar(expr, argu)).methods.get(method) != null) { 
//                     checkArgs(args, this.table.getClass(isVar(expr, argu)).methods.get(method), argu);
//                     return this.table.getClass(isVar(expr, argu)).methods.get(method).returnType;
//                 }
//             }
//             throw new Exception("No such class");
//         } else if (this.table.getClass(expr).methods.get(method) == null) {
//             throw new Exception("No such class method");
//         } else if (this.table.getClass(expr).methods.get(method) != null) { // found a class with this method 
//             checkArgs(args, this.table.getClass(expr).methods.get(method), argu);
//             return this.table.getClass(expr).methods.get(method).returnType;

//         }
//         return null;
//    }

//    /**
//     * f0 -> Expression()
//     * f1 -> ExpressionTail()
//     */
//    public String visit(ExpressionList n, Class argu) throws Exception {
//        return n.f0.accept(this, argu) + n.f1.accept(this, argu);
//    }

//    /**
//     * f0 -> ( ExpressionTerm() )*
//     */
//    public String visit(ExpressionTail n, Class argu) throws Exception {
//       String tail = "";
//         for (int i = 0; i < n.f0.size(); i++) {
//             String param = n.f0.elementAt(i).accept(this, argu);
//             tail = tail + param;
//         }
//         return tail;
//    }

//    /**
//     * f0 -> ","
//     * f1 -> Expression()
//     */
//    public String visit(ExpressionTerm n, Class argu) throws Exception {
//        return "," + n.f1.accept(this, argu);
//    }

//    /**
//     * f0 -> AndExpression()
//     *       | CompareExpression()
//     *       | PlusExpression()
//     *       | MinusExpression()
//     *       | TimesExpression()
//     *       | ArrayLookup()
//     *       | ArrayLength()
//     *       | MessageSend()
//     *       | Clause()
//     */
//    public String visit(Expression n, Class argu) throws Exception {
//       return n.f0.accept(this, argu);
//    }


//    /**
//     * f0 -> <INTEGER_LITERAL>
//     */
//    public String visit(IntegerLiteral n, Class argu) throws Exception {
//       return "int";
//    }

//    /**
//     * f0 -> "true"
//     */
//    public String visit(TrueLiteral n, Class argu) throws Exception {
//       return "boolean";
//    }

//    /**
//     * f0 -> "false"
//     */
//    public String visit(FalseLiteral n, Class argu) throws Exception {
//       return "boolean";
//    }


//    /**
//     * f0 -> "new"
//     * f1 -> "boolean"
//     * f2 -> "["
//     * f3 -> Expression()
//     * f4 -> "]"
//     */
//    public String visit(BooleanArrayAllocationExpression n, Class argu) throws Exception {
//        String expr = n.f3.accept(this, argu);
//        if (!expr.equals("int") && !isVar(expr, argu).equals("int")) { // basic integer type or integer identifier
// 			throw new Exception("Index should be integer");
//        }
//        return "boolean[]";
//    }

//    /**
//     * f0 -> "new"
//     * f1 -> "int"
//     * f2 -> "["
//     * f3 -> Expression()
//     * f4 -> "]"
//     */
//    public String visit(IntegerArrayAllocationExpression n, Class argu) throws Exception {
//         String expr = n.f3.accept(this, argu);
//         if (!expr.equals("int") && !isVar(expr, argu).equals("int")) { // basic integer type or integer identifier
//             throw new Exception("Index should be integer");
//         }
//        return "int[]";
//    }

//    /**
//     * f0 -> "new"
//     * f1 -> Identifier()
//     * f2 -> "("
//     * f3 -> ")"
//     */
//    public String visit(AllocationExpression n, Class argu) throws Exception {
//         String type = n.f1.accept(this, argu);
// 		if (this.table.getClass(type) == null)  {// must be a class instance
// 			throw new Exception("No such class name");
//         }
// 		return type;
//    }

//    /**
//     * f0 -> "!"
//     * f1 -> Clause()
//     */
//    public String visit(NotExpression n, Class argu) throws Exception {
//         String clause = n.f1.accept(this, argu);
//         if (clause.equals("boolean") || isVar(clause, argu).equals("boolean")) {   // basic type or identifier of basic type
//             return "boolean";
//         } else {
//             throw new Exception("Cannot use logical operators on non boolean data");
//         }
//    }

//     /**
//     * f0 -> "this"
//     */
//    public String visit(ThisExpression n, Class argu) throws Exception {
//       return argu.name;
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
